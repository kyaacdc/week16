package com.smarthouse.pojo;

import javax.persistence.*;

@Entity
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "attributename", nullable = false)
    AttributeName attributeName;

    @ManyToOne
    @JoinColumn(name = "productcard")
    ProductCard productCard;

    public AttributeValue() {
    }

    public AttributeValue(String value, AttributeName attributeName, ProductCard productCard) {
        this.value = value;
        this.attributeName = attributeName;
        this.productCard = productCard;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AttributeName getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(AttributeName attributeName) {
        this.attributeName = attributeName;
    }

    public ProductCard getProductCard() {
        return productCard;
    }

    public void setProductCard(ProductCard productCard) {
        this.productCard = productCard;
    }
}
