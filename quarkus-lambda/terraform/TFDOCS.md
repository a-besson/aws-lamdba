# terraform

<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
| ---- | ------- |
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | ~> 1.16 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | 6.68.0 |
| <a name="requirement_external"></a> [external](#requirement\_external) | 2.4.2 |
| <a name="requirement_local"></a> [local](#requirement\_local) | 2.9.1 |
| <a name="requirement_null"></a> [null](#requirement\_null) | 3.3.2 |

## Providers

| Name | Version |
| ---- | ------- |
| <a name="provider_aws"></a> [aws](#provider\_aws) | 6.68.0 |

## Modules

| Name | Source | Version |
| ---- | ------ | ------- |
| <a name="module_lambda"></a> [lambda](#module\_lambda) | terraform-aws-modules/lambda/aws | 8.12.0 |
| <a name="module_lambda_bucket"></a> [lambda\_bucket](#module\_lambda\_bucket) | terraform-aws-modules/s3-bucket/aws | 5.16.2 |

## Resources

| Name | Type |
| ---- | ---- |
| [aws_s3_object.deploy_lambda_to_s3](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_object) | resource |

## Inputs

| Name | Description | Type | Default | Required |
| ---- | ----------- | ---- | ------- | :------: |
| <a name="input_aws_profile"></a> [aws\_profile](#input\_aws\_profile) | AWS profile | `string` | `"default"` | no |
| <a name="input_aws_region"></a> [aws\_region](#input\_aws\_region) | AWS target region | `string` | `"eu-west-3"` | no |
| <a name="input_lambda_bucket"></a> [lambda\_bucket](#input\_lambda\_bucket) | lambda bucket name | `string` | `"cloud-public-quarkus-lambda-stack-bucket"` | no |
| <a name="input_lambda_function_name"></a> [lambda\_function\_name](#input\_lambda\_function\_name) | lambda function name, also prefixes the IAM and log group names | `string` | `"cloud-public-quarkus-lambda"` | no |
| <a name="input_lambda_insights_enabled"></a> [lambda\_insights\_enabled](#input\_lambda\_insights\_enabled) | Attach the AWS-owned Lambda Insights layer (not available on the Floci emulator, see tests/) | `bool` | `true` | no |
| <a name="input_lambda_package_path"></a> [lambda\_package\_path](#input\_lambda\_package\_path) | Lambda package built by `task <sample>:package`, relative to this directory | `string` | `"../target/function.zip"` | no |

## Outputs

| Name | Description |
| ---- | ----------- |
| <a name="output_aws_lambda_function"></a> [aws\_lambda\_function](#output\_aws\_lambda\_function) | Quarkus Lambda ARN |
| <a name="output_aws_lambda_function_url"></a> [aws\_lambda\_function\_url](#output\_aws\_lambda\_function\_url) | URL de la lambda |
| <a name="output_lambda_function_url"></a> [lambda\_function\_url](#output\_lambda\_function\_url) | Function URL of the Lambda (read by `task invoke`) |
<!-- END_TF_DOCS -->
