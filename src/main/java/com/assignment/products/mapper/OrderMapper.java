package com.assignment.products.mapper;

import com.assignment.products.entity.Order;
import com.assignment.products.entity.OrderItem;
import com.assignment.products.model.OrderResponseDTO;
import com.assignment.products.model.OrderResponseDTO.OrderItemResponseDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDTO convertToOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderItems(order.getItems().stream().map(this::convertToDto).collect(Collectors.toList()))
                .totalOrderPrice(order.getTotalPrice())
                .build();
    }

    private OrderItemResponseDTO convertToDto(OrderItem orderItem) {
        return OrderItemResponseDTO.builder()
                .productId(orderItem.getProduct().getId())
                .quantity(orderItem.getQuantity())
                .productName(orderItem.getProduct().getName())
                .build();
    }
}
