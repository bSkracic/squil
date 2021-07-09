package hr.bskracic.squil.controller;

import com.github.dockerjava.api.model.Container;
import hr.bskracic.squil.dto.QueryRequest;
import hr.bskracic.squil.service.SquilDockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

class SquilContainer {
    private String id;
    private String status;
    private String image;

    public SquilContainer(String id, String status, String image) {
        this.id = id;
        this.status = status;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

@RestController
@RequestMapping(path = "api/v1/containers")
public class SquilDockerController {

    @Autowired
    private SquilDockerService squilDockerService;

    @GetMapping
    public List<Container> getAllContainers() {

//        ArrayList<SquilContainer> containers = new ArrayList<>();
//
//        squilDockerService.getAllContainers().forEach(c -> {
//            containers.add(new SquilContainer(c.getId(), c.getStatus(), c.getImage()));
//        });

        List<Container> containers = squilDockerService.getAllContainers();

        return containers;
    }

    @PostMapping
    public String createContainer(@RequestParam(name="userID") String userID) {
        squilDockerService.createContainer(userID);
        return userID;
    }

    @DeleteMapping(path = "{userID}")
    public void deleteContainer(@PathVariable("userID") String userID) {
        squilDockerService.stopContainer(userID);
    }

    @GetMapping(path = "/execute")
    public String executeQuery(@RequestBody QueryRequest queryRequest) {
        String containerID = squilDockerService.executeQuery(queryRequest.sqlQuery, queryRequest.userID);
        return containerID;
    }

}
