package com.assignment.products.discount;

import com.assignment.products.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderPriceDiscountStrategy implements DiscountStrategy{

    @Value("${spring.application.order.discount.minimum-price}")
    String minimumOrderPriceForDiscount;

    @Value("${spring.application.order.discount.order-amount}")
    String orderPriceDiscount;

    @Override
    public BigDecimal calculateDiscount(User user, BigDecimal orderTotal) {
        if (orderTotal.compareTo(new BigDecimal(minimumOrderPriceForDiscount)) > 0) {
            return new BigDecimal(orderPriceDiscount);
        }
        return BigDecimal.ZERO;
    }
}
