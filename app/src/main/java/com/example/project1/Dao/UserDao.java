package com.example.project1.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project1.Model.Role;
import com.example.project1.Model.User;

public class UserDao {
    private final SQLiteDatabase db;

    public UserDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm tài khoản
    public boolean insert(User user) {
        ContentValues values = new ContentValues();
        values.put("Username", user.getUsername());
        values.put("Password", user.getPassword());
        values.put("ID_Role", user.getIdRole());
        long result = db.insert("User", null, values);
        return result != -1;
    }

    // Cập nhật mật khẩu
    public boolean updatePassword(User user, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("Password", newPassword);
        int result = db.update("User", values, "Username = ?", new String[]{user.getUsername()});
        return result != -1;
    }

    // Xóa tài khoản
    public boolean delete(User user) {
        int result = db.delete("User", "Username = ?", new String[]{user.getUsername()});
        return result > 0;
    }

    // Kiểm tra Username
    public boolean checkUsername(User user) {
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE Username = ?", new String[]{user.getUsername()});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Kiểm tra Password
    public boolean checkPassword(User user) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM User WHERE Username = ? AND Password = ?",
                new String[]{user.getUsername(), user.getPassword()});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Kiểm tra Role
    public boolean checkRole(User user, Role role) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM User WHERE Username = ? AND ID_Role = ?",
                new String[]{user.getUsername(), String.valueOf(role.getIdRole())});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
