
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

variable "lambda_bucket" {
    type        = string
    default     = "cloud-public-springboot-lambda-stack-bucket"
    description = "lambda bucket name"
}

variable "lambda_function_name" {
    type        = string
    default     = "cloud-public-springboot-lambda"
    description = "lambda bucket name"
}
