package com.assignment.products.discount;

import com.assignment.products.entity.User;
import com.assignment.products.enums.Role;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PremiumUserDiscountStrategy implements DiscountStrategy{

    @Override
    public BigDecimal calculateDiscount(User user, BigDecimal orderTotal) {
        if (user.getRole() == Role.PREMIUM_USER) {
            return new BigDecimal("0.10");
        }
        return BigDecimal.ZERO;
    }
}
