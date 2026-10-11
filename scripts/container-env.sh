# Sourced by the Taskfiles that need a container engine (Floci ITs, `terraform test`): on a Mac without DOCKER_HOST,
# point Testcontainers and the CLI at the Podman machine socket. Docker (CI, Linux) needs nothing.
if [ -z "${DOCKER_HOST:-}" ] && command -v podman >/dev/null 2>&1; then
  podman machine start >/dev/null 2>&1 || true
  DOCKER_HOST="unix://$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}')"
  export DOCKER_HOST
  export TESTCONTAINERS_RYUK_DISABLED=true
fi
