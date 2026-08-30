
output "aws_lambda_function" {
    value = aws_lambda_function.springboot_lambda.arn
    description = "Spring Boot Lambda ARN"
}

output "api_gtw_deployment_invoke_url" {
    description = "Deployment invoke url"
    value       = <<EOF
        curl -X GET ${aws_api_gateway_deployment.api_deploy.invoke_url}/dev/demo
        curl -X POST ${aws_api_gateway_deployment.api_deploy.invoke_url}/dev/demo
        curl -X PUT ${aws_api_gateway_deployment.api_deploy.invoke_url}/dev/demo
    EOF
}

output "custom_domain_api" {
    value = <<EOF
        curl -X GET https://${aws_api_gateway_base_path_mapping.api.domain_name}/demo
        curl -X POST https://${aws_api_gateway_base_path_mapping.api.domain_name}/demo
        curl -X PUT https://${aws_api_gateway_base_path_mapping.api.domain_name}/demo
    EOF
}
