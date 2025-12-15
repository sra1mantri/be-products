package com.assignment.products.integration;

import com.assignment.products.entity.Order;
import com.assignment.products.entity.Product;
import com.assignment.products.entity.User;
import com.assignment.products.enums.Role;
import com.assignment.products.model.OrderRequestDTO;
import com.assignment.products.model.OrderRequestDTO.OrderItemRequestDTO;
import com.assignment.products.repository.OrderRepository;
import com.assignment.products.repository.ProductsRepository;
import com.assignment.products.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productsRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void placeOrder_ShouldSucceed_WhenStockIsAvailable() throws Exception {
        User customUser = saveAndReturnCustomer();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                customUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Product product = saveAndReturnProduct(BigDecimal.valueOf(100));

        OrderRequestDTO orderRequest = getOrderRequestDTO(product);

        mockMvc.perform(post("/api/v1/orders/place-order")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk());

        Product updatedProduct = productsRepository.findById(product.getId()).get();
        assertEquals(8, updatedProduct.getQuantity());
    }

    @Test
    void placeOrder_ShouldFail_WhenStockInsufficient() throws Exception {

        User customUser = saveAndReturnCustomer();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                customUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Product product = saveAndReturnProduct(BigDecimal.valueOf(100));

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setProductId(product.getId());
        orderItemRequestDTO.setQuantity(12);
        orderRequest.setOrderItems(List.of(orderItemRequestDTO));

        mockMvc.perform(post("/api/v1/orders/place-order")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isConflict());

        Product productInDb = productsRepository.findById(product.getId()).get();
        assertEquals(10, productInDb.getQuantity(), "Stock should not change on failed order");
    }

    @Test
    void placeOrder_ShouldApply_discount_successfully() throws Exception {

        User customUser = saveAndReturnCustomer();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                customUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Product product = saveAndReturnProduct(BigDecimal.valueOf(1000));

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setProductId(product.getId());
        orderItemRequestDTO.setQuantity(2);
        orderRequest.setOrderItems(List.of(orderItemRequestDTO));

        mockMvc.perform(post("/api/v1/orders/place-order")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        List<Order> orderInDB = orderRepository.findAll();
        assertEquals(BigDecimal.valueOf(1900.0).setScale(2), orderInDB.getFirst().getTotalPrice().setScale(2));
    }

    private static OrderRequestDTO getOrderRequestDTO(Product product) {
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        OrderItemRequestDTO orderItemRequestDTO = new OrderItemRequestDTO();
        orderItemRequestDTO.setProductId(product.getId());
        orderItemRequestDTO.setQuantity(2);
        orderRequest.setOrderItems(List.of(orderItemRequestDTO));
        return orderRequest;
    }

    private Product saveAndReturnProduct(BigDecimal price) {
        Product product = new Product();
        product.setName("In Stock Item");
        product.setPrice(price);
        product.setQuantity(10);
        product = productsRepository.save(product);
        return product;
    }

    private User saveAndReturnCustomer() {
        User customUser = new User();
        customUser.setUserName("admin");
        customUser.setPassword("password");
        customUser.setRole(Role.USER);
        customUser.setFirstName("Test");
        customUser.setLastName("User");
        customUser = userRepository.save(customUser);
        return customUser;
    }
}
