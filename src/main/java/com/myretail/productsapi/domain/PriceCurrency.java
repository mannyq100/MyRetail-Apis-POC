package com.myretail.productsapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceCurrency {
    @NotNull(message = "Price value must not ne null")
    @Positive(message = "Price must be greater than zero")
    private double value;
    @NotEmpty(message = "Currency code must be provided")
    //This could be represented as enum since currencies are fixed
    private String currencyCode;
}
