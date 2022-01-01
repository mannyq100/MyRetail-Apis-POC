package com.myretail.productsapi.exception;

public class ProductNotFoundException extends RuntimeException {
    private String message;

    public ProductNotFoundException(String s) {
        super(s);
        this.message = s;
    }
}
