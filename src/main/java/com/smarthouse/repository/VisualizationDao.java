package com.smarthouse.repository;

import com.smarthouse.pojo.ProductCard;
import com.smarthouse.pojo.Visualization;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface VisualizationDao extends Repository<Visualization, Integer> {
    Visualization save(Visualization visualization);
    List<Visualization> findByProductCard(ProductCard productCard);
}
