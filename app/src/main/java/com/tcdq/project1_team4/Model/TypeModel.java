package com.tcdq.project1_team4.Model;

public class TypeModel {
    private int idType;         // ID của loại sản phẩm
    private String typeName;    // Tên loại sản phẩm (Ví dụ: "Áo", "Quần", "Phụ kiện")

    // Constructor mặc định
    public TypeModel() {
    }

    // Constructor với tham số
    public TypeModel(int idType, String typeName) {
        this.idType = idType;
        this.typeName = typeName;
    }

    // Getter và Setter cho ID_Type
    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    // Getter và Setter cho TypeName
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
