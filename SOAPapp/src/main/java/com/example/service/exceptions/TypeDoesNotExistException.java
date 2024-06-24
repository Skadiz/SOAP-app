package com.example.service.exceptions;

public class TypeDoesNotExistException extends Exception {

    public TypeDoesNotExistException() {
        super("This Type does not exist in database, please put Internal or External");
    }
}
