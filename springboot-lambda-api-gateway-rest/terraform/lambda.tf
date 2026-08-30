#
# Lambda artifact bucket
#
resource "aws_s3_bucket" "lambda_bucket" {
    bucket = var.lambda_bucket
}

resource "aws_s3_object" "deploy_lambda_to_s3" {
    bucket = var.lambda_bucket
    key    = "lambda"
    source = "../target/springboot-lambda-api-gateway-rest.jar"
    etag   = filemd5("../target/springboot-lambda-api-gateway-rest.jar")

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
    handler       = "com.springboot.lambda.rest.StreamLambdaHandler::handleRequest"
    runtime       = "java21"
    role          = aws_iam_role.lambda_execution_role.arn
    memory_size   = 512
    timeout       = 30

    tracing_config {
        mode = "Active"
    }

    layers = [
        "arn:aws:lambda:${var.aws_region}:580247275435:layer:LambdaInsightsExtension:14"
    ]

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
    name                = "springboot_rest_lambda_execution_role"
    assume_role_policy  = data.aws_iam_policy_document.lambda_policy_assume_role.json
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
