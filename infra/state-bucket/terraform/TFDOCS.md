# terraform

<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
| ---- | ------- |
| <a name="requirement_terraform"></a> [terraform](#requirement\_terraform) | ~> 1.16 |
| <a name="requirement_aws"></a> [aws](#requirement\_aws) | 6.68.0 |

## Providers

No providers.

## Modules

| Name | Source | Version |
| ---- | ------ | ------- |
| <a name="module_state_bucket"></a> [state\_bucket](#module\_state\_bucket) | terraform-aws-modules/s3-bucket/aws | 5.16.2 |

## Resources

No resources.

## Inputs

| Name | Description | Type | Default | Required |
| ---- | ----------- | ---- | ------- | :------: |
| <a name="input_aws_profile"></a> [aws\_profile](#input\_aws\_profile) | AWS profile | `string` | `"default"` | no |
| <a name="input_aws_region"></a> [aws\_region](#input\_aws\_region) | AWS region of the state bucket | `string` | `"eu-west-3"` | no |
| <a name="input_noncurrent_version_days"></a> [noncurrent\_version\_days](#input\_noncurrent\_version\_days) | Days a previous version of a state file is kept | `number` | `90` | no |
| <a name="input_state_bucket"></a> [state\_bucket](#input\_state\_bucket) | Globally unique name of the state bucket (set it in a git-ignored terraform.tfvars) | `string` | n/a | yes |

## Outputs

| Name | Description |
| ---- | ----------- |
| <a name="output_backend_config"></a> [backend\_config](#output\_backend\_config) | Content of the repo-root backend.hcl file |
<!-- END_TF_DOCS -->
