package com.aws.lambda.quarkus.simpledemo;

import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

/**
 * Floci (local AWS emulator) in a container. Lambda functions run in sibling containers started
 * through the mounted container engine socket, so Docker or Podman must be reachable ({@code task
 * it} sets it up). The image is pinned by the {@code floci.image} property of the root pom.
 */
final class FlociEmulator implements AutoCloseable {

    private static final int PORT = 4566;
    private static final String ENGINE_SOCKET = "/var/run/docker.sock";

    private final GenericContainer<?> container =
            new GenericContainer<>(DockerImageName.parse(image()))
                    .withExposedPorts(PORT)
                    // the engine socket is owned by root (Docker) or by the VM user (rootless
                    // Podman)
                    .withEnv("FLOCI_RUN_AS_ROOT", "true")
                    // no ":z" relabel (the Testcontainers default) and no SELinux confinement: both
                    // are denied on a Podman
                    // machine for a socket the container does not own; harmless with Docker
                    .withCreateContainerCmdModifier(
                            cmd ->
                                    cmd.getHostConfig()
                                            .withBinds(
                                                    new Bind(
                                                            ENGINE_SOCKET,
                                                            new Volume(ENGINE_SOCKET),
                                                            AccessMode.rw))
                                            .withSecurityOpts(List.of("label=disable")))
                    .waitingFor(
                            Wait.forHttp("/_floci/health")
                                    .forPort(PORT)
                                    .withStartupTimeout(Duration.ofMinutes(2)));

    FlociEmulator start() {
        container.start();
        return this;
    }

    String endpoint() {
        return "http://" + container.getHost() + ":" + container.getMappedPort(PORT);
    }

    LambdaClient lambda() {
        return LambdaClient.builder()
                .endpointOverride(URI.create(endpoint()))
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")))
                .httpClient(UrlConnectionHttpClient.create())
                .build();
    }

    @Override
    public void close() {
        container.stop();
    }

    private static String image() {
        String image = System.getProperty("floci.image");
        if (image == null || image.isBlank()) {
            throw new IllegalStateException(
                    "floci.image is not set: run the tests through Maven (task it)");
        }
        return image;
    }
}
