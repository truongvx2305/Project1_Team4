package com.example.project1.Dao;

import android.database.sqlite.SQLiteDatabase;

public class StatusDao {
    private final SQLiteDatabase db;

    public StatusDao(SQLiteDatabase db) {
        this.db = db;
    }
}
