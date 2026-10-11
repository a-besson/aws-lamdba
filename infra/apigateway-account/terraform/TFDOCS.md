# terraform

<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
| ---- | ------- |
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | ~> 1.16 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | 6.68.0 |

## Providers

| Name | Version |
| ---- | ------- |
| <a name="provider_aws"></a> [aws](#provider\_aws) | 6.68.0 |

## Modules

No modules.

## Resources

| Name | Type |
| ---- | ---- |
| [aws_api_gateway_account.this](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_account) | resource |
| [aws_iam_role.api_gateway_account](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role) | resource |
| [aws_iam_role_policy_attachment.api_gateway_account](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role_policy_attachment) | resource |
| [aws_iam_policy_document.api_gateway_assume_role](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
| ---- | ----------- | ---- | ------- | :------: |
| <a name="input_aws_profile"></a> [aws\_profile](#input\_aws\_profile) | AWS profile | `string` | `"default"` | no |
| <a name="input_aws_region"></a> [aws\_region](#input\_aws\_region) | AWS target region (the API Gateway account settings are per region) | `string` | `"eu-west-3"` | no |
| <a name="input_role_name"></a> [role\_name](#input\_role\_name) | Name of the IAM role API Gateway assumes to write its logs to CloudWatch | `string` | `"api-gateway-cloudwatch-logs"` | no |

## Outputs

| Name | Description |
| ---- | ----------- |
| <a name="output_cloudwatch_role_arn"></a> [cloudwatch\_role\_arn](#output\_cloudwatch\_role\_arn) | IAM role API Gateway uses to write its logs, for the whole region |
<!-- END_TF_DOCS -->
