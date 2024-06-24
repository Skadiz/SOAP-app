package com.example.service.exceptions;

public class PersonCanNotBeEmptyException extends Exception{
    public PersonCanNotBeEmptyException(){
        super("Person or Person ID cannot be null.");
    }
}
