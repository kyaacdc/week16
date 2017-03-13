package com.smarthouse.repository;

import com.smarthouse.pojo.AttributeName;
import org.springframework.data.repository.Repository;

public interface AttributeNameDao extends Repository<AttributeName, String> {
    AttributeName save(AttributeName attributeName);
}
