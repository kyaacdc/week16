package com.smarthouse.repository;

import com.smarthouse.pojo.Customer;
import com.smarthouse.pojo.OrderMain;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OrderMainDao extends Repository<OrderMain, Integer> {
    OrderMain save(OrderMain orderMain);
    List<OrderMain> findByCustomer(Customer customer);
    OrderMain findByOrderId(Integer orderId);
}
