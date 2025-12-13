package com.assignment.products.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ProductUpdateRequestDTO {

    @Schema(description = "New name (optional)", example = "Iphone 15 Pro")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name;

    @Schema(description = "New description (optional)", example = "Updated description")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description;

    @Schema(description = "New price (optional)", example = "1099.99")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
    BigDecimal price;

    @Schema(description = "New stock quantity (optional)", example = "100")
    @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantity;

    @JsonIgnore
    @AssertTrue(message = "At least one field (name, description, price, or quantity) must be provided to update")
    public boolean isAtLeastOneFieldPresent() {
        return name != null || description != null || price != null || quantity != null;
    }
}
