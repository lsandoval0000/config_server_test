package com.learning.service;

import com.learning.dto.OrderLineItemDto;
import com.learning.dto.OrderRequest;
import com.learning.feign.InventoryServiceClient;
import com.learning.model.Order;
import com.learning.model.OrderLineItem;
import com.learning.repository.OrderRepository;
import com.learning.feign.InventoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceClient inventoryServiceClient;

    @Transactional
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItemList = orderRequest.getOrderLineItemDtoList().stream().
                map(OrderService::mapOrderRequestToOrder).collect(Collectors.toList());
        order.setOrderLineItemList(orderLineItemList);

        List<String> skuCodes = order.getOrderLineItemList()
                .stream()
                .map(OrderLineItem::getSkuCode).collect(Collectors.toList());

        // Call Inventory Service and place order if product is in stock
        List<InventoryResponse> productsInStock = inventoryServiceClient.isInStock(skuCodes);

        boolean allProductsInStock = productsInStock.stream().allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
            return "Order Placed Successfully";
        } else {
            throw new IllegalArgumentException("Some Products are not in stock, please try again later.");
        }

    }

    private static OrderLineItem mapOrderRequestToOrder(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .skuCode(orderLineItemDto.getSkuCode()).build();
    }
}
