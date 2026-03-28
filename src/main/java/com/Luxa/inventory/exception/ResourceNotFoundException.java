package com.Luxa.inventory.exception;

/**
 * Thrown when a requested resource (e.g. product) does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
