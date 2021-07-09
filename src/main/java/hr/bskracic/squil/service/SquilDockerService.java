package hr.bskracic.squil.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.*;
import hr.bskracic.squil.exception.ContainerNotFoundException;
import hr.bskracic.squil.util.SquilDockerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class SquilDockerService {

    public List<Container> getAllContainers() {
        DockerClient client = SquilDockerClient.getInstance().client;
        return client.listContainersCmd().exec();
    }

    public String createContainer(String userID) {
        DockerClient client = SquilDockerClient.getInstance().client;

        // Create volume
        CreateVolumeResponse volume = client.createVolumeCmd().withName(userID).exec();

        // Create container with volume
        CreateContainerResponse createContainerResponse = client.createContainerCmd("postgres")
                        .withHostConfig(HostConfig.newHostConfig()
                        .withBinds(new Bind("/var/lib/postgresql/data", new Volume(volume.getMountpoint()))))
                        .withImage("postgres")
                        .withName(userID)
                        .withEnv("POSTGRES_PASSWORD=password")
                        .withAttachStdout(true)
                        .withAttachStderr(true)
                        .withTty(true)
                        .exec();

        client.startContainerCmd(createContainerResponse.getId()).exec();

        return createContainerResponse.getId();
    }

    public String executeQuery(String sqlQuery, String userID) {

        DockerClient client = SquilDockerClient.getInstance().client;

        // Check if container is running, otherwise start it <optional>

        // Prepare statement
        ExecCreateCmdResponse command = client
                .execCreateCmd(userID)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd("psql", "-U", "postgres", "-d", "postgres", "-c", sqlQuery)
                .exec();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Execute sql command
        try {
            client.execStartCmd(command.getId()).exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame object) {
                    try {
                        outputStream.write(object.getPayload());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onComplete() {
                    System.out.println("Message: " + outputStream);
                }
            }).awaitCompletion(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return outputStream.toString();
    }

//    public boolean isRunning(String userID) {
//        DockerClient client = SquilDockerClient.getInstance().client;
//
//        List<Container> containers = client.listContainersCmd().withFilter("name=", List.of("\" + userID)).exec();
//        Optional<Container> container = containers.stream().filter(c -> c.getId().equals(userID)).findFirst();
//
//        if(container.isPresent()) {
//            String[] names = container.get().getNames();
//
//        }
//    }

    public void stopContainer(String userID) throws ContainerNotFoundException {
        DockerClient client = SquilDockerClient.getInstance().client;
        try {
            client.stopContainerCmd(userID).exec();
        } catch(Exception e) {
            throw new ContainerNotFoundException("Container not found");
        }
    }

}
