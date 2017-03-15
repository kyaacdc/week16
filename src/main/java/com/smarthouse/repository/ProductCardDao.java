package com.smarthouse.repository;

import com.smarthouse.pojo.Category;
import com.smarthouse.pojo.ProductCard;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ProductCardDao  extends Repository<ProductCard, String> {

    ProductCard save(ProductCard productCard);
    void delete(String sku);
    ProductCard findBySku(String sku);
    Boolean exists(String sku);
    List<ProductCard> findByNameIgnoreCase(String name);
    List<ProductCard> findByProductDescriptionIgnoreCase(String productDescription);
    List<ProductCard> findByCategory(Category category);
    List<ProductCard> findByCategory(Category category, Sort sort);
    List<ProductCard> findAllBy(Sort sort);
}
