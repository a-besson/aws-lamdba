output "cloudwatch_role_arn" {
  description = "IAM role API Gateway uses to write its logs, for the whole region"
  value       = aws_api_gateway_account.this.cloudwatch_role_arn
}
