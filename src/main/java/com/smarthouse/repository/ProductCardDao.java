package com.smarthouse.repository;

import com.smarthouse.pojo.Category;
import com.smarthouse.pojo.ProductCard;
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
    List<ProductCard> findByCategoryOrderByNameDesc(Category category);
    List<ProductCard> findByCategoryOrderByNameAsc(Category category);
    List<ProductCard> findByCategoryOrderByPriceDesc(Category category);
    List<ProductCard> findByCategoryOrderByPriceAsc(Category category);
    List<ProductCard> findByCategoryOrderByLikes(Category category);
    List<ProductCard> findByCategoryOrderByDislikes(Category category);
    List<ProductCard> findAllByOrderByNameAsc();
    List<ProductCard> findAllByOrderByNameDesc();
    List<ProductCard> findAllByOrderByPriceAsc();
    List<ProductCard> findAllByOrderByPriceDesc();
    List<ProductCard> findAllByOrderByLikes();
    List<ProductCard> findAllByOrderByDislikes();
}
