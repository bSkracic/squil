package hr.bskracic.squil.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import hr.bskracic.squil.util.ContainerClient;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContainerServiceTest {

    private ContainerService underTest;
    private static final String TEST_ID = "test";

    @BeforeEach
    void setUp() {
        underTest = new ContainerService();
    }

    @BeforeAll
    static void setAll() {
        DockerClient client = ContainerClient.getInstance().client;
        if(client.listContainersCmd().exec().stream().anyMatch(c -> Arrays.equals(c.getNames(),
                new String[]{"/squil_container_" + TEST_ID}))) {
            client.stopContainerCmd("squil_container_" + TEST_ID).exec();
            client.removeContainerCmd("squil_container_" + TEST_ID).exec();
        }
        else if(client.listContainersCmd().withFilter("status", List.of("exited")).exec().stream().anyMatch(c -> Arrays.equals(c.getNames(),
                new String[]{"/squil_container_" + TEST_ID}))) {
            client.removeContainerCmd("squil_container_" + TEST_ID).exec();
        }
    }

    @AfterAll
    static void cleanUp() {
        DockerClient client = ContainerClient.getInstance().client;

        if(client.listContainersCmd().exec().stream().anyMatch(c -> Arrays.equals(c.getNames(),
                new String[]{"/squil_container_" + TEST_ID}))) {
            client.removeContainerCmd("squil_container_" + TEST_ID).exec();
        }
    }

    @Test
    @Order(1)
    void getAllContainers() {
        assertThat(underTest.getAllContainers()).isNotNull();
    }

    @Test
    @Order(2)
    void createContainer() {
        underTest.createContainer(TEST_ID);
        Optional<Container> findContainer = underTest.getAllContainers().stream().filter(c ->
                Arrays.equals(c.getNames(), new String[]{"/squil_container_" + TEST_ID}))
        .findFirst();
        assertThat(findContainer).isNotEmpty();
    }

    @Test
    @Order(3)
    void executeQuery() {
        String result = underTest.executeQuery("select * from test;", TEST_ID);
        System.out.println(result);
        assertThat(result).isNotNull();
    }

    @Test
    @Order(4)
    void stopContainer() {
        underTest.stopContainer(TEST_ID);
        assertThat(ContainerClient.getInstance().client.listContainersCmd().withFilter("name",
                List.of("squil_container_" + TEST_ID)).exec().stream().anyMatch(c -> Arrays.equals(c.getNames(),
                new String[]{"/squil_container_" +TEST_ID}))).isFalse();
    }

    @Test
    @Order(5)
    void removeContainer() {
        underTest.removeContainer(TEST_ID);
        assertThat(underTest.getAllContainers().isEmpty()).isTrue();
    }
}