package com.myretail.productsapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="products")
@Builder
public class Product {
    @Id
    @NotBlank(message = "Must provide Id for product to be updated")
    private String id;
    private String name;
    @Valid
    private PriceCurrency current_price;

}
