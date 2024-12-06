package com.tcdq.project1_team4.Model;

import com.tcdq.project1_team4.Dao.ProductDao;

/**
 * @noinspection ALL
 */
public class ProductModel {
    private int id;
    private String name;
    private int idProductType; // ID của loại sản phẩm
    private int idBrand; // ID của thương hiệu

    // Constructor
    public ProductModel(int id, String name, int idProductType, int idBrand) {
        this.id = id;
        this.name = name;
        this.idProductType = idProductType;
        this.idBrand = idBrand;
    }

    public ProductModel() {
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdProductType() {
        return idProductType;
    }

    public int getIdBrand() {
        return idBrand;
    }

    // Cập nhật loại sản phẩm với ID từ Spinnerz
    public void setType(int idProductType) {
        this.idProductType = idProductType;
    }

    // Cập nhật thương hiệu với ID từ Spinner
    public void setBrand(int idBrand) {
        this.idBrand = idBrand;
    }

    public String getTypeName(ProductDao productDao) {
        return productDao.getTypeNameById(this.idProductType);
    }

    public String getBrandName(ProductDao productDao) {
        return productDao.getBrandNameById(this.idBrand);
    }
}
