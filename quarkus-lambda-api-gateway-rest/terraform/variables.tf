
# AWS vars
variable "aws_region" {
  type        = string
  default     = "eu-west-3"
  description = "AWS target region"
}

variable "aws_profile" {
  type        = string
  default     = "default"
  description = "AWS profile"
}

variable "lambda_package_path" {
  type        = string
  default     = "../target/function.zip"
  description = "Lambda package built by `task <sample>:package`, relative to this directory"
}

variable "lambda_bucket" {
  type        = string
  default     = "cloud-public-quarkus-lambda-api-gateway-rest-bucket"
  description = "lambda bucket name"
}

variable "lambda_function_name" {
  type        = string
  default     = "cloud-public-quarkus-lambda-api-gateway-rest"
  description = "lambda function name, also prefixes the IAM and log group names"
}

variable "domain_name" {
  type        = string
  default     = "aws-lab-demo.com"
  description = "domain name"
}
