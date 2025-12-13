package com.assignment.products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NotStockAvailableException extends RuntimeException{

    public NotStockAvailableException(String message){
        super(message);
    }
}
