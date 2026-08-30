resource "aws_lambda_function_url" "springboot_lambda_url" {
    function_name      = aws_lambda_function.springboot_lambda.function_name
    authorization_type = "NONE" # authorization_type = "AWS_IAM"
}
