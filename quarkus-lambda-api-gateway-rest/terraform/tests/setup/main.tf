# Test fixture: the public hosted zone the stack looks up with a data source. Created on Floci only.
terraform {
  required_version = "~> 1.16"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.68.0"
    }
  }
}

variable "domain_name" {
  type = string
}

resource "aws_route53_zone" "public" {
  name = var.domain_name
}
