package com.smarthouse.pojo;

import javax.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int amount;

    private int totalprice;

    @ManyToOne
    @JoinColumn(name = "productCard")
    ProductCard productCard;

    @ManyToOne
    @JoinColumn(name = "orderMain", nullable = false)
    OrderMain orderMain;

    public OrderItem() {
    }

    public OrderItem(int amount, int totalprice, ProductCard productCard, OrderMain orderMain) {
        this.amount = amount;
        this.totalprice = totalprice;
        this.productCard = productCard;
        this.orderMain = orderMain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public ProductCard getProductCard() {
        return productCard;
    }

    public void setProductCard(ProductCard productCard) {
        this.productCard = productCard;
    }

    public OrderMain getOrderMain() {
        return orderMain;
    }

    public void setOrderMain(OrderMain orderMain) {
        this.orderMain = orderMain;
    }
}
