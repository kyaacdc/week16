package com.smarthouse.pojo;

import javax.persistence.*;

@Entity
public class OrderMain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    private String address;

    private int status;

    @ManyToOne
    @JoinColumn(name = "customer", nullable = false)
    Customer customer;

    public OrderMain() {
    }

    public OrderMain(String address, int status, Customer customer) {
        this.address = address;
        this.status = status;
        this.customer = customer;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
