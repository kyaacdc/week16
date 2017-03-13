package com.smarthouse.repository;

import com.smarthouse.pojo.OrderItem;
import com.smarthouse.pojo.OrderMain;
import com.smarthouse.pojo.ProductCard;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OrderItemDao extends Repository<OrderItem, Integer> {
    OrderItem save(OrderItem orderItem);
    List<OrderItem> findByOrderMain(OrderMain orderMain);
    List<OrderItem> findByProductCard(ProductCard productCard);
}
