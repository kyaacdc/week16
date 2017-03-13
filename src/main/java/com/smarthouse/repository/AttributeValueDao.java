package com.smarthouse.repository;

import com.smarthouse.pojo.AttributeName;
import com.smarthouse.pojo.AttributeValue;
import com.smarthouse.pojo.ProductCard;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface AttributeValueDao extends Repository<AttributeValue, Integer> {
    AttributeValue save(AttributeValue attributeValue);
    List<AttributeValue> findByProductCard(ProductCard productCard);
    List<AttributeValue> findByAttributeName(AttributeName attributeName);
}
