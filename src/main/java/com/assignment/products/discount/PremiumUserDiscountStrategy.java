package com.assignment.products.discount;

import com.assignment.products.entity.User;
import com.assignment.products.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PremiumUserDiscountStrategy implements DiscountStrategy{

    @Value("${spring.application.order.discount.premium-user}")
    String premiumUserDiscount;

    @Override
    public BigDecimal calculateDiscount(User user, BigDecimal orderTotal) {
        if (user.getRole() == Role.PREMIUM_USER) {
            return new BigDecimal(premiumUserDiscount);
        }
        return BigDecimal.ZERO;
    }
}
