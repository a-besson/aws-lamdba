
output "aws_lambda_function" {
    value = aws_lambda_function.springboot_lambda.arn
    description = "Spring Boot Lambda ARN"
}
output "aws_lambda_function_url" {
    value = <<EOF
        Lambda URL: ${aws_lambda_function_url.springboot_lambda_url.function_url}
        curl -X POST '${aws_lambda_function_url.springboot_lambda_url.function_url}' \
             -H 'content-type: application/json' \
             -d '{ "body": "hello lambda" }'
    EOF
    description = "URL de la lambda"
}
