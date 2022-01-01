package com.myretail.productsapi.domain.productDescription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductClassification {
    @JsonProperty(value = "product_type_name")
    public String productTypeName;
    @JsonProperty(value = "merchandise_type_name")
    public String merchandiseTypeName;
}
