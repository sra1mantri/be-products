package com.assignment.products.discount;

import com.assignment.products.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface DiscountStrategy {

    BigDecimal calculateDiscount(User user, BigDecimal orderTotal);
}
