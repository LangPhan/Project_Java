package com.models;

import javax.persistence.*;

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


}
