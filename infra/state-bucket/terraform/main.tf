terraform {
  required_version = "~> 1.16"

  # No backend on purpose: this stack creates the bucket that holds the state of the others, so its own state stays
  # local (terraform.tfstate is ignored by git). Keep that file, or re-import the bucket if it is lost.
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.69.1"
    }
  }
}

provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
}

#
# Terraform state bucket
#
# Accepted: SSE-S3 (AES256) instead of a customer managed KMS key. The state of these public samples holds no secret
# and a CMK would add a monthly cost per key.
#trivy:ignore:AVD-AWS-0132
module "state_bucket" {
  source  = "terraform-aws-modules/s3-bucket/aws"
  version = "5.17.0"

  bucket = var.state_bucket

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true

  attach_deny_insecure_transport_policy = true

  # versioning keeps the previous states, S3 native locking (use_lockfile) needs no DynamoDB table
  versioning = {
    enabled = true
  }

  server_side_encryption_configuration = {
    rule = {
      apply_server_side_encryption_by_default = {
        sse_algorithm = "AES256"
      }
    }
  }

  lifecycle_rule = [
    {
      id      = "expire-old-state-versions"
      enabled = true

      filter = {}

      noncurrent_version_expiration = {
        days = var.noncurrent_version_days
      }

      abort_incomplete_multipart_upload_days = 7
    }
  ]
}
