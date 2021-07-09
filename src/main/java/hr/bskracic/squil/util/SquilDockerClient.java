package hr.bskracic.squil.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;

public final class SquilDockerClient {
    private static volatile SquilDockerClient instance;

    public DockerClient client;

    private SquilDockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        this.client = DockerClientImpl.getInstance(config, httpClient);
    }

    public static SquilDockerClient getInstance() {
        SquilDockerClient result = instance;
        if (result != null) {
            return result;
        }
        synchronized (SquilDockerClient.class) {
            if (instance == null) {
                instance = new SquilDockerClient();
            }
            return instance;
        }
    }
}