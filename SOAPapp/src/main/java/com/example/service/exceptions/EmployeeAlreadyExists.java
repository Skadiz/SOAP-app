package com.example.service.exceptions;

public class EmployeeAlreadyExists extends Exception {
    public EmployeeAlreadyExists (String personId){
        super("Employee with id " + personId + " already exists");
    }
}
