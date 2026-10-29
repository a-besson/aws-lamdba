terraform {
  required_version = "~> 1.16"

  # Partial configuration: bucket, region and key come from backend.hcl and `task <namespace>:tf:init`
  # (see backend.hcl.example). The lock is a lockfile in the bucket, no DynamoDB table.
  backend "s3" {
    encrypt      = true
    use_lockfile = true
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.70.0"
    }
    # used by terraform-aws-modules/lambda/aws
    external = {
      source  = "hashicorp/external"
      version = "2.4.2"
    }
    local = {
      source  = "hashicorp/local"
      version = "2.9.1"
    }
    null = {
      source  = "hashicorp/null"
      version = "3.3.2"
    }
  }
}

provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
}
