package hr.bskracic.squil.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ContainerNotRunningException extends RuntimeException{
    public ContainerNotRunningException(String containerID) {
        super("container " + containerID + " not running");
    }
}
