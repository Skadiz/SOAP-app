package com.example.service.exceptions;

public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(String personId) {
        super("Employee with id " + personId + " not found");
    }
    public EmployeeNotFoundException() {
        super("Employee with given parameters not found");
    }
}

