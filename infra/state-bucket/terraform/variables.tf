# AWS vars
variable "aws_region" {
  type        = string
  default     = "eu-west-3"
  description = "AWS region of the state bucket"
}

variable "aws_profile" {
  type        = string
  default     = "default"
  description = "AWS profile"
}

variable "state_bucket" {
  type        = string
  description = "Globally unique name of the state bucket (set it in a git-ignored terraform.tfvars)"
}

variable "noncurrent_version_days" {
  type        = number
  default     = 90
  description = "Days a previous version of a state file is kept"
}
