package com.flyxia.flytalk.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author automannn@163.com
 * @time 2019/5/19 11:46
 */
@Entity
@Table(name = "tb_product")
public class Product {
    @Id
    private String code;

    @Column(name = "price",columnDefinition = "double(10,2) default '0.00'")
    @NotBlank
    private Double price;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    public Product() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
