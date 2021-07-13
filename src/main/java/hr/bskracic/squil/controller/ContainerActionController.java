package hr.bskracic.squil.controller;

import hr.bskracic.squil.dto.query.QueryRequest;
import hr.bskracic.squil.dto.query.QueryResponse;
import hr.bskracic.squil.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/containers")
public class ContainerActionController {

    @Autowired
    private ContainerService containerService;

    @GetMapping(path = "/execute")
    public QueryResponse executeQuery(@RequestBody QueryRequest queryRequest) {
        String result = containerService.executeQuery(queryRequest.sqlQuery, queryRequest.userID);
        return new QueryResponse(result);
    }

    @GetMapping(path = "/start/{userID}")
    public void startContainer (@PathVariable(name="userID") String userID) {
        containerService.startContainer(userID);
    }

    @GetMapping(path = "/stop/{userID}")
    public void stopContainer(@PathVariable(name="userID") String userID) {
        containerService.stopContainer(userID);
    }
}
