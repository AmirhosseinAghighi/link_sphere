package org.linkSphere.exceptions;

public class NoStatusCodeException extends Exception{
    public NoStatusCodeException() throws duplicateException {
        throw new duplicateException("There isn't any status code.");
    }

    public NoStatusCodeException(String message) {
        super(message);
    }
}
