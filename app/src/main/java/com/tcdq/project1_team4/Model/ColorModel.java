package com.tcdq.project1_team4.Model;

public class ColorModel {
    private int idColor;
    private String colorName;

    public ColorModel() {
    }

    public ColorModel(int idColor, String colorName) {
        this.idColor = idColor;
        this.colorName = colorName;
    }

    public int getIdColor() {
        return idColor;
    }

    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }
}
