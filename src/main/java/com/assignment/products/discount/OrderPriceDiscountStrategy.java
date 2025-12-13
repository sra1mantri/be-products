package com.assignment.products.discount;

import com.assignment.products.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderPriceDiscountStrategy implements DiscountStrategy{

    @Override
    public BigDecimal calculateDiscount(User user, BigDecimal orderTotal) {
        if (orderTotal.compareTo(new BigDecimal("500.00")) > 0) {
            return new BigDecimal("0.05");
        }
        return BigDecimal.ZERO;
    }
}
