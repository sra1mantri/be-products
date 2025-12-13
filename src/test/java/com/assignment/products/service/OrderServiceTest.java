package com.assignment.products.service;

import com.assignment.products.discount.DiscountStrategy;
import com.assignment.products.entity.Order;
import com.assignment.products.entity.Product;
import com.assignment.products.entity.User;
import com.assignment.products.mapper.OrderMapper;
import com.assignment.products.model.OrderRequestDTO;
import com.assignment.products.model.OrderRequestDTO.OrderItemDTO;
import com.assignment.products.model.OrderResponseDTO;
import com.assignment.products.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private ProductsService productService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private DiscountStrategy discountStrategy;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        List<DiscountStrategy> strategies = Collections.singletonList(discountStrategy);
        orderService = new OrderService(orderRepository, productService, orderMapper, strategies);
    }

    @Test
    void placeOrder_ShouldCalculateTotalAndSave_WhenStockIsAvailable() {
        User user = new User();
        user.setId(1L);
        user.setUserName("user");

        OrderRequestDTO.OrderItemDTO itemDto = new OrderItemDTO(101L, 2);
        OrderRequestDTO request = new OrderRequestDTO(Collections.singletonList(itemDto));

        Product product = new Product();
        product.setId(101L);
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(10);
        product.setName("Test Product");

        when(productService.findProductById(101L)).thenReturn(product);
        when(discountStrategy.calculateDiscount(any(), any())).thenReturn(new BigDecimal("0.10"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.convertToOrderResponseDTO(any())).thenReturn(new OrderResponseDTO());

        orderService.placeOrder(user, request);

        verify(productService, times(1)).reduceProductStock(product, 2);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void placeOrder_ShouldFail_WhenStockInsufficient() {
        User user = new User();
        user.setId(1L);
        user.setUserName("user");

        OrderRequestDTO.OrderItemDTO itemDto = new OrderItemDTO(101L, 20);
        OrderRequestDTO request = new OrderRequestDTO(Collections.singletonList(itemDto));

        Product product = new Product();
        product.setId(101L);
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(5);
        product.setName("Test Product");

        when(productService.findProductById(101L)).thenReturn(product);

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(user, request));

        verify(orderRepository, never()).save(any());
        verify(productService, never()).reduceProductStock(any(), anyInt());
    }
}
