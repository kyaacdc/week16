package com.smarthouse.pojo;

import com.smarthouse.service.util.validators.Name;
import com.smarthouse.service.util.validators.Phone;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {

    @Id
    private String email;

    @Name
    private String name;

    private boolean subscribe;

    @Phone
    private String phone;

    public Customer() {
    }

    public Customer(String email, String name, boolean subscribe, String phone) {
        this.email = email;
        this.name = name;
        this.subscribe = subscribe;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
