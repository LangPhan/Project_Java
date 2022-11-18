package com.models;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "CATEGORY_NAME", nullable = false)
    private String name;
    @Column(name = "CREATED_AT", nullable = false)
    private String createdAt;
    @Column(name = "UPDATED_AT", nullable = false)
    private String updateAt;


    @OneToMany(mappedBy = "category")
    private Set<Product> product;

    public Category() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        this.createdAt = dateFormat.format(date);
        this.updateAt = dateFormat.format(date);
    }

    public Set<Product> getProduct() {
        return product;
    }


    public void setProduct(Set<Product> product) {
        this.product = product;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }


}
