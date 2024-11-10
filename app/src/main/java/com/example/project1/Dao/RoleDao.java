package com.example.project1.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project1.Model.Role;

public class RoleDao {
    private final SQLiteDatabase db;

    public RoleDao(SQLiteDatabase db) {
        this.db = db;
    }
}
