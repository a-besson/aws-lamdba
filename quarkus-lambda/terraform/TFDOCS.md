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
| [aws_cloudwatch_log_group.lambda_log_group](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/cloudwatch_log_group) | resource |
| [aws_iam_policy.lambda_logging_policy](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_policy) | resource |
| [aws_iam_role.lambda_execution_role](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role) | resource |
| [aws_iam_role_policy_attachment.insights_policy](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role_policy_attachment) | resource |
| [aws_iam_role_policy_attachment.lambda_logs_role](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role_policy_attachment) | resource |
| [aws_iam_role_policy_attachment.this_xray_tracing](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role_policy_attachment) | resource |
| [aws_lambda_function.quarkus_lambda](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/lambda_function) | resource |
| [aws_lambda_function_url.quarkus_lambda_url](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/lambda_function_url) | resource |
| [aws_s3_bucket.lambda_bucket](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_bucket) | resource |
| [aws_s3_bucket_public_access_block.lambda_bucket](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_bucket_public_access_block) | resource |
| [aws_s3_bucket_server_side_encryption_configuration.lambda_bucket](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_bucket_server_side_encryption_configuration) | resource |
| [aws_s3_object.deploy_lambda_to_s3](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_object) | resource |
| [aws_iam_policy_document.lambda_logging_policy](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/data-sources/iam_policy_document) | data source |
| [aws_iam_policy_document.lambda_policy_assume_role](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/data-sources/iam_policy_document) | data source |

## Inputs

| Name | Description | Type | Default | Required |
| ---- | ----------- | ---- | ------- | :------: |
| <a name="input_aws_profile"></a> [aws\_profile](#input\_aws\_profile) | AWS profile | `string` | `"default"` | no |
| <a name="input_aws_region"></a> [aws\_region](#input\_aws\_region) | AWS target region | `string` | `"eu-west-3"` | no |
| <a name="input_lambda_bucket"></a> [lambda\_bucket](#input\_lambda\_bucket) | lambda bucket name | `string` | `"cloud-public-quarkus-lambda-stack-bucket"` | no |
| <a name="input_lambda_function_name"></a> [lambda\_function\_name](#input\_lambda\_function\_name) | lambda function name, also prefixes the IAM and log group names | `string` | `"cloud-public-quarkus-lambda"` | no |
| <a name="input_lambda_package_path"></a> [lambda\_package\_path](#input\_lambda\_package\_path) | Lambda package built by `task <sample>:package`, relative to this directory | `string` | `"../target/function.zip"` | no |

## Outputs

| Name | Description |
| ---- | ----------- |
| <a name="output_aws_lambda_function"></a> [aws\_lambda\_function](#output\_aws\_lambda\_function) | Quarkus Lambda ARN |
| <a name="output_aws_lambda_function_url"></a> [aws\_lambda\_function\_url](#output\_aws\_lambda\_function\_url) | URL de la lambda |
| <a name="output_lambda_function_url"></a> [lambda\_function\_url](#output\_lambda\_function\_url) | Function URL of the Lambda (read by `task invoke`) |
<!-- END_TF_DOCS -->
