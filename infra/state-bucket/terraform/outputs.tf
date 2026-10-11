output "backend_config" {
  description = "Content of the repo-root backend.hcl file"
  value       = <<-HCL
    bucket  = "${module.state_bucket.s3_bucket_id}"
    region  = "${module.state_bucket.s3_bucket_region}"
    encrypt = true
  HCL
}
