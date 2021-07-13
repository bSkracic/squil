package hr.bskracic.squil.controller;

import hr.bskracic.squil.dto.container.ContainerDTO;
import hr.bskracic.squil.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/containers")
public class ContainerController {

    @Autowired
    private ContainerService containerService;

    @GetMapping
    public List<ContainerDTO> getAllContainers() {
        ArrayList<ContainerDTO> containers = new ArrayList<>();

        containerService.getAllContainers().forEach(c -> {
            containers.add(new ContainerDTO(c));
        });

        return containers;
    }

    @PostMapping
    public String createContainer(@RequestParam(name="userID") String userID) {
        containerService.createContainer(userID);
        return userID;
    }

    @DeleteMapping(path = "{userID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContainer(@PathVariable(name="userID") String userID) {
        containerService.removeContainer(userID);
    }



}
