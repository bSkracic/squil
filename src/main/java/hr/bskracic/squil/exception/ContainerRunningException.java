package hr.bskracic.squil.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ContainerRunningException extends RuntimeException {
    public ContainerRunningException(String containerID) {
        super("container " + containerID + " still running");
    }
}
