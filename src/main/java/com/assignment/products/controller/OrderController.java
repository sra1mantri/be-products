package com.assignment.products.controller;

import com.assignment.products.entity.User;
import com.assignment.products.model.OrderRequestDTO;
import com.assignment.products.model.OrderResponseDTO;
import com.assignment.products.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Search/Filter all products",
            description = "Search/Filter all products based on User criteria."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returned product successfully based on Criteria"),
            @ApiResponse(responseCode = "401", description = "UnAuthorized - Authorization required")
    })
    @PostMapping("/place-order")
    public ResponseEntity<OrderResponseDTO> placeOrder(@AuthenticationPrincipal User currentUser,@RequestBody @Valid OrderRequestDTO orderRequest){
        return ResponseEntity.ok(orderService.placeOrder(currentUser, orderRequest));
    }
}
