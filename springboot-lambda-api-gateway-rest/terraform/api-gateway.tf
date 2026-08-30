resource "aws_api_gateway_rest_api" "my_api" {
    name          = "main"
    description = "my api gtw"

    binary_media_types = ["application/json"]
    endpoint_configuration {
        types = ["REGIONAL"]
    }
}

resource "aws_api_gateway_resource" "root" {
    rest_api_id = aws_api_gateway_rest_api.my_api.id
    parent_id = aws_api_gateway_rest_api.my_api.root_resource_id
    path_part = "{proxy+}"
}

resource "aws_api_gateway_method" "proxy" {
    rest_api_id = aws_api_gateway_rest_api.my_api.id
    resource_id = aws_api_gateway_resource.root.id
    http_method = "ANY"
    authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda_integration" {
    rest_api_id = aws_api_gateway_rest_api.my_api.id
    resource_id = aws_api_gateway_resource.root.id
    http_method = aws_api_gateway_method.proxy.http_method
    integration_http_method = "POST"
    type = "AWS_PROXY"
    uri = aws_lambda_function.springboot_lambda.invoke_arn
}

resource "aws_api_gateway_deployment" "api_deploy" {
    depends_on = [
        aws_api_gateway_integration.lambda_integration,
    ]
    rest_api_id = aws_api_gateway_rest_api.my_api.id

    lifecycle {
        create_before_destroy = true
    }
}

resource "aws_cloudwatch_log_group" "main_api_gw" {
    name              = "API-Gateway-Execution-Logs-${aws_api_gateway_rest_api.my_api.id}"
    retention_in_days = 1
}


resource "aws_api_gateway_stage" "stage" {
    deployment_id = aws_api_gateway_deployment.api_deploy.id
    rest_api_id   = aws_api_gateway_rest_api.my_api.id
    stage_name    = "dev"
    xray_tracing_enabled = true

    access_log_settings {
        destination_arn = aws_cloudwatch_log_group.main_api_gw.arn
        format = jsonencode(
            {
                httpMethod     = "$context.httpMethod"
                ip             = "$context.identity.sourceIp"
                protocol       = "$context.protocol"
                requestId      = "$context.requestId"
                requestTime    = "$context.requestTime"
                responseLength = "$context.responseLength"
                routeKey       = "$context.routeKey"
                status         = "$context.status"
            })
    }
}

resource "aws_api_gateway_method_settings" "all" {
    rest_api_id = aws_api_gateway_rest_api.my_api.id
    stage_name  = aws_api_gateway_stage.stage.stage_name
    method_path = "*/*"

    settings {
        logging_level = "INFO"
        data_trace_enabled = true
        metrics_enabled = true
    }
}

resource "aws_lambda_permission" "apigw_lambda" {
    statement_id = "AllowExecutionFromAPIGateway"
    action = "lambda:InvokeFunction"
    function_name = aws_lambda_function.springboot_lambda.function_name
    principal = "apigateway.amazonaws.com"

    source_arn = "${aws_api_gateway_rest_api.my_api.execution_arn}/*/*/*"
}

resource "aws_iam_role" "api_gateway_account_role" {
    name = "springboot-api-gateway-account-role"
    assume_role_policy = jsonencode({
        "Version" : "2012-10-17",
        "Statement" : [
            {
                "Sid" : "",
                "Effect" : "Allow",
                "Principal" : {
                    "Service" : "apigateway.amazonaws.com"
                },
                "Action" : "sts:AssumeRole"
            }
        ]
    })
}

resource "aws_iam_role_policy" "api_gateway_cloudwatch_policy" {
    name = "api-gateway-cloudwatch-policy"
    role = aws_iam_role.api_gateway_account_role.id
    policy = jsonencode({
        "Version" : "2012-10-17",
        "Statement" : [
            {
                "Effect" : "Allow",
                "Action" : [
                    "logs:CreateLogGroup",
                    "logs:CreateLogStream",
                    "logs:DescribeLogGroups",
                    "logs:DescribeLogStreams",
                    "logs:PutLogEvents",
                    "logs:GetLogEvents",
                    "logs:FilterLogEvents"
                ],
                "Resource" : "*"
            }
        ]
    })
}

resource "aws_api_gateway_account" "api_gateway_account" {
    cloudwatch_role_arn = aws_iam_role.api_gateway_account_role.arn
}
