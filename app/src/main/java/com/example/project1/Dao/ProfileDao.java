package com.example.project1.Dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.project1.Model.Profile;

public class ProfileDao {
    private final SQLiteDatabase db;

    public ProfileDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm thông tin cá nhân
    public boolean insert(Profile profile) {
        ContentValues values = new ContentValues();
        values.put("ID_User", profile.getIdUser());
        values.put("Name", profile.getName());
        values.put("Email", profile.getEmail());
        values.put("Phone_Number", profile.getPhoneNumber());
        values.put("ID_Status", profile.getIdStatus());
        long result = db.insert("Profile", null, values);
        return result != -1;
    }

    // Cập nhật thông tin cá nhân
    public boolean update(Profile profile) {
        ContentValues values = new ContentValues();
        values.put("Name", profile.getName());
        values.put("Email", profile.getEmail());
        values.put("Phone_Number", profile.getPhoneNumber());
        values.put("ID_Status", profile.getIdStatus());
        int result = db.update("Profile", values, "ID_User = ?", new String[]{String.valueOf(profile.getIdUser())});
        return result != -1;
    }
}
