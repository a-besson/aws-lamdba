#
# Lambda artifact bucket
#
resource "aws_s3_bucket" "lambda_bucket" {
    bucket = var.lambda_bucket
}

resource "aws_s3_object" "deploy_lambda_to_s3" {
    bucket = var.lambda_bucket
    key    = "lambda"
    source = "../target/springboot-lambda.jar"
    etag   = filemd5("../target/springboot-lambda.jar")

    depends_on = [
        aws_s3_bucket.lambda_bucket
    ]
}

#
# Lambda
#
resource "aws_lambda_function" "springboot_lambda" {
    function_name = var.lambda_function_name
    s3_bucket     = var.lambda_bucket
    s3_key        = "lambda"
    handler       = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
    runtime       = "java21"
    role          = aws_iam_role.lambda_execution_role.arn
    memory_size   = 256
    timeout       = 15

    tracing_config {
        mode = "Active"
    }

    layers = [
        "arn:aws:lambda:${var.aws_region}:580247275435:layer:LambdaInsightsExtension:14"
    ]

    environment {
        variables = {
            SPRING_CLOUD_FUNCTION_DEFINITION = "demo"
        }
    }

    depends_on = [
        aws_s3_object.deploy_lambda_to_s3,
        aws_iam_role_policy_attachment.lambda_logs_role,
        aws_cloudwatch_log_group.lambda_log_group,
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
    name                = "springboot_lambda_execution_role"
    assume_role_policy  = data.aws_iam_policy_document.lambda_policy_assume_role.json
}

#
# Lambda log group
#
resource "aws_cloudwatch_log_group" "lambda_log_group" {
    name              = "/aws/lambda/${var.lambda_function_name}-tf"
    retention_in_days = 1
}

#
# Lambda log policy
#
data "aws_iam_policy_document" "lambda_logging_policy" {
    statement {
        effect = "Allow"

        actions = [
            "logs:CreateLogGroup",
            "logs:CreateLogStream",
            "logs:PutLogEvents",
        ]

        resources = ["arn:aws:logs:*:*:*"]
    }
}

resource "aws_iam_policy" "lambda_logging_policy" {
    name        = "springboot_lambda_logging"
    path        = "/"
    description = "IAM policy for logging from a lambda"
    policy      = data.aws_iam_policy_document.lambda_logging_policy.json
}

resource "aws_iam_role_policy_attachment" "lambda_logs_role" {
    role       = aws_iam_role.lambda_execution_role.name
    policy_arn = aws_iam_policy.lambda_logging_policy.arn
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
