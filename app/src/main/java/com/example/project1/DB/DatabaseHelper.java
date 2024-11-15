package com.example.project1.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "project1.db";
    public static final String userTable = "user";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bật kiểm tra khóa ngoại
        db.execSQL("PRAGMA foreign_keys=ON;");

        // Tạo bảng user
        createUserTable(db);

        // Chèn dữ liệu admin mẫu
        insertAdmin(db);
        // Chèn dữ liệu nhân viên mẫu
        insertEmployee(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + userTable);

        // Tạo lại bảng
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Bật kiểm tra khóa ngoại
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    private void createUserTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + userTable + " (" +
                "ID_User INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Username TEXT UNIQUE, " +
                "Password TEXT, " +
                "Image BLOB, " +
                "Name TEXT, " +
                "Email TEXT UNIQUE, " +
                "Phone_Number TEXT UNIQUE, " +
                "isAdmin INTEGER, " +   // Sử dụng INTEGER thay vì BOOLEAN
                "isActive INTEGER)");   // Sử dụng INTEGER thay vì BOOLEAN
    }

    private void insertAdmin(SQLiteDatabase db) {
        ContentValues adminUser = new ContentValues();
        adminUser.put("Username", "Admin");
        adminUser.put("Password", "12345678");
        adminUser.put("Name", "TCDQ");
        adminUser.put("Email", "TCDQ@gmail.com");
        adminUser.put("Phone_Number", "0123456789");
        adminUser.put("isAdmin", 1); // 1 cho true
        adminUser.put("isActive", 1); // 1 cho true

        db.insert(userTable, null, adminUser);
    }

    private void insertEmployee(SQLiteDatabase db) {
        ContentValues employeeUser = new ContentValues();
        employeeUser.put("Username", "Employee001");
        employeeUser.put("Password", "12345678");
        employeeUser.put("Name", "Employee001");
        employeeUser.put("Email", "Employee001@gmail.com");
        employeeUser.put("Phone_Number", "0987654321");
        employeeUser.put("isAdmin", 0); // 0 cho false
        employeeUser.put("isActive", 1); // 1 cho true

        db.insert(userTable, null, employeeUser);
    }
}
