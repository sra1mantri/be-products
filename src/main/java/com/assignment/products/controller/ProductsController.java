package com.assignment.products.controller;

import com.assignment.products.model.ProductRequestDTO;
import com.assignment.products.model.ProductUpdateRequestDTO;
import com.assignment.products.model.ProductsResponseDTO;
import com.assignment.products.service.ProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsService productsService;

    @Operation(
            summary = "Fetch All products",
            description = "Fetch all the products. All users can view the products"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product details returned successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication- Required")
    })
    @GetMapping("/")
    ResponseEntity<List<ProductsResponseDTO>> findAllProducts(){
        return ResponseEntity.ok(productsService.findAllProducts());
    }

    @Operation(
            summary = "Creates a new product",
            description = "Creates a product. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @PostMapping("/create-products")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<ProductsResponseDTO>> createProducts(@Valid @RequestBody List<ProductRequestDTO> createProductsList){
        return new ResponseEntity<>(productsService.createProducts(createProductsList),HttpStatus.CREATED);
    }

    @Operation(
            summary = "Updates an existing product",
            description = "Updates a product. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @PutMapping("/update-product/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ProductsResponseDTO> updateProducts(@PathVariable(name = "id") @Positive(message = "Product ID must be greater than 0") long productId, @RequestBody @Valid ProductUpdateRequestDTO productRequestDTO){
        return ResponseEntity.ok(productsService.updateProducts(productId, productRequestDTO));
    }

    @Operation(
            summary = "Deletes an existing product",
            description = "Deletes a product. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @DeleteMapping("/delete-product/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") @Positive(message = "Product ID must be greater than 0") long productId){
        productsService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Search/Filters all products",
            description = "Search/Filters all products based on User criteria."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returned product successfully based on Criteria"),
            @ApiResponse(responseCode = "401", description = "UnAuthorized - Authorization required")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductsResponseDTO>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "true") boolean available){
        return ResponseEntity.ok(productsService.findByCriteria(name,minPrice, maxPrice, available));
    }

}
