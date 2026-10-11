# AWS vars
variable "aws_region" {
  type        = string
  default     = "eu-west-3"
  description = "AWS target region (the API Gateway account settings are per region)"
}

variable "aws_profile" {
  type        = string
  default     = "default"
  description = "AWS profile"
}

variable "role_name" {
  type        = string
  default     = "api-gateway-cloudwatch-logs"
  description = "Name of the IAM role API Gateway assumes to write its logs to CloudWatch"
}
