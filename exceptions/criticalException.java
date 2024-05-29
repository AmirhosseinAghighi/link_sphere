package org.linkSphere.exceptions;

public class criticalException extends Exception{
    public criticalException() throws duplicateException {
        throw new duplicateException("There is a duplication happening in the project");
    }

    public criticalException(String message) {
        super(message);
    }
}
