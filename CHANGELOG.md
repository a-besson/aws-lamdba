# Changelog

## [2026-10-10] - Daily Log - Repo review: aws-lambda rename, test layers, Taskfile, quality/security gates, per-module CI

- **maven**: parent renamed `aws-lambda` (`com.aws.lambda`), packages moved to `com.aws.lambda.{quarkus,springboot}.*`, plugin versions centralised in the root `pluginManagement`, Lombok declared as annotation processor (build was red on JDK 23+), `quarkus.package.type` (removed in Quarkus 3.39) replaced by `quarkus.native.enabled`, `quarkus-junit5` replaced by `quarkus-junit`
- **tests**: `*Test` (surefire) vs `*IT` (failsafe) convention, `skipUTs`/`skipITs`, failsafe enabled in the four modules (ITs never ran before), `StreamLambdaIT` and `RestApiDemoIT` fixed/re-enabled, REST GET/POST/PUT and error-path tests added, one `NoOpContext` per Spring module
- **tooling**: CloudFormation/SAM and Makefiles removed, root Taskfile with one namespace per sample, Checkstyle (lint rules only), pre-commit, TFLint (AWS ruleset), KICS, Trivy, Brewfile
- **terraform**: formatted, exact provider and `required_version` pins, IAM names prefixed by the function name, logging policy limited to the function log group, API Gateway data trace off, S3 package bucket hardened, REST outputs fixed for provider 6.x, `validate` green on a clean checkout
- **security**: Jackson and Tomcat bumped to patched releases of the current lines; Spring Framework CVE-2026-47884/47890 tracked in `.trivyignore` until 2026-11-09 (fix only in 7.x)
- **ci**: `github-actions.yml` replaced by `_build-java` + one `build-<module>` per sample, `check-cyber`, `check-quality`; actions pinned by SHA; Renovate pins new action digests
