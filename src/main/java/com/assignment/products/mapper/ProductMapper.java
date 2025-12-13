package com.assignment.products.mapper;

import com.assignment.products.entity.Product;
import com.assignment.products.model.ProductRequestDTO;
import com.assignment.products.model.ProductsResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductsResponseDTO convertToDTO(Product product) {
        return ProductsResponseDTO.builder()
                .description(product.getDescription())
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .isDeleted(product.isDeleted())
                .createTime(product.getCreatedAt())
                .lastUpdateTime(product.getUpdatedAt())
                .build();
    }

    public Product convertFromDTO(ProductRequestDTO product) {
        return Product.builder()
                .description(product.getDescription())
                .name(product.getName())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .build();
    }
}
