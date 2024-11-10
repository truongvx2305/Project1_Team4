package com.example.project1.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "project1.db";
    public static final String roleTable = "role";
    public static final String statusTable = "status";
    public static final String profileTable = "userDetail";
    public static final String userTable = "user";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bật kiểm tra khóa ngoại
        db.execSQL("PRAGMA foreign_keys=ON;");

        // Tạo bảng
        createRoleTable(db);
        createStatusTable(db);
        createUserTable(db);
        createProfileTable(db);

        // Chèn dữ liệu
        insertRoles(db);
        insertStatus(db);
        insertAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + userTable);
        db.execSQL("DROP TABLE IF EXISTS " + profileTable);
        db.execSQL("DROP TABLE IF EXISTS " + roleTable);
        db.execSQL("DROP TABLE IF EXISTS " + statusTable);

        // Tạo lại bảng
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Bật kiểm tra khóa ngoại
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    private void createRoleTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + roleTable + " (" +
                "ID_Role INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Role_Name TEXT UNIQUE)");
    }

    private void createStatusTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + statusTable + " (" +
                "ID_Status INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Status TEXT)");
    }

    private void createUserTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + userTable + " (" +
                "ID_User INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Username TEXT UNIQUE, " +
                "Password TEXT, " +
                "ID_Role INTEGER, " +
                "FOREIGN KEY (ID_Role) REFERENCES " + roleTable + "(ID_Role))");
    }

    private void createProfileTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + profileTable + " (" +
                "ID_Profile INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ID_User INTEGER UNIQUE, " +
                "Name TEXT, " +
                "Email TEXT UNIQUE, " +
                "Phone_Number TEXT UNIQUE, " +
                "Image BLOB, " +
                "ID_Status INTEGER, " +
                "FOREIGN KEY (ID_User) REFERENCES " + userTable + "(ID_User), " +
                "FOREIGN KEY (ID_Status) REFERENCES " + statusTable + "(ID_Status))");
    }

    private void insertRoles(SQLiteDatabase db) {
        ContentValues roleAdmin = new ContentValues();
        roleAdmin.put("Role_Name", "Admin");
        db.insert(roleTable, null, roleAdmin);

        ContentValues roleEmployee = new ContentValues();
        roleEmployee.put("Role_Name", "Nhân viên");
        db.insert(roleTable, null, roleEmployee);
    }

    private void insertStatus(SQLiteDatabase db) {
        ContentValues statusWorking = new ContentValues();
        statusWorking.put("Status", "Đang làm việc");
        db.insert(statusTable, null, statusWorking);

        ContentValues statusRetired = new ContentValues();
        statusRetired.put("Status", "Đã nghỉ việc");
        db.insert(statusTable, null, statusRetired);
    }

    private void insertAdmin(SQLiteDatabase db) {
        // Lấy ID_Role của vai trò "Admin"
        Cursor cursor = db.rawQuery("SELECT ID_Role FROM " + roleTable + " WHERE Role_Name = 'Admin'", null);
        if (cursor.moveToFirst()) {
            int adminRoleId = cursor.getInt(0);
            ContentValues adminUser = new ContentValues();
            adminUser.put("Username", "Admin");
            adminUser.put("Password", "12345678");
            adminUser.put("ID_Role", adminRoleId); // Gán ID vai trò admin
            db.insert(userTable, null, adminUser);

            // Lấy ID_User của người dùng Admin vừa thêm
            Cursor userCursor = db.rawQuery("SELECT ID_User FROM " + userTable + " WHERE Username = 'Admin'", null);
            if (userCursor.moveToFirst()) {
                int adminUserId = userCursor.getInt(0);

                // Thêm thông tin profile cho Admin
                ContentValues adminProfile = new ContentValues();
                adminProfile.put("ID_User", adminUserId);
                adminProfile.put("Name", "TCDQ");
                adminProfile.put("Email", "TCDQ@gmail.com");
                adminProfile.put("Phone_Number", "0123456789");
                db.insert(profileTable, null, adminProfile);
            }
            userCursor.close();
        }
        cursor.close();
    }
}
