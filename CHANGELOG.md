# Changelog

## [2026-10-11] - Daily Log - Terraform tests on Floci, infra/ folder

- **terraform**: `terraform test` of every stack on Floci (`task <ns>:tf:test`, `terraform/tests/floci.tftest.hcl`, placeholder zip, hosted zone fixture and ACM validation override for the REST stacks), new `lambda_insights_enabled` variable (default true) to skip the AWS-owned layer on the emulator
- **ci**: `terraform-test` job in `_build-java.yml` (Floci container, fake credentials), required by `package`
- **ci**: workflows run on `pull_request` only (no `push` trigger); `check-quality` keeps its weekly cron
- **tooling**: `task <target>` run from a module directory failed (`Could not find the selected project in the reactor`: Task takes the module Taskfile as root, so `ROOT_DIR` was the module); module Taskfiles now use `REPO_DIR`, and the container engine setup and the Floci `terraform test` run moved to `scripts/` so they work from both places
- **layout**: `apigateway-account/` and `state-bucket/` moved under `infra/` (state keys unchanged); unused IntelliJ `.run/` debug configuration removed
- **tooling**: `tf:*` preconditions now resolve `target/` from the module directory (they looked in `terraform/..` relative to the wrong directory)

## [2026-10-10] - Daily Log - Repo review: aws-lambda rename, test layers, Taskfile, quality/security gates, per-module CI

- **maven**: parent renamed `aws-lambda` (`com.aws.lambda`), packages moved to `com.aws.lambda.{quarkus,springboot}.*`, plugin versions centralised in the root `pluginManagement`, Lombok declared as annotation processor (build was red on JDK 23+), `quarkus.package.type` (removed in Quarkus 3.39) replaced by `quarkus.native.enabled`, `quarkus-junit5` replaced by `quarkus-junit`
- **tests**: `*Test` (surefire) vs `*IT` (failsafe) convention, `skipUTs`/`skipITs`, failsafe enabled in the four modules (ITs never ran before), `StreamLambdaIT` and `RestApiDemoIT` fixed/re-enabled, REST GET/POST/PUT and error-path tests added, one `NoOpContext` per Spring module
- **tooling**: CloudFormation/SAM and Makefiles removed, root Taskfile with one namespace per sample, Checkstyle (lint rules only), pre-commit, TFLint (AWS ruleset), KICS, Trivy, Brewfile
- **terraform**: formatted, exact provider and `required_version` pins, IAM names prefixed by the function name, logging policy limited to the function log group, API Gateway data trace off, S3 package bucket hardened, REST outputs fixed for provider 6.x, `validate` green on a clean checkout
- **security**: Jackson and Tomcat bumped to patched releases of the current lines; Spring Framework CVE-2026-47884/47890 tracked in `.trivyignore` until 2026-11-09 (fix only in 7.x)
- **ci**: `github-actions.yml` replaced by `_build-java` + one `build-<module>` per sample, `check-cyber`, `check-quality`; actions pinned by SHA; Renovate pins new action digests
- **sast**: CodeQL (`java-kotlin`, manual build) as a `codeql` job of `check-quality`, on PR and weekly (`0 5 * * 1`)
- **emulator tests**: `Floci*IT` per sample (Testcontainers + AWS SDK v2): the packaged artifact is deployed to Floci (`floci/floci:2.2.0`, pinned in the root pom, kept by Renovate) with the settings of its Terraform stack and invoked through the Lambda API or an API Gateway REST API; Podman handled by `task <ns>:it` (machine socket, Ryuk off)
- **terraform**: partial S3 backend with `use_lockfile` (`backend.hcl.example`, `tf:migrate-state`) and a `infra/state-bucket/` bootstrap stack; `infra/apigateway-account/` stack for the `aws_api_gateway_account` singleton, removed from both REST stacks; REST stacks get the function log group, `logging_config` and a scoped logging policy; Lambda and artifact bucket moved to `terraform-aws-modules/lambda` 8.12.0 and `s3-bucket` 5.16.2 (`moved` blocks, `external`/`local`/`null` providers pinned), API Gateway REST stays native (no REST module)
- **java**: Spotless with google-java-format 1.35.0 (AOSP), `task format`, check bound to `validate`; unused Quarkus Dockerfiles and `.dockerignore` removed
