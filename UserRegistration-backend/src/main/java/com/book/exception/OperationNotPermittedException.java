package com.book.exception;


public class OperationNotPermittedException extends RuntimeException{
    public OperationNotPermittedException(String msg){
        //we have to pass this exception message to the super class exception
        super(msg);
        //now we have to handle this exception in exceptionHandler class
    }
}
