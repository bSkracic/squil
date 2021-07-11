package hr.bskracic.squil.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.CreateVolumeResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.*;
import hr.bskracic.squil.exception.*;
import hr.bskracic.squil.util.ContainerClient;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ContainerService {

    public List<Container> getAllContainers() {
        return ContainerClient.getInstance().client.listContainersCmd().withShowAll(true).withFilter("name", List.of("squil_container_")).exec();
    }

    public String createContainer(String userID) {
        DockerClient client = ContainerClient.getInstance().client;

        // Create volume
        CreateVolumeResponse volume = client.createVolumeCmd().withName(userID).exec();

        // Create container with volume
        CreateContainerResponse createContainerResponse = client.createContainerCmd("postgres")
                        .withHostConfig(HostConfig.newHostConfig()
                        .withBinds(new Bind("/var/lib/postgresql/data", new Volume(volume.getMountpoint()))))
                        .withImage("postgres")
                        .withName("squil_container_" + userID)
                        .withEnv("POSTGRES_PASSWORD=password")
                        .withAttachStdout(true)
                        .withAttachStderr(true)
                        .withTty(true)
                        .exec();

        client.startContainerCmd(createContainerResponse.getId()).exec();

        return createContainerResponse.getId();
    }

    public void startContainer(String userID) {
        try {
            ContainerClient.getInstance().client.startContainerCmd("squil_container_" + userID).exec();
        } catch (Exception e) {
            throw new ContainerRunningException(userID);
        }
    }

    public String executeQuery(String sqlQuery, String userID) {

        DockerClient client = ContainerClient.getInstance().client;

        if(!containerExists(userID)){
            throw new ContainerNotStartedException(userID);
        }

        // Prepare statement
        ExecCreateCmdResponse command = client
                .execCreateCmd("squil_container_" + userID)
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
            throw new ContainerQueryFailedException(userID);
        }

        return outputStream.toString();
    }

    public void stopContainer(String userID) throws ContainerNotFoundException {
        if(containerRunning(userID)) {
            ContainerClient.getInstance().client.stopContainerCmd("squil_container_" + userID).exec();
        } else {
            throw new ContainerNotRunningException(userID);
        }
    }

    public void removeContainer(String userID) throws ContainerRunningException, ContainerNotRunningException {
        if (containerRunning(userID)) {
            throw new ContainerRunningException(userID);
        } else if(!containerExists(userID)) {
            throw new ContainerNotFoundException(userID);
        } else {
            ContainerClient.getInstance().client.removeContainerCmd("squil_container_"+ userID).exec();
        }
    }

    public boolean containerExists(String containerID) {

        for(var c : ContainerClient.getInstance().client.listContainersCmd().withShowAll(true).exec()) {
            System.out.println(c.getId() + " | " + Arrays.toString(c.getNames()));
        }

        return ContainerClient.getInstance().client.listContainersCmd().withShowAll(true).exec().stream().anyMatch(c -> Arrays.equals(c.getNames(),
                new String[]{"/squil_container_" + containerID}));
    }

    public boolean containerRunning(String containerID) {
        return ContainerClient.getInstance().client.listContainersCmd().exec().stream().anyMatch(c -> Arrays.equals(c.getNames(),
                new String[]{"/squil_container_" + containerID}));
    }

}
