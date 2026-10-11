#!/usr/bin/env bash
# Usage: floci-terraform-test.sh <terraform-dir>
# Runs `terraform test` of one stack against a throwaway Floci container (fake credentials, no AWS account).
# The image is the one pinned by `floci.image` in the root pom.
set -euo pipefail

repo_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
tf_dir="$(cd "${1:?usage: $0 <terraform-dir>}" && pwd)"

# shellcheck source=scripts/container-env.sh
. "$repo_dir/scripts/container-env.sh"

cli="$(command -v docker || command -v podman)"
image="$(sed -n 's#^ *<floci.image>\(.*\)</floci.image>.*#\1#p' "$repo_dir/pom.xml" | head -n 1)"
name="floci-tf-test-$$"
TF_DATA_DIR="$(mktemp -d)" # keeps the S3-backed .terraform of tf:init untouched
export TF_DATA_DIR
trap '"$cli" rm -f "$name" >/dev/null 2>&1; rm -rf "$TF_DATA_DIR"' EXIT

"$cli" run -d --name "$name" -p 4566:4566 -e FLOCI_RUN_AS_ROOT=true --security-opt label=disable \
  -v /var/run/docker.sock:/var/run/docker.sock "$image" >/dev/null
for _ in $(seq 1 60); do curl -sf http://localhost:4566/_floci/health >/dev/null && break; sleep 1; done

cd "$tf_dir"
terraform init -backend=false -input=false
terraform test
