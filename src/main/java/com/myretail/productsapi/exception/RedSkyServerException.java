package com.myretail.productsapi.exception;

public class RedSkyServerException extends RuntimeException{

    private String message;
    private Integer statusCode;

    public RedSkyServerException(String message, Integer statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
