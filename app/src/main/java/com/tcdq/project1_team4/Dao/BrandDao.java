package com.tcdq.project1_team4.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tcdq.project1_team4.Model.BrandModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection ALL
 */
public class BrandDao {
    private final SQLiteDatabase db;

    public BrandDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm
    public boolean insert(BrandModel brand) {
        ContentValues values = new ContentValues();
        values.put("Name", brand.getName());

        long result = db.insert("brand", null, values);
        if (result != -1) {
            brand.setIdBrand((int) result); // Gán ID vừa được sinh ra
            return true;
        }
        return false;
    }

    // Cập nhật
    public boolean updateBrand(BrandModel brand) {

        if (brand == null || brand.getIdBrand() <= 0) {
            Log.e("Update", "Dữ liệu nhãn hàng không hợp lệ.");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Name", brand.getName());


        int result = db.update("brand", values, "ID_Brand = ?", new String[]{String.valueOf(brand.getIdBrand())});

        // Kiểm tra kết quả
        if (result > 0) {
            Log.d("Update", "Cập nhật thông tin nhãn hàng thành công.");
            return true; // Cập nhật thành công
        } else {
            Log.d("Update", "Không có bản ghi nào được cập nhật.");
            return false; // Không có dòng nào được cập nhật
        }
    }

    // Xóa
    public boolean delete(String name) {
        int result = db.delete("brand", "Name = ?", new String[]{name});
        return result > 0;
    }

    // 2. Các phương thức kiểm tra dữ liệu

    // 3. Các phương thức lấy phiếu giảm giá

    // Lấy danh sách
    public List<BrandModel> getAlLBrand() {
        List<BrandModel> brands = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM brand", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Brand"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));

                BrandModel Model = new BrandModel(id, name);
                brands.add(Model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return brands;
    }

    public String getBrandNameById(int idBrand) {
        String name = null;
        Cursor cursor = db.rawQuery("SELECT Name FROM brand WHERE ID_Brand = ?", new String[]{String.valueOf(idBrand)});
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
        }
        cursor.close();
        return name != null ? name : "Không xác định";
    }

}
