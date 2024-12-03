package com.tcdq.project1_team4.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tcdq.project1_team4.Model.ColorModel;

import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL*/
public class ColorDao {
    private final SQLiteDatabase db;

    public ColorDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm
    public boolean insert(ColorModel color) {
        ContentValues values = new ContentValues();
        values.put("Name", color.getColorName());

        long result = db.insert("color", null, values);
        if (result != -1) {
            color.setIdColor((int) result); // Gán ID vừa được sinh ra
            return true;
        }
        return false;
    }

    // Cập nhật
    public boolean updateColor(ColorModel color) {
        if (color == null || color.getIdColor() <= 0) {
            Log.e("Update", "Dữ liệu màu không hợp lệ.");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Name", color.getColorName());


        int result = db.update("color", values, "ID_Color = ?", new String[]{String.valueOf(color.getIdColor())});

        // Kiểm tra kết quả
        if (result > 0) {
            Log.d("Update", "Cập nhật thông tin màu thành công.");
            return true; // Cập nhật thành công
        } else {
            Log.d("Update", "Không có bản ghi nào được cập nhật.");
            return false; // Không có dòng nào được cập nhật
        }
    }

    // Xóa
    public boolean deleteColor(String name) {
        int result = db.delete("color", "Name = ?", new String[]{name});
        return result > 0;
    }

    // 2. Các phương thức kiểm tra dữ liệu

    // 3. Các phương thức lấy phiếu giảm giá

    // Lấy danh sách
    public List<ColorModel> getAlLColor() {
        List<ColorModel> colors = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM color", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Color"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));

                ColorModel Model = new ColorModel(id, name);
                colors.add(Model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return colors;
    }
}
