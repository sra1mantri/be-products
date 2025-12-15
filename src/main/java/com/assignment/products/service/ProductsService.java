package com.assignment.products.service;

import com.assignment.products.entity.Product;
import com.assignment.products.mapper.ProductMapper;
import com.assignment.products.model.ProductRequestDTO;
import com.assignment.products.model.ProductUpdateRequestDTO;
import com.assignment.products.model.ProductsResponseDTO;
import com.assignment.products.repository.ProductSpecification;
import com.assignment.products.repository.ProductsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;

    private final ProductMapper productMapper;

    private final ProductSpecification productSpecification;

    public List<ProductsResponseDTO> findAllProducts(){
        boolean isAdmin = isRoleAdmin();
        List<Product> productsList = productsRepository.findAll(productSpecification.filterProducts(null, null, null, false, isAdmin));
        return productsList.stream().filter(Objects::nonNull).map(productMapper::convertToDTO).collect(Collectors.toList());
    }

    public List<ProductsResponseDTO> createProducts(List<ProductRequestDTO> createProductsList) {
        List<Product> products = createProductsList.stream().map(productMapper::convertFromDTO).collect(Collectors.toList());
        return productsRepository.saveAllAndFlush(products).stream().filter(Objects::nonNull).map(
                productMapper::convertToDTO).collect(Collectors.toList());
    }

    public ProductsResponseDTO updateProducts(
            long productId,
            ProductUpdateRequestDTO productRequestDTO) {

        Product product = productsRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Optional.ofNullable(productRequestDTO.getName())
                .ifPresent(product::setName);

        Optional.ofNullable(productRequestDTO.getDescription())
                .ifPresent(product::setDescription);

        Optional.ofNullable(productRequestDTO.getPrice())
                .ifPresent(product::setPrice);

        Optional.ofNullable(productRequestDTO.getQuantity())
                .ifPresent(product::setQuantity);

        return productMapper.convertToDTO(productsRepository.save(product));
    }


    public void deleteProduct(long productId) {
        productsRepository.deleteById(productId);
    }

    public List<ProductsResponseDTO> findByCriteria(String name, BigDecimal minPrice, BigDecimal maxPrice, Boolean available){
        boolean isAdmin = isRoleAdmin();
            return productsRepository.findAll(productSpecification.filterProducts(name, minPrice, maxPrice, available, isAdmin))
                    .stream().map(productMapper::convertToDTO).collect(Collectors.toList());
    }

    public Product findProductById(long productId){
        return productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
    }

    public void reduceProductStock(Product product, int quantity){
        product.setQuantity(product.getQuantity() - quantity);
        productsRepository.save(product);
    }

    private static boolean isRoleAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ROLE_ADMIN"));
    }

}
