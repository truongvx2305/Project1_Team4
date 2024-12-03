package com.tcdq.project1_team4.Model;

public class BrandModel {
    private int idBrand;
    private String name;

    // Constructor mặc định
    public BrandModel() {
    }

    // Constructor với tham số
    public BrandModel(int idBrand, String name) {
        this.idBrand = idBrand;
        this.name = name;
    }

    // Getter và Setter cho ID_Brand
    public int getIdBrand() {
        return idBrand;
    }

    public void setIdBrand(int idBrand) {
        this.idBrand = idBrand;
    }

    // Getter và Setter cho Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
