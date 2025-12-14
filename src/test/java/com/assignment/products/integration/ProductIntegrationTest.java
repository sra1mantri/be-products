package com.assignment.products.integration;

import com.assignment.products.entity.Product;
import com.assignment.products.model.ProductRequestDTO;
import com.assignment.products.model.ProductUpdateRequestDTO;
import com.assignment.products.repository.ProductsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductsRepository productsRepository;

    @BeforeEach
    void setUp() {
        productsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllProducts_ShouldReturnList() throws Exception {
        saveProduct("Item A", 10.0);
        saveProduct("Item B", 20.0);

        mockMvc.perform(get("/api/v1/products/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProducts_ShouldPersistToH2Database() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO(
                "Integration Test Item",
                "Description for H2",
                BigDecimal.valueOf(99.99),
                50
        );

        mockMvc.perform(post("/api/v1/products/create-products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(request))))
                .andExpect(status().isCreated());

        var productsInDb = productsRepository.findAll();

        assertEquals(1, productsInDb.size(), "Database should have exactly 1 product");
        assertEquals("Integration Test Item", productsInDb.get(0).getName());
        assertEquals(BigDecimal.valueOf(99.99), productsInDb.get(0).getPrice());
    }


    @Test
    @WithMockUser(roles = "USER")
    void createProducts_ShouldFailForUserRole() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO("Admin Item", "Desc", BigDecimal.TEN, 1);

        mockMvc.perform(post("/api/v1/products/create-products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(request))))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProducts_ShouldModifyDatabase_WhenAdmin() throws Exception {
        Product existing = saveProduct("Old Name", 50.0);

        ProductUpdateRequestDTO updateDTO = new ProductUpdateRequestDTO();
        updateDTO.setName("New Name");
        updateDTO.setQuantity(11);
        updateDTO.setPrice(BigDecimal.valueOf(75.0));


        mockMvc.perform(put("/api/v1/products/update-product/{id}", existing.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Name")));

        Product updated = productsRepository.findById(existing.getId()).orElseThrow();
        assertEquals("New Name", updated.getName());
        assertEquals(BigDecimal.valueOf(75.0).setScale(2), updated.getPrice().setScale(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_ShouldRemoveFromDatabase() throws Exception {
        Product existing = saveProduct("To Delete", 10.0);

        mockMvc.perform(delete("/api/v1/products/delete-product/{id}", existing.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertTrue(productsRepository.findById(existing.getId()).isEmpty(), "Product should be gone");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_ShouldReturn400_WhenInvalidId() throws Exception {
        mockMvc.perform(delete("/api/v1/products/delete-product/-5")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchProducts_ShouldFilterByPriceAndName() throws Exception {
        saveProduct("Apple iPhone", 1000.0);
        saveProduct("Apple Watch", 500.0);
        saveProduct("Samsung TV", 1000.0);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("name", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/v1/products/search")
                        .param("maxPrice", "600"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Apple Watch")));
    }

    private Product saveProduct(String name, double price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(price));
        product.setQuantity(10);
        return productsRepository.save(product);
    }

}

