# Applies this stack on the Floci emulator with fake credentials (never on a real account).
# Run with `task <namespace>:tf:test`; CI runs it in the `terraform-test` job of _build-java.yml.
# It checks the wiring of the resources, not the code: the package is a placeholder zip.

variables {
  floci_endpoint      = "http://localhost:4566"
  lambda_package_path = "tests/lambda-placeholder.zip"

  # the Insights layer belongs to an AWS account, Floci cannot serve it
  lambda_insights_enabled = false
}

# Overrides the provider of main.tf: every call goes to the emulator.
provider "aws" {
  region                      = "eu-west-3"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true
  s3_use_path_style           = true

  endpoints {
    s3             = var.floci_endpoint
    iam            = var.floci_endpoint
    sts            = var.floci_endpoint
    lambda         = var.floci_endpoint
    cloudwatchlogs = var.floci_endpoint
  }
}

run "apply_on_floci" {
  command = apply

  assert {
    condition     = module.lambda.lambda_function_name == var.lambda_function_name
    error_message = "unexpected function name"
  }

  assert {
    condition     = module.lambda.lambda_function_arn != ""
    error_message = "the function was not created"
  }

  assert {
    condition     = module.lambda.lambda_cloudwatch_log_group_name == "/aws/lambda/${var.lambda_function_name}-tf"
    error_message = "the function must log to its dedicated log group"
  }

  assert {
    condition     = module.lambda_bucket.s3_bucket_id == var.lambda_bucket
    error_message = "unexpected artifact bucket"
  }

  assert {
    condition     = module.lambda.lambda_function_url != ""
    error_message = "the Function URL was not created"
  }
}
