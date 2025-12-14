package com.assignment.products.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequestDTO {

    @Schema(
            description = "List of items to purchase. Must not be empty.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    @Size(max = 50, message = "Cannot order more than 50 distinct items at once")
    List<OrderItemDTO> orderItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        @Schema(
                description = "The unique identifier of the product",
                example = "10023",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Product ID is mandatory")
        @Positive(message = "Product ID must be a positive number")
        private Long productId;

        @Schema(
                description = "Quantity to purchase. Must be between 1 and 100.",
                example = "2",
                minimum = "1",
                maximum = "100",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Quantity is mandatory")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 100, message = "Max quantity per item is 100")
        private Integer quantity;
    }
}
