package hr.bskracic.squil.exception;

public class ContainerQueryFailedException extends RuntimeException{
    public ContainerQueryFailedException(String containerID) {
        super("execution failed for container " + containerID);
    }
}
