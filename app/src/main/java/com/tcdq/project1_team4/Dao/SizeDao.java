package com.tcdq.project1_team4.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Model.SizeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection ALL
 */
public class SizeDao {
    private final SQLiteDatabase db;

    public SizeDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm
    public boolean insert(SizeModel size) {
        ContentValues values = new ContentValues();
        values.put("Name", size.getSizeName());

        long result = db.insert("size", null, values);
        if (result != -1) {
            size.setIdSize((int) result); // Gán ID vừa được sinh ra
            return true;
        }
        return false;
    }

    // Cập nhật
    public boolean update(SizeModel size) {
        ContentValues values = new ContentValues();
        values.put("Name", size.getSizeName());

        int result = db.update("size", values, "ID_Size = ?", new String[]{String.valueOf(size.getIdSize())});
        return result > 0;
    }

    // Xóa
    public boolean delete(int sizeId) {
        int result = db.delete("size", "Name = ?", new String[]{String.valueOf(sizeId)});
        return result > 0;
    }

    // 2. Các phương thức kiểm tra dữ liệu

    // 3. Các phương thức lấy phiếu giảm giá

    // Lấy danh sách
    public List<SizeModel> getAlLSize() {
        List<SizeModel> sizes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM size", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Size"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));

                SizeModel Model = new SizeModel(id, name);
                sizes.add(Model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return sizes;
    }

    public boolean isSizeUsedInWarehouse(int sizeId) {
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.warehouseTable + " WHERE ID_Size = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sizeId)});
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        cursor.close();
        return false;
    }
}
