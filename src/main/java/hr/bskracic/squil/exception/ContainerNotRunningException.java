package hr.bskracic.squil.exception;

public class ContainerNotRunningException extends RuntimeException{
    public ContainerNotRunningException(String containerID) {
        super("container " + containerID + " not running");
    }
}
