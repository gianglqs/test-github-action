package com.hysteryale.exception;

public class MissingColumnException extends Exception{
    public MissingColumnException(String message){
        super(message);
    }
}
