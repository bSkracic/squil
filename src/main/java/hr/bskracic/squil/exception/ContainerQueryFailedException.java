package hr.bskracic.squil.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ContainerQueryFailedException extends RuntimeException{
    public ContainerQueryFailedException(String containerID) {
        super("execution failed for container " + containerID);
    }
}
