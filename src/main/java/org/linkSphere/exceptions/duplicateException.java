package org.linkSphere.exceptions;

public class duplicateException extends Exception{
    public duplicateException() throws duplicateException {
        throw new duplicateException("There is a duplication happening in the project");
    }

    public duplicateException(String message) {
        super(message);
    }
}
