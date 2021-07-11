package hr.bskracic.squil.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContainerNotFoundException extends RuntimeException {
    public ContainerNotFoundException(String containerID) {
        super("container " + containerID + " not found");
    }
}
