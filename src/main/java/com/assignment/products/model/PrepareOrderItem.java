package com.assignment.products.model;

import com.assignment.products.entity.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrepareOrderItem {

    long productId;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal totalPrice;
    Product product;
}
