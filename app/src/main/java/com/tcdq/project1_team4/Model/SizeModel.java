package com.tcdq.project1_team4.Model;

public class SizeModel {
    private int idSize;
    private String sizeName;

    // Constructor mặc định
    public SizeModel() {
    }

    // Constructor với tham số
    public SizeModel(int idSize, String sizeName) {
        this.idSize = idSize;
        this.sizeName = sizeName;
    }

    // Getter và Setter cho ID_Size
    public int getIdSize() {
        return idSize;
    }

    public void setIdSize(int idSize) {
        this.idSize = idSize;
    }

    // Getter và Setter cho SizeName
    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }
}
