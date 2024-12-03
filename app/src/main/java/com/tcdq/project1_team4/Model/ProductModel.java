package com.tcdq.project1_team4.Model;

import com.tcdq.project1_team4.Dao.ProductDao;

/** @noinspection ALL*/
public class ProductModel {
    private int id;
    private byte[] image;
    private String name;
    private int idProductType; // ID của loại sản phẩm
    private int idBrand; // ID của thương hiệu

    // Constructor
    public ProductModel(int id, byte[] image, String name, int idProductType, int idBrand) {
        this.id = id;
        this.image = image;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

// --Commented out by Inspection START (12/3/2024 6:07 PM):
//    public void setIdProductType(int idProductType) {
//        this.idProductType = idProductType;
//    }
// --Commented out by Inspection STOP (12/3/2024 6:07 PM)

    public int getIdBrand() {
        return idBrand;
    }

// --Commented out by Inspection START (12/3/2024 6:07 PM):
//    public void setIdBrand(int idBrand) {
//        this.idBrand = idBrand;
//    }
// --Commented out by Inspection STOP (12/3/2024 6:07 PM)

    // Cập nhật loại sản phẩm với ID từ Spinnerz
    public void setType(int idProductType) {
        this.idProductType = idProductType;
    }

    // Cập nhật thương hiệu với ID từ Spinner
    public void setBrand(int idBrand) {
        this.idBrand = idBrand;
    }

// --Commented out by Inspection START (12/3/2024 6:07 PM):
//    public String getBrandName(ProductDao productDao) {
// --Commented out by Inspection START (12/3/2024 6:07 PM):
////        return productDao.getBrandNameById(this.idBrand);
////    }
//// --Commented out by Inspection STOP (12/3/2024 6:07 PM)
//
//    // Thêm phương thức để lấy tên loại sản phẩm
// --Commented out by Inspection STOP (12/3/2024 6:07 PM)
    public String getTypeName(ProductDao productDao) {
        return productDao.getTypeNameById(this.idProductType);
    }
}
