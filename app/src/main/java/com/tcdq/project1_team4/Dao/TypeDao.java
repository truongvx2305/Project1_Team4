package com.tcdq.project1_team4.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tcdq.project1_team4.Model.TypeModel;

import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL*/
public class TypeDao {
    private final SQLiteDatabase db;

    public TypeDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm
    public boolean insert(TypeModel type) {
        ContentValues values = new ContentValues();
        values.put("Name", type.getTypeName());

        long result = db.insert("product_type", null, values);
        if (result != -1) {
            type.setIdType((int) result); // Gán ID vừa được sinh ra
            return true;
        }
        return false;
    }

    // Cập nhật
    public boolean updateType(TypeModel type) {
        if (type == null || type.getIdType() <= 0) {
            Log.e("Update", "Dữ liệu loại hàng không hợp lệ.");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Name", type.getTypeName());


        int result = db.update("product_type", values, "ID_ProductType = ?", new String[]{String.valueOf(type.getIdType())});

        // Kiểm tra kết quả
        if (result > 0) {
            Log.d("Update", "Cập nhật thông tin loại hàng thành công.");
            return true; // Cập nhật thành công
        } else {
            Log.d("Update", "Không có bản ghi nào được cập nhật.");
            return false; // Không có dòng nào được cập nhật
        }
    }

    // Xóa
    public boolean delete(int typeId) {
        int rows = db.delete("product_type", "ID_ProductType = ?", new String[]{String.valueOf(typeId)});
        return rows > 0;
    }

    // 2. Các phương thức kiểm tra dữ liệu

    // 3. Các phương thức lấy phiếu giảm giá

    // Lấy danh sách
    public List<TypeModel> getAlLType() {
        List<TypeModel> types = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM product_type", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_ProductType"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));

                TypeModel Model = new TypeModel(id, name);
                types.add(Model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return types;
    }

    public String getTypeNameById(int idType) {
        String name = null;
        Cursor cursor = db.rawQuery("SELECT Name FROM product_type WHERE ID_ProductType = ?", new String[]{String.valueOf(idType)});
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
        }
        cursor.close();
        return name != null ? name : "Không xác định";
    }

}
