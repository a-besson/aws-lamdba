terraform {
    required_providers {
        aws = {
            source  = "hashicorp/aws"
            version = "~> 6.0"
        }
    }
#    backend "s3" {
#        bucket         = "cloud-public-springboot-lambda-tf-stack"
#        key            = "terraform/states/cloud-public-springboot-lambda-tf-stack"
#        region         = "eu-west-3"
#        encrypt        = "true"
#        dynamodb_table = "cloud-public-springboot-lambda-tf-stack-lock"
#    }
}

provider "aws" {
    profile = var.aws_profile
    region  = var.aws_region
}
