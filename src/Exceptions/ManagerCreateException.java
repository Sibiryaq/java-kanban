package Exceptions;

public class ManagerCreateException extends RuntimeException {
    public ManagerCreateException(final String message) {
        super(message);
    }
}