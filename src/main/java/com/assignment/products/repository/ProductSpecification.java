package com.assignment.products.repository;

import com.assignment.products.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class ProductSpecification {

    public Specification<Product> filterProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, boolean onlyAvailable, boolean isAdmin) {
        return (root, query, cb) -> {
            Specification<Product> spec = Specification.unrestricted();

            if (StringUtils.hasText(name)) {
                spec = spec.and((r, q, b) -> b.like(b.lower(r.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (minPrice != null) {
                spec = spec.and((r, q, b) -> b.greaterThanOrEqualTo(r.get("price"), minPrice));
            }
            if (maxPrice != null) {
                spec = spec.and((r, q, b) -> b.lessThanOrEqualTo(r.get("price"), maxPrice));
            }
            if (onlyAvailable) {
                spec = spec.and((r, q, b) -> b.greaterThan(r.get("quantity"), 0));
            }
            if (!isAdmin) {
                spec = spec.and((r, q, b) -> b.isFalse(r.get("isDeleted")));
            }

            return spec.toPredicate(root, query, cb);
        };
    }
}
