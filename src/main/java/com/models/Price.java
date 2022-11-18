package com.models;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "SIZE_S", nullable = false)
    private double sizeS;
    @Column(name = "SIZE_M", nullable = false)
    private double sizeM;
    @Column(name = "SIZE_L", nullable = false)
    private double sizeL;
    @Column(name = "CREATED_AT", nullable = false)
    private String createdAt;
    @Column(name = "UPDATED_AT", nullable = false)
    private String updateAt;

    @OneToOne(mappedBy = "price")
    private Product product;

    public Price(double sizeS, double sizeM, double sizeL) {
        this.sizeS = sizeS;
        this.sizeM = sizeM;
        this.sizeL = sizeL;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        this.createdAt = dateFormat.format(date);
        this.updateAt = dateFormat.format(date);
    }

    public Price() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        this.createdAt = dateFormat.format(date);
        this.updateAt = dateFormat.format(date);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getSizeS() {
        return sizeS;
    }

    public void setSizeS(double sizeS) {
        this.sizeS = sizeS;
    }

    public double getSizeM() {
        return sizeM;
    }

    public void setSizeM(double sizeM) {
        this.sizeM = sizeM;
    }

    public double getSizeL() {
        return sizeL;
    }

    public void setSizeL(double sizeL) {
        this.sizeL = sizeL;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
