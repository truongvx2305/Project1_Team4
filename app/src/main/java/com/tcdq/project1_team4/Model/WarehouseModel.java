package com.tcdq.project1_team4.Model;

import com.tcdq.project1_team4.Dao.WarehouseDao;

/** @noinspection ALL */
public class WarehouseModel {
    private int idProduct;
    private byte[] image;
    private int idColor;
    private int idSize;
    private int quantity;
    private String entryDate;
    private double entryPrice;
    private String exitDate;
    private double exitPrice;
    private boolean isStill;

    /** @noinspection unused*/
    public WarehouseModel() {
    }

    public WarehouseModel(int idProduct, byte[] image, int idColor, int idSize, int quantity, String entryDate, double entryPrice, String exitDate, double exitPrice, boolean isStill) {
        this.idProduct = idProduct;
        this.image = image;
        this.idColor = idColor;
        this.idSize = idSize;
        this.quantity = quantity;
        this.entryDate = entryDate;
        this.entryPrice = entryPrice;
        this.exitDate = exitDate;
        this.exitPrice = exitPrice;
        this.isStill = isStill;
    }

    public int getIdProduct() {
        return idProduct;
    }

    /** @noinspection unused*/
    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public byte[] getImage() {
        return image;
    }

    /** @noinspection unused*/
    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getIdColor() {
        return idColor;
    }

    /** @noinspection unused*/
    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }

    public int getIdSize() {
        return idSize;
    }

    /** @noinspection unused*/
    public void setIdSize(int idSize) {
        this.idSize = idSize;
    }

    public int getQuantity() {
        return quantity;
    }

    /** @noinspection unused*/
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getEntryDate() {
        return entryDate;
    }

    /** @noinspection unused*/
    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    /** @noinspection unused*/
    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    /** @noinspection unused*/
    public String getExitDate() {
        return exitDate;
    }

    /** @noinspection unused*/
    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }

    public double getExitPrice() {
        return exitPrice;
    }

    /** @noinspection unused*/
    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }

    public boolean isStill() {
        return isStill;
    }

    /** @noinspection unused*/
    public void setStill(boolean still) {
        isStill = still;
    }

    /** @noinspection unused*/
    public String getStatus() {
        return this.isStill ? "Còn hàng" : "Hết hàng";
    }

    public String getColor(WarehouseDao warehouseDao) {
        return warehouseDao.getColorNameById(this.idColor);
    }

    public String getSize(WarehouseDao warehouseDao) {
        return warehouseDao.getSizeNameById(this.idSize);
    }

    public String getName(WarehouseDao warehouseDao) {
        return warehouseDao.getNameById(this.idProduct);
    }


}
