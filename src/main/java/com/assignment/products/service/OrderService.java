package com.assignment.products.service;

import com.assignment.products.discount.DiscountStrategy;
import com.assignment.products.entity.Order;
import com.assignment.products.entity.OrderItem;
import com.assignment.products.entity.Product;
import com.assignment.products.entity.User;
import com.assignment.products.exception.NotStockAvailableException;
import com.assignment.products.mapper.OrderMapper;
import com.assignment.products.model.OrderRequestDTO;
import com.assignment.products.model.OrderRequestDTO.OrderItemRequestDTO;
import com.assignment.products.model.OrderResponseDTO;
import com.assignment.products.model.PrepareOrderItem;
import com.assignment.products.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductsService productsService;

    private final OrderMapper orderMapper;

    private final List<DiscountStrategy> discountStrategies;

    @Transactional
    public OrderResponseDTO placeOrder(User user, OrderRequestDTO orderRequestDTO){

        log.info("Starting Place order for User: {}, Items: {}", user.getId(), orderRequestDTO.getOrderItems().size());
        Order order = new Order();

        List<OrderItem> orderItems = new ArrayList<>();
        List<PrepareOrderItem> prepareOrderItems = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : orderRequestDTO.getOrderItems()) {
            Product product = productsService.findProductById(itemDto.getProductId());
            if(product.getQuantity() < itemDto.getQuantity()){
                log.error("Order Creation failed. Product {} (ID: {}) has insufficient stock. Requested: {}, Available: {}"
                        ,product.getName(), product.getId(), itemDto.getQuantity(), product.getQuantity());
                throw new NotStockAvailableException("Insufficient Stock for the product: "+ product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            subTotal = subTotal.add(itemTotal);

            PrepareOrderItem prepareOrderItem = new PrepareOrderItem();
            prepareOrderItem.setProductId(product.getId());
            prepareOrderItem.setQuantity(itemDto.getQuantity());
            prepareOrderItem.setUnitPrice(product.getPrice());
            prepareOrderItem.setTotalPrice(itemTotal);
            prepareOrderItem.setProduct(product);
            prepareOrderItems.add(prepareOrderItem);

        }

        BigDecimal totalDiscountPercent = BigDecimal.ZERO;
        for (DiscountStrategy strategy : discountStrategies) {
            totalDiscountPercent = totalDiscountPercent.add(
                    strategy.calculateDiscount(user, subTotal)
            );
        }
        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        for(PrepareOrderItem preparedItem: prepareOrderItems){
            productsService.reduceProductStock(preparedItem.getProduct(), preparedItem.getQuantity());
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(preparedItem.getProduct());
            orderItem.setQuantity(preparedItem.getQuantity());
            orderItem.setUnitPrice(preparedItem.getUnitPrice());

            BigDecimal rawItemTotal = preparedItem.getTotalPrice();
            BigDecimal itemDiscount = rawItemTotal.multiply(totalDiscountPercent);
            BigDecimal itemFinalPrice = rawItemTotal.subtract(itemDiscount);

            orderItem.setDiscountApplied(itemDiscount);
            orderItem.setTotalPrice(itemFinalPrice);
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            totalOrderPrice = totalOrderPrice.add(itemFinalPrice);
        }

        order.setItems(orderItems);
        order.setUser(user);
        order.setTotalPrice(totalOrderPrice);

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully. Order Id {}, Total {} ", savedOrder.getId(), savedOrder.getTotalPrice());
        return orderMapper.convertToOrderResponseDTO(savedOrder);
    }
}
