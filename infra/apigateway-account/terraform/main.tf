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
      version = "6.68.0"
    }
  }
}

provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
}

#
# API Gateway account settings
#
# aws_api_gateway_account is a singleton per account and region: the CloudWatch Logs role it holds is shared by every
# REST API of the region. It is declared here, once, instead of in each REST stack (the last apply would overwrite the
# previous one). Apply this stack before the REST stacks.
#
data "aws_iam_policy_document" "api_gateway_assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["apigateway.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "api_gateway_account" {
  name               = var.role_name
  assume_role_policy = data.aws_iam_policy_document.api_gateway_assume_role.json
}

resource "aws_iam_role_policy_attachment" "api_gateway_account" {
  role       = aws_iam_role.api_gateway_account.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
}

resource "aws_api_gateway_account" "this" {
  cloudwatch_role_arn = aws_iam_role.api_gateway_account.arn

  depends_on = [aws_iam_role_policy_attachment.api_gateway_account]
}
