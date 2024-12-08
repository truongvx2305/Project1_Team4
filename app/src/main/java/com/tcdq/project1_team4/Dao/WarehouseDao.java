package com.tcdq.project1_team4.Dao;

import static com.tcdq.project1_team4.DB.DatabaseHelper.brandTable;
import static com.tcdq.project1_team4.DB.DatabaseHelper.colorTable;
import static com.tcdq.project1_team4.DB.DatabaseHelper.productTable;
import static com.tcdq.project1_team4.DB.DatabaseHelper.productTypeTable;
import static com.tcdq.project1_team4.DB.DatabaseHelper.sizeTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tcdq.project1_team4.Model.WarehouseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection ALL
 */
public class WarehouseDao {
    private final SQLiteDatabase db;

    public WarehouseDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm sản phẩm
    public boolean insert(WarehouseModel warehouse) {
        ContentValues values = new ContentValues();
        values.put("idProduct", warehouse.getIdProduct());
        values.put("Image", warehouse.getImage());
        values.put("idColor", warehouse.getIdColor());
        values.put("idSize", warehouse.getIdSize());
        values.put("quantity", warehouse.getQuantity());
        values.put("Entry_Date", warehouse.getEntryDate());
        values.put("Entry_Price", warehouse.getEntryPrice());
        values.put("Exit_Price", warehouse.getExitPrice());
        values.put("isStill", warehouse.isStill() ? 1 : 0);

        long result = db.insert("warehouse", null, values);
        return result != -1;
    }

    public boolean update(WarehouseModel warehouse) {
        if (warehouse == null || warehouse.getIdProduct() <= 0) {
            Log.e("Update", "Dữ liệu sản phẩm không hợp lệ.");
            return false;
        }

        // Tính tổng số lượng mới
        int newTotalQuantity = warehouse.getQuantity();

        // Xác định trạng thái isValid (1: còn hàng, 0: hết hàng)
        int isStill = newTotalQuantity > 0 ? 1 : 0;

        // Tạo ContentValues để lưu trữ các giá trị cần cập nhật
        ContentValues values = new ContentValues();
        values.put("Quantity", newTotalQuantity);
        values.put("Entry_Date", warehouse.getEntryDate());
        values.put("Entry_Price", warehouse.getEntryPrice());
        values.put("Exit_Price", warehouse.getExitPrice());
        values.put("isStill", isStill);

        // Thực hiện câu lệnh cập nhật dữ liệu trong cơ sở dữ liệu
        int result = db.update("warehouse", values, "ID_Product = ?", new String[]{String.valueOf(warehouse.getIdProduct())});

        if (result > 0) {
            Log.d("Update", "Cập nhật thông tin sản phẩm thành công.");
            return true; // Cập nhật thành công
        } else {
            Log.d("Update", "Không có bản ghi nào được cập nhật.");
            return false; // Không có dòng nào được cập nhật
        }
    }

    public boolean delete(int productId) {
        int result = db.delete("warehouse", "ID_Product = ?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    // Lấy danh sách sản phẩm
    public List<WarehouseModel> getAllProduct() {
        List<WarehouseModel> products = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM warehouse", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Product"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("Image"));
                int idColor = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Color"));
                int idSize = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Size"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("Quantity"));
                String entryDate = cursor.getString(cursor.getColumnIndexOrThrow("Entry_Date"));
                double entryPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("Entry_Price"));
                String exitDate = cursor.getString(cursor.getColumnIndexOrThrow("Exit_Date"));
                double exitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("Exit_Price"));
                boolean isStill = cursor.getInt(cursor.getColumnIndexOrThrow("isStill")) == 1;

                WarehouseModel product = new WarehouseModel(id, image, idColor, idSize, quantity, entryDate, entryPrice, exitDate, exitPrice, isStill);
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public List<String> getAllColor() {
        List<String> typeList = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT DISTINCT Name FROM " + colorTable, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int columnIndex = cursor.getColumnIndex("Name");
                    if (columnIndex != -1) {
                        String typeName = cursor.getString(columnIndex);
                        typeList.add(typeName);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error while fetching color", e);
        }
        return typeList;
    }

    public List<String> getAllSize() {
        List<String> brands = new ArrayList<>();
        String query = "SELECT DISTINCT Name FROM " + sizeTable;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Sử dụng đúng tên cột
                String brand = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                brands.add(brand);
            }
            cursor.close();
        }
        return brands;
    }

    public List<String> getAllName() {
        List<String> names = new ArrayList<>();
        String query = "SELECT DISTINCT Name FROM " + productTable;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Sử dụng đúng tên cột
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                names.add(name);
            }
            cursor.close();
        }
        return names;
    }

    public int getColorIdByName(String name) {
        int colorId = -1; // Giá trị mặc định nếu không tìm thấy
        Cursor cursor = null;
        try {
            String query = "SELECT ID_Color FROM " + colorTable + " WHERE Name = ?";
            cursor = db.rawQuery(query, new String[]{name});
            if (cursor != null && cursor.moveToFirst()) {
                colorId = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Color"));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getColorIdByName", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return colorId;
    }

    public int getSizeIdByName(String name) {
        int sizeId = -1; // Giá trị mặc định nếu không tìm thấy
        Cursor cursor = null;
        try {
            String query = "SELECT ID_Size FROM " + sizeTable + " WHERE Name = ?";
            cursor = db.rawQuery(query, new String[]{name});
            if (cursor != null && cursor.moveToFirst()) {
                sizeId = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Size"));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getSizeIdByName", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sizeId;
    }

    public int getProductIdByName(String name) {
        int productId = -1; // Giá trị mặc định nếu không tìm thấy
        Cursor cursor = null;
        try {
            String query = "SELECT ID_Product FROM " + productTable + " WHERE Name = ?";
            cursor = db.rawQuery(query, new String[]{name});
            if (cursor != null && cursor.moveToFirst()) {
                productId = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Product"));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getProductIdByName", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productId;
    }

    /**
     * @noinspection unused
     */
    public String getColorNameById(int colorId) {
        String colorName = null;
        Cursor cursor = db.rawQuery("SELECT Name FROM color WHERE ID_Color = ?", new String[]{String.valueOf(colorId)});
        if (cursor != null && cursor.moveToFirst()) {
            colorName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            cursor.close();
        }
        return colorName;
    }

    public String getSizeNameById(int sizeId) {
        String sizeName = null;
        Cursor cursor = db.rawQuery("SELECT Name FROM size WHERE ID_Size = ?", new String[]{String.valueOf(sizeId)});
        if (cursor != null && cursor.moveToFirst()) {
            sizeName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            cursor.close();
        }
        return sizeName;
    }

    public String getNameById(int productId) {
        String productName = null;
        Cursor cursor = db.rawQuery("SELECT Name FROM product WHERE ID_Product = ?", new String[]{String.valueOf(productId)});
        if (cursor != null && cursor.moveToFirst()) {
            productName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            cursor.close();
        }
        return productName;
    }

    /**
     * @noinspection unused
     */
    public boolean updateProductImage(int productId, byte[] imageBytes) {
        ContentValues values = new ContentValues();
        values.put("Image", imageBytes);
        int rows = db.update("warehouse", values, "ID_Product = ?", new String[]{String.valueOf(productId)});
        return rows > 0;
    }

    public int getProductCountByColorId(int colorId) {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Count FROM warehouse WHERE ID_Color = ?", new String[]{String.valueOf(colorId)});
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndexOrThrow("Count"));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getProductCountByColorId", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    public int getProductCountBySizeId(int sizeId) {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS Count FROM warehouse WHERE ID_Size = ?", new String[]{String.valueOf(sizeId)});
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndexOrThrow("Count"));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getProductCountBySizeId", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    public String[] getBrandAndTypeByName(String name) {
        String[] result = new String[2]; // result[0] = type, result[1] = brand
        Cursor cursor = null;
        try {
            String query = "SELECT pt.Name AS ProductType, b.Name AS BrandName " +
                    "FROM " + productTable + " p " +
                    "JOIN " + productTypeTable + " pt ON p.ID_ProductType = pt.ID_ProductType " +
                    "JOIN " + brandTable + " b ON p.ID_Brand = b.ID_Brand " +
                    "WHERE p.Name = ?";
            cursor = db.rawQuery(query, new String[]{name});
            if (cursor != null && cursor.moveToFirst()) {
                result[0] = cursor.getString(cursor.getColumnIndexOrThrow("ProductType"));
                result[1] = cursor.getString(cursor.getColumnIndexOrThrow("BrandName"));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getBrandAndTypeByName", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public String[] getBrandAndTypeByProductId(int productId) {
        String query = "SELECT " +
                brandTable + ".Name AS BrandName, " +
                productTypeTable + ".Name AS TypeName " +
                "FROM " + productTable + " " +
                "INNER JOIN " + brandTable + " ON " + productTable + ".ID_Brand = " + brandTable + ".ID_Brand " +
                "INNER JOIN " + productTypeTable + " ON " + productTable + ".ID_ProductType = " + productTypeTable + ".ID_ProductType " +
                "WHERE " + productTable + ".ID_Product = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String brand = cursor.getString(cursor.getColumnIndexOrThrow("BrandName"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("TypeName"));
                    return new String[]{type, brand};
                }
            } finally {
                cursor.close(); // Đảm bảo cursor luôn được đóng
            }
        }
        return null;
    }

    public boolean insertOrUpdateProduct(WarehouseModel warehouse) {
        if (warehouse == null) {
            Log.e("InsertOrUpdate", "Warehouse data is null.");
            return false;
        }

        Cursor cursor = db.rawQuery(
                "SELECT ID_Product, Quantity FROM warehouse WHERE ID_Product = ? AND ID_Color = ? AND ID_Size = ?",
                new String[]{
                        String.valueOf(warehouse.getIdProduct()),
                        String.valueOf(warehouse.getIdColor()),
                        String.valueOf(warehouse.getIdSize())
                });

        try {
            if (cursor != null && cursor.moveToFirst()) {
                // Sản phẩm đã tồn tại, cập nhật số lượng và thông tin
                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("Quantity"));

                // Cập nhật số lượng mới
                int newQuantity = currentQuantity + warehouse.getQuantity();

                // Kiểm tra số lượng để quyết định trạng thái still
                boolean isStill = newQuantity > 0;

                // Tạo đối tượng ContentValues để cập nhật
                ContentValues values = new ContentValues();
                values.put("Image", warehouse.getImage());
                values.put("Quantity", newQuantity);
                values.put("Entry_Date", warehouse.getEntryDate());
                values.put("Entry_Price", warehouse.getEntryPrice());
                values.put("Exit_Price", warehouse.getExitPrice());
                values.put("isStill", isStill ? 1 : 0);  // Cập nhật trạng thái still

                int rows = db.update(
                        "warehouse",
                        values,
                        "ID_Product = ? AND ID_Color = ? AND ID_Size = ?",
                        new String[]{
                                String.valueOf(warehouse.getIdProduct()),
                                String.valueOf(warehouse.getIdColor()),
                                String.valueOf(warehouse.getIdSize())
                        });

                if (rows > 0) {
                    Log.d("InsertOrUpdate", "Updated existing product successfully.");
                    return true;
                } else {
                    Log.e("InsertOrUpdate", "Failed to update existing product.");
                    return false;
                }
            } else {
                // Sản phẩm chưa tồn tại, thêm mới
                ContentValues values = new ContentValues();
                values.put("ID_Product", warehouse.getIdProduct());
                values.put("Image", warehouse.getImage());
                values.put("ID_Color", warehouse.getIdColor());
                values.put("ID_Size", warehouse.getIdSize());
                values.put("Quantity", warehouse.getQuantity());
                values.put("Entry_Date", warehouse.getEntryDate());
                values.put("Entry_Price", warehouse.getEntryPrice());
                values.put("Exit_Price", warehouse.getExitPrice());
                values.put("isStill", warehouse.getQuantity() > 0 ? 1 : 0);  // Cập nhật trạng thái still

                long result = db.insert("warehouse", null, values);
                if (result != -1) {
                    Log.d("InsertOrUpdate", "Inserted new product successfully.");
                    return true;
                } else {
                    Log.e("InsertOrUpdate", "Failed to insert new product.");
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in insertOrUpdateProduct", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean checkIfProductExists(int productId, int colorId, int sizeId) {
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM warehouse WHERE ID_Product = ? AND ID_Color = ? AND ID_Size = ?",
                new String[]{String.valueOf(productId), String.valueOf(colorId), String.valueOf(sizeId)});

        try {
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
