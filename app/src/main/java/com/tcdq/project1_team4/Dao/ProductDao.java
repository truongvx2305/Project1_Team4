package com.tcdq.project1_team4.Dao;

import static com.tcdq.project1_team4.DB.DatabaseHelper.brandTable;
import static com.tcdq.project1_team4.DB.DatabaseHelper.productTypeTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tcdq.project1_team4.Model.ProductModel;

import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL */
public class ProductDao {
    private final SQLiteDatabase db;

    public ProductDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm sản phẩm
    public boolean insertProduct(ProductModel product) {
        ContentValues values = new ContentValues();
        values.put("Name", product.getName());
        values.put("ID_ProductType", product.getIdProductType());
        values.put("ID_Brand", product.getIdBrand());
        values.put("Image", product.getImage());  // Lưu image dưới dạng byte[]

        long result = db.insert("product", null, values);
        if (result != -1) {
            product.setId((int) result); // Gán ID vừa được sinh ra
            return true;
        }
        return false;
    }

    public boolean updateProduct(ProductModel product) {
        if (product == null || product.getId() <= 0) {
            Log.e("Update", "Dữ liệu sản phẩm không hợp lệ.");
            return false;
        }

        // Tạo ContentValues để lưu trữ các giá trị cần cập nhật
        ContentValues values = new ContentValues();
        values.put("name", product.getName());  // Cập nhật tên sản phẩm
        values.put("image", product.getImage());  // Cập nhật hình ảnh dưới dạng byte[]

        // Thực hiện câu lệnh cập nhật dữ liệu trong cơ sở dữ liệu
        int result = db.update("product", values, "ID_Product = ?", new String[]{String.valueOf(product.getId())});

        if (result > 0) {
            Log.d("Update", "Cập nhật thông tin sản phẩm thành công.");
            return true; // Cập nhật thành công
        } else {
            Log.d("Update", "Không có bản ghi nào được cập nhật.");
            return false; // Không có dòng nào được cập nhật
        }
    }

    // Lấy danh sách sản phẩm
    public List<ProductModel> getAllProduct() {
        List<ProductModel> products = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM product", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Product"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                int productTypeId = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ProductType"));
                int brandId = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Brand"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("Image"));  // Lấy hình ảnh dưới dạng byte[]

                ProductModel product = new ProductModel(id, image, name, productTypeId, brandId);
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public List<String> getAllProductTypes() {
        List<String> typeList = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT DISTINCT Name FROM " + productTypeTable, null)) {
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
            Log.e("DatabaseError", "Error while fetching product types", e);
        }
        return typeList;
    }


    // Lấy tất cả thương hiệu sản phẩm
    public List<String> getAllBrands() {
        List<String> brands = new ArrayList<>();
        String query = "SELECT DISTINCT Name FROM " + brandTable;

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


    public int getProductTypeIdByName(String name) {
        int productTypeId = -1; // Giá trị mặc định nếu không tìm thấy
        Cursor cursor = null;
        try {
            String query = "SELECT ID_ProductType FROM " + productTypeTable + " WHERE Name = ?";
            cursor = db.rawQuery(query, new String[]{name});
            if (cursor != null && cursor.moveToFirst()) {
                productTypeId = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ProductType"));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getProductTypeIdByName", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productTypeId;
    }


    // Lấy ID thương hiệu từ tên
    public int getBrandIdByName(String name) {
        int brandId = -1; // Giá trị mặc định nếu không tìm thấy
        Cursor cursor = null;
        try {
            String query = "SELECT ID_Brand FROM " + brandTable + " WHERE Name = ?";
            cursor = db.rawQuery(query, new String[]{name});
            if (cursor != null && cursor.moveToFirst()) {
                brandId = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Brand"));
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getBrandIdByName", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return brandId;
    }

    /** @noinspection unused*/ // Lấy tên thương hiệu theo ID
    public String getBrandNameById(int brandId) {
        String brandName = null;
        Cursor cursor = db.rawQuery("SELECT Name FROM brand WHERE ID_Brand = ?", new String[]{String.valueOf(brandId)});
        if (cursor != null && cursor.moveToFirst()) {
            brandName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            cursor.close();
        }
        return brandName;
    }

    // Lấy tên loại sản phẩm theo ID
    public String getTypeNameById(int typeId) {
        String typeName = null;
        Cursor cursor = db.rawQuery("SELECT Name FROM product_type WHERE ID_ProductType = ?", new String[]{String.valueOf(typeId)});
        if (cursor != null && cursor.moveToFirst()) {
            typeName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            cursor.close();
        }
        return typeName;
    }

    /** @noinspection unused*/
    public boolean updateProductImage(int productId, byte[] imageBytes) {
        ContentValues values = new ContentValues();
        values.put("image", imageBytes);
        int rows = db.update("product", values, "ID_Product = ?", new String[]{String.valueOf(productId)});
        return rows > 0;
    }
}
