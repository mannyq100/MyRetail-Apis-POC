package com.myretail.productsapi.exception;

public class RedSkyClientException extends RuntimeException{

    private String message;
    private Integer statusCode;

    public RedSkyClientException(String message, Integer statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
