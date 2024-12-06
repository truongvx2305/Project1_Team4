package com.tcdq.project1_team4.Model;

import com.tcdq.project1_team4.Dao.ProductDao;
import com.tcdq.project1_team4.Dao.WarehouseDao;
import com.tcdq.project1_team4.Function.Management.Warehouse;

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

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getIdColor() {
        return idColor;
    }

    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }

    public int getIdSize() {
        return idSize;
    }

    public void setIdSize(int idSize) {
        this.idSize = idSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public String getExitDate() {
        return exitDate;
    }

    public void setExitDate(String exitDate) {
        this.exitDate = exitDate;
    }

    public double getExitPrice() {
        return exitPrice;
    }

    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }

    public boolean isStill() {
        return isStill;
    }

    public void setStill(boolean still) {
        isStill = still;
    }

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
