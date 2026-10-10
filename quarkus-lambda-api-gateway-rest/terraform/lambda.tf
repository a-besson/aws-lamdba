#
# Lambda artifact bucket
#
resource "aws_s3_bucket" "lambda_bucket" {
  bucket = var.lambda_bucket
}

resource "aws_s3_bucket_public_access_block" "lambda_bucket" {
  bucket = aws_s3_bucket.lambda_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Accepted: SSE-S3 (AES256) instead of a customer managed KMS key. This bucket only holds the public sample
# package and a CMK would add a monthly cost per key for no gain on a demo.
#trivy:ignore:AVD-AWS-0132
resource "aws_s3_bucket_server_side_encryption_configuration" "lambda_bucket" {
  bucket = aws_s3_bucket.lambda_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_object" "deploy_lambda_to_s3" {
  bucket = var.lambda_bucket
  key    = "lambda"
  source = var.lambda_package_path
  etag   = filemd5(var.lambda_package_path)

  depends_on = [
    aws_s3_bucket.lambda_bucket
  ]
}

#
# Lambda
#
resource "aws_lambda_function" "quarkus_lambda" {
  function_name = var.lambda_function_name
  s3_bucket     = var.lambda_bucket
  s3_key        = "lambda"
  handler       = "not.used.in.provided.runtime"
  runtime       = "provided.al2023"
  role          = aws_iam_role.lambda_execution_role.arn
  memory_size   = 128
  timeout       = 15

  tracing_config {
    mode = "Active"
  }

  layers = [
    "arn:aws:lambda:${var.aws_region}:580247275435:layer:LambdaInsightsExtension:14"
  ]

  environment {
    variables = {
      DISABLE_SIGNAL_HANDLERS = true
    }
  }

  depends_on = [
    aws_s3_object.deploy_lambda_to_s3,
  ]
}

#
# Lambda role
#
data "aws_iam_policy_document" "lambda_policy_assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "lambda_execution_role" {
  name               = "${var.lambda_function_name}-execution"
  assume_role_policy = data.aws_iam_policy_document.lambda_policy_assume_role.json
}

#
# Lambda Insights
#
resource "aws_iam_role_policy_attachment" "insights_policy" {
  role       = aws_iam_role.lambda_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchLambdaInsightsExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "this_xray_tracing" {
  role       = aws_iam_role.lambda_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSXRayDaemonWriteAccess"
}
