#
# Lambda artifact bucket
#
# Accepted: SSE-S3 (AES256) instead of a customer managed KMS key. This bucket only holds the public sample
# package and a CMK would add a monthly cost per key for no gain on a demo.
#trivy:ignore:AVD-AWS-0132
module "lambda_bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "5.16.2"

  bucket = var.lambda_bucket

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true

  attach_deny_insecure_transport_policy = true

  server_side_encryption_configuration = {
    rule = {
      apply_server_side_encryption_by_default = {
        sse_algorithm = "AES256"
      }
    }
  }
}

moved {
  from = aws_s3_bucket.lambda_bucket
  to   = module.lambda_bucket.aws_s3_bucket.this[0]
}

moved {
  from = aws_s3_bucket_public_access_block.lambda_bucket
  to   = module.lambda_bucket.aws_s3_bucket_public_access_block.this[0]
}

moved {
  from = aws_s3_bucket_server_side_encryption_configuration.lambda_bucket
  to   = module.lambda_bucket.aws_s3_bucket_server_side_encryption_configuration.this[0]
}

resource "aws_s3_object" "deploy_lambda_to_s3" {
  bucket = module.lambda_bucket.s3_bucket_id
  key    = "lambda"
  source = var.lambda_package_path
  etag   = filemd5(var.lambda_package_path)
}

#
# Lambda, execution role and log group
#
module "lambda" {
  source  = "terraform-aws-modules/lambda/aws"
  version = "8.12.0"

  function_name = var.lambda_function_name
  handler       = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
  runtime       = "java21"
  memory_size   = 256
  timeout       = 15

  # the package is the object uploaded above: the module neither builds nor uploads anything
  create_package = false
  s3_existing_package = {
    bucket = module.lambda_bucket.s3_bucket_id
    key    = aws_s3_object.deploy_lambda_to_s3.key
  }

  tracing_mode = "Active"
  layers = var.lambda_insights_enabled ? [
    "arn:aws:lambda:${var.aws_region}:580247275435:layer:LambdaInsightsExtension:14"
  ] : []

  environment_variables = {
    SPRING_CLOUD_FUNCTION_DEFINITION = "demo"
  }

  create_lambda_function_url = true
  authorization_type         = "NONE" # authorization_type = "AWS_IAM"

  # log group managed by the module, the logging policy is limited to it
  logging_log_format                 = "Text"
  logging_log_group                  = "/aws/lambda/${var.lambda_function_name}-tf"
  cloudwatch_logs_retention_in_days  = 1
  attach_create_log_group_permission = false

  role_name             = "${var.lambda_function_name}-execution"
  attach_tracing_policy = true
  attach_policies       = true
  number_of_policies    = 1
  policies              = ["arn:aws:iam::aws:policy/CloudWatchLambdaInsightsExecutionRolePolicy"]
}

moved {
  from = aws_lambda_function.springboot_lambda
  to   = module.lambda.aws_lambda_function.this[0]
}

moved {
  from = aws_iam_role.lambda_execution_role
  to   = module.lambda.aws_iam_role.lambda[0]
}

moved {
  from = aws_cloudwatch_log_group.lambda_log_group
  to   = module.lambda.aws_cloudwatch_log_group.lambda[0]
}

moved {
  from = aws_lambda_function_url.springboot_lambda_url
  to   = module.lambda.aws_lambda_function_url.this[0]
}
