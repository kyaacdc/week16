package com.smarthouse.pojo;

import javax.persistence.*;

@Entity
public class Visualization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int type;

    private String url;

    @ManyToOne
    @JoinColumn(name = "productCard")
    ProductCard productCard;

    public Visualization() {
    }

    public Visualization(int type, String url, ProductCard productCard) {
        this.type = type;
        this.url = url;
        this.productCard = productCard;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ProductCard getProductCard() {
        return productCard;
    }

    public void setProductCard(ProductCard productCard) {
        this.productCard = productCard;
    }
}
