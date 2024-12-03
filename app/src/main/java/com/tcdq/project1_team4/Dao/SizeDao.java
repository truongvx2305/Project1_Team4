package com.tcdq.project1_team4.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tcdq.project1_team4.Model.SizeModel;

import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL */
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
    public boolean delete(String name) {
        int result = db.delete("size", "Name = ?", new String[]{name});
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
}
