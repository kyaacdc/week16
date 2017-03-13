package com.smarthouse.repository;

import com.smarthouse.pojo.Category;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface CategoryDao  extends Repository<Category, Integer> {
    Category save(Category category);
    void delete(Integer id);
    Category findById(Integer id);
    List<Category> findByDescriptionIgnoreCase(String description);
    List<Category> findByNameIgnoreCase(String name);
    List<Category> findByCategory(Category category);
}
