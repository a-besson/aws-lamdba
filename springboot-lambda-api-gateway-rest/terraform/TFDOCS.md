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
| [aws_acm_certificate.api](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/acm_certificate) | resource |
| [aws_acm_certificate_validation.api](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/acm_certificate_validation) | resource |
| [aws_api_gateway_account.api_gateway_account](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_account) | resource |
| [aws_api_gateway_base_path_mapping.api](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_base_path_mapping) | resource |
| [aws_api_gateway_deployment.api_deploy](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_deployment) | resource |
| [aws_api_gateway_domain_name.api](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_domain_name) | resource |
| [aws_api_gateway_integration.lambda_integration](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_integration) | resource |
| [aws_api_gateway_method.proxy](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_method) | resource |
| [aws_api_gateway_method_settings.all](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_method_settings) | resource |
| [aws_api_gateway_resource.root](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_resource) | resource |
| [aws_api_gateway_rest_api.my_api](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_rest_api) | resource |
| [aws_api_gateway_stage.stage](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/api_gateway_stage) | resource |
| [aws_cloudwatch_log_group.main_api_gw](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/cloudwatch_log_group) | resource |
| [aws_iam_role.api_gateway_account_role](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role) | resource |
| [aws_iam_role.lambda_execution_role](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role) | resource |
| [aws_iam_role_policy.api_gateway_cloudwatch_policy](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role_policy) | resource |
| [aws_iam_role_policy_attachment.insights_policy](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role_policy_attachment) | resource |
| [aws_iam_role_policy_attachment.this_xray_tracing](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/iam_role_policy_attachment) | resource |
| [aws_lambda_function.springboot_lambda](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/lambda_function) | resource |
| [aws_lambda_permission.apigw_lambda](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/lambda_permission) | resource |
| [aws_route53_record.api](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/route53_record) | resource |
| [aws_route53_record.api_validation](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/route53_record) | resource |
| [aws_s3_bucket.lambda_bucket](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_bucket) | resource |
| [aws_s3_bucket_public_access_block.lambda_bucket](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_bucket_public_access_block) | resource |
| [aws_s3_bucket_server_side_encryption_configuration.lambda_bucket](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_bucket_server_side_encryption_configuration) | resource |
| [aws_s3_object.deploy_lambda_to_s3](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/resources/s3_object) | resource |
| [aws_iam_policy_document.lambda_policy_assume_role](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/data-sources/iam_policy_document) | data source |
| [aws_route53_zone.public](https://registry.terraform.io/providers/hashicorp/aws/6.68.0/docs/data-sources/route53_zone) | data source |

## Inputs

| Name | Description | Type | Default | Required |
| ---- | ----------- | ---- | ------- | :------: |
| <a name="input_aws_profile"></a> [aws\_profile](#input\_aws\_profile) | AWS profile | `string` | `"default"` | no |
| <a name="input_aws_region"></a> [aws\_region](#input\_aws\_region) | AWS target region | `string` | `"eu-west-3"` | no |
| <a name="input_domain_name"></a> [domain\_name](#input\_domain\_name) | domain name | `string` | `"aws-lab-demo.com"` | no |
| <a name="input_lambda_bucket"></a> [lambda\_bucket](#input\_lambda\_bucket) | lambda bucket name | `string` | `"cloud-public-springboot-lambda-api-gateway-rest-bucket"` | no |
| <a name="input_lambda_function_name"></a> [lambda\_function\_name](#input\_lambda\_function\_name) | lambda function name, also prefixes the IAM and log group names | `string` | `"cloud-public-springboot-lambda-api-gateway-rest"` | no |
| <a name="input_lambda_package_path"></a> [lambda\_package\_path](#input\_lambda\_package\_path) | Lambda package built by `task <sample>:package`, relative to this directory | `string` | `"../target/springboot-lambda-api-gateway-rest.jar"` | no |

## Outputs

| Name | Description |
| ---- | ----------- |
| <a name="output_api_gtw_deployment_invoke_url"></a> [api\_gtw\_deployment\_invoke\_url](#output\_api\_gtw\_deployment\_invoke\_url) | Deployment invoke url |
| <a name="output_api_invoke_url"></a> [api\_invoke\_url](#output\_api\_invoke\_url) | API Gateway URL of the /demo resource (read by `task invoke`) |
| <a name="output_aws_lambda_function"></a> [aws\_lambda\_function](#output\_aws\_lambda\_function) | Spring Boot Lambda ARN |
| <a name="output_custom_domain_api"></a> [custom\_domain\_api](#output\_custom\_domain\_api) | n/a |
<!-- END_TF_DOCS -->
