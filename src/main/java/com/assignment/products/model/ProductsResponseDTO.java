package com.assignment.products.model;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductsResponseDTO {

    long id;
    String name;
    String description;
    BigDecimal price;
    int quantity;
    boolean isDeleted;
    LocalDateTime createTime;
    LocalDateTime lastUpdateTime;

}
