
output "aws_lambda_function" {
  value       = module.lambda.lambda_function_arn
  description = "Spring Boot Lambda ARN"
}
output "aws_lambda_function_url" {
  value       = <<EOF
        Lambda URL: ${module.lambda.lambda_function_url}
        curl -X POST '${module.lambda.lambda_function_url}' \
             -H 'content-type: application/json' \
             -d '{ "body": "hello lambda" }'
    EOF
  description = "URL de la lambda"
}

output "lambda_function_url" {
  description = "Function URL of the Lambda (read by `task invoke`)"
  value       = module.lambda.lambda_function_url
}
