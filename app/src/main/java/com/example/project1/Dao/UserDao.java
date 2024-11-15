package com.example.project1.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project1.Model.UserModel;

public class UserDao {
    private final SQLiteDatabase db;

    public UserDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm tài khoản
    public boolean insert(UserModel user) {
        ContentValues values = new ContentValues();
        values.put("Username", user.getUsername());
        values.put("Password", user.getPassword());
        values.put("Image", user.getImage());
        values.put("Name", user.getName());
        values.put("Email", user.getEmail());
        values.put("Phone_Number", user.getPhoneNumber());
        values.put("isAdmin", user.isAdmin() ? 1 : 0); // Lưu giá trị boolean dưới dạng 1 hoặc 0
        values.put("isActive", user.isActive() ? 1 : 0); // Lưu giá trị boolean dưới dạng 1 hoặc 0

        long result = db.insert("user", null, values); // Chú ý tên bảng "user" (chữ thường)
        return result != -1;
    }

    // Cập nhật mật khẩu
    public boolean updatePassword(UserModel user, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("Password", newPassword);
        int result = db.update("user", values, "Username = ?", new String[]{user.getUsername()});
        return result > 0;
    }

    // Cập nhật thông tin người dùng
    public boolean updateUserProfile(UserModel user) {
        ContentValues values = new ContentValues();
        values.put("Username", user.getUsername());
        values.put("Password", user.getPassword());
        if (user.getImage() != null) { // Chỉ cập nhật ảnh nếu không phải null
            values.put("Image", user.getImage());
        }
        values.put("Name", user.getName());
        values.put("Email", user.getEmail());
        values.put("Phone_Number", user.getPhoneNumber());
        values.put("isAdmin", user.isAdmin() ? 1 : 0);
        values.put("isActive", user.isActive() ? 1 : 0);

        int result = db.update("user", values, "ID_User = ?", new String[]{String.valueOf(user.getId())});
        return result > 0;
    }

    // Xóa tài khoản
    public boolean delete(UserModel user) {
        int result = db.delete("user", "Username = ?", new String[]{user.getUsername()});
        return result > 0;
    }

    // Kiểm tra Username
    public boolean checkUsername(UserModel user) {
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE Username = ?", new String[]{user.getUsername()});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Kiểm tra Password
    public boolean checkPassword(UserModel user) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM user WHERE Username = ? AND Password = ?",
                new String[]{user.getUsername(), user.getPassword()});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Kiểm tra Role
    public boolean checkRole(UserModel user) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM user WHERE Username = ? AND isAdmin = ?",
                new String[]{user.getUsername(), user.isAdmin() ? "1" : "0"});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Lấy thông tin người dùng từ database
    public UserModel getProfileByUsername(String username) {
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE Username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_User"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("Image"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
            boolean isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("isAdmin")) == 1;
            boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive")) == 1;

            cursor.close();
            return new UserModel(id, username, password, image, name, email, phoneNumber, isAdmin, isActive);
        }
        cursor.close();
        return null;
    }
}
