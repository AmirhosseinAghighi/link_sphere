package org.linkSphere.exceptions;

public class notFoundException extends Exception{
    public notFoundException() {
        new notFoundException("Not Found.");
    }

    public notFoundException(String message) {
        super(message);
    }
}
