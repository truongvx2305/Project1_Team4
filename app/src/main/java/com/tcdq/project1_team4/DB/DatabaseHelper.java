package com.tcdq.project1_team4.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "project1_group4.db";
    public static final String userTable = "user";
    public static final String customerTable = "customer";
    public static final String discountTable = "discount";
    public static final String productTable = "product";
    public static final String brandTable = "brand";
    public static final String sizeTable = "size";
    public static final String colorTable = "color";
    public static final String productTypeTable = "product_type";
    public static final String warehouseTable = "warehouse";
    public static final String invoiceTable = "invoice";
    public static final String invoiceDetailTable = "invoiceDetail";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 42);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bật kiểm tra khóa ngoại
        db.execSQL("PRAGMA foreign_keys=ON;");

        // Tạo bảng user
        createUserTable(db);
        // Tạo bảng customer
        createCustomerTable(db);
        // Tạo bảng discount
        createDiscountTable(db);
        // Tạo bảng product_type
        createProductTypeTable(db);
        // Tạo bảng brand
        createBrandTable(db);
        // Tạo bảng sản phẩm
        createProductTable(db);
        // Tạo bảng size
        createSizeTable(db);
        // Tạo bảng color
        createColorTable(db);
        // Tạo bảng warehouse
        createWarehouseTable(db);
        // Tạo bảng Invoice
        createInvoiceTable(db);
        // Tạo bảng InvoiceDetail
        createInvoiceDetailTable(db);


        // Chèn dữ liệu admin mẫu
        insertAdmin(db);
        // Chèn dữ liệu nhân viên mẫu
        insertEmployee(db);
        insertEmployee2(db);
        // Chèn dữ liệu khách hàng mẫu
        insertCustomer(db);
        insertCustomer2(db);
        // Chèn dữ liệu giảm giá mẫu
        insertDiscount(db);
        insertDiscount2(db);
        //chèn dữ liệu mẫu brand, size, color, product_type
        insertProductType(db);
        insertBrand(db);
        insertSize(db);
        insertColor(db);
        //chèn dữ liệu mẫu product
        insertProduct(db);
        //chèn dữ liệu mẫu warehouse
        insertWarehouse(db);
        insertWarehouse2(db);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + userTable);
        db.execSQL("DROP TABLE IF EXISTS " + customerTable);
        db.execSQL("DROP TABLE IF EXISTS " + discountTable);
        db.execSQL("DROP TABLE IF EXISTS " + productTypeTable);
        db.execSQL("DROP TABLE IF EXISTS " + brandTable);
        db.execSQL("DROP TABLE IF EXISTS " + productTable);
        db.execSQL("DROP TABLE IF EXISTS " + sizeTable);
        db.execSQL("DROP TABLE IF EXISTS " + colorTable);
        db.execSQL("DROP TABLE IF EXISTS " + warehouseTable);

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
                "isAdmin INTEGER, " +
                "isActive INTEGER, " +
                "Security_Lock TEXT)");
    }

    private void createCustomerTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + customerTable + " (" +
                "ID_Customer INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, " +
                "Phone_Number TEXT UNIQUE, " +
                "isVIP INTEGER)");
    }

    private void createDiscountTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + discountTable + " (" +
                "ID_Discount INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, " +
                "Discount_Price REAL, " +
                "Min_Order_Price INTEGER, " + // Giá trị hóa đơn tối thiểu để áp dụng
                "Start_Date TEXT, " +      // Ngày bắt đầu
                "End_Date TEXT, " +        // Ngày kết thúc
                "Quantity INTEGER, " +     // Số lượng
                "isValid INTEGER)");       // Hạn sử dụng
    }

    private void createProductTypeTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + productTypeTable + " (" +
                "ID_ProductType INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT NOT NULL UNIQUE)");
    }

    private void createBrandTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + brandTable + " (" +
                "ID_Brand INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT NOT NULL UNIQUE)");
    }

    private void createProductTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + productTable + " (" +
                "ID_Product INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, " +
                "ID_ProductType INTEGER, " +
                "ID_Brand INTEGER, " +
                "FOREIGN KEY(ID_ProductType) REFERENCES " + productTypeTable + "(ID_ProductType), " +
                "FOREIGN KEY(ID_Brand) REFERENCES " + brandTable + "(ID_Brand))");
    }

    private void createSizeTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + sizeTable + " (" +
                "ID_Size INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT NOT NULL UNIQUE)");
    }

    private void createColorTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + colorTable + " (" +
                "ID_Color INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT NOT NULL UNIQUE)");
    }

    private void createWarehouseTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + warehouseTable + " (" +
                "ID_Product INTEGER, " +
                "Image BLOB, " +
                "ID_Color INTEGER, " +
                "ID_Size INTEGER, " +
                "Quantity INTEGER, " +
                "Entry_Date TEXT, " +
                "Entry_Price REAL, " +
                "Exit_Date TEXT, " +
                "Exit_Price REAL, " +
                "isStill INTEGER, " +
                "FOREIGN KEY(ID_Product) REFERENCES " + productTable + "(ID_Product), " +
                "FOREIGN KEY(ID_Color) REFERENCES " + colorTable + "(ID_Color), " +
                "FOREIGN KEY(ID_Size) REFERENCES " + sizeTable + "(ID_Size))");
    }

    private void createInvoiceTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + invoiceTable + " (" +
                "ID_Invoice INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ID_User INTEGER, " +
                "ID_Customer INTEGER, " +
                "Date TEXT, " +
                "Total_Amount REAL, " +
                "FOREIGN KEY(ID_User) REFERENCES " + userTable + "(ID_User), " +
                "FOREIGN KEY(ID_Customer) REFERENCES " + customerTable + "(ID_Customer))");
    }

    private void createInvoiceDetailTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + invoiceDetailTable + " (" +
                "ID_InvoiceDetail INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ID_Invoice INTEGER, " +
                "ID_Discount INTEGER, " +
                "Quantity INTEGER, " +
                "Total_Price REAL, " +
                "FOREIGN KEY(ID_Invoice) REFERENCES " + invoiceTable + "(ID_Invoice), " +
                "FOREIGN KEY(ID_Discount) REFERENCES " + discountTable + "(ID_Discount))");
    }

    private void insertAdmin(SQLiteDatabase db) {
        ContentValues adminUser = new ContentValues();
        adminUser.put("Username", "Admin");
        adminUser.put("Password", "12345678");
        adminUser.put("Name", "TCDQ");
        adminUser.put("Email", "TCDQ@gmail.com");
        adminUser.put("Phone_Number", "0123456789");
        adminUser.put("isAdmin", 1);
        adminUser.put("isActive", 1);

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

    private void insertEmployee2(SQLiteDatabase db) {
        ContentValues employeeUser2 = new ContentValues();
        employeeUser2.put("Username", "Employee002");
        employeeUser2.put("Password", "12345678");
        employeeUser2.put("Name", "Employee002");
        employeeUser2.put("Email", "Employee002@gmail.com");
        employeeUser2.put("Phone_Number", "0896745231");
        employeeUser2.put("isAdmin", 0); // 0 cho false
        employeeUser2.put("isActive", 0); // 1 cho true

        db.insert(userTable, null, employeeUser2);
    }

    private void insertCustomer(SQLiteDatabase db) {
        ContentValues customer = new ContentValues();
        customer.put("Name", "Customer001");
        customer.put("Phone_Number", "0123456789");
        customer.put("isVIP", 1);

        db.insert(customerTable, null, customer);
    }

    private void insertCustomer2(SQLiteDatabase db) {
        ContentValues customer2 = new ContentValues();
        customer2.put("Name", "Customer002");
        customer2.put("Phone_Number", "019999999");
        customer2.put("isVIP", 0);

        db.insert(customerTable, null, customer2);
    }

    private void insertDiscount(SQLiteDatabase db) {
        ContentValues discount = new ContentValues();
        discount.put("Name", "Phiếu giảm giá 10%");
        discount.put("Discount_Price", 0.1);
        discount.put("Min_Order_Price", 1000000);
        discount.put("Start_Date", "2024-11-21");
        discount.put("End_Date", "2024-12-31");
        discount.put("Quantity", 10);
        discount.put("isValid", 1);

        db.insert(discountTable, null, discount);
    }

    private void insertDiscount2(SQLiteDatabase db) {
        ContentValues discount = new ContentValues();
        discount.put("Name", "Phiếu giảm giá 20%");
        discount.put("Discount_Price", 0.2);
        discount.put("Min_Order_Price", 2000000);
        discount.put("Start_Date", "2024-11-21");
        discount.put("End_Date", "2024-11-29");
        discount.put("Quantity", 10);
        discount.put("isValid", 0);

        db.insert(discountTable, null, discount);
    }

    private void insertProductType(SQLiteDatabase db) {
        ContentValues type1 = new ContentValues();
        type1.put("Name", "Áo");
        db.insert(productTypeTable, null, type1);

        ContentValues type2 = new ContentValues();
        type2.put("Name", "Quần");
        db.insert(productTypeTable, null, type2);
    }

    private void insertBrand(SQLiteDatabase db) {
        ContentValues brand1 = new ContentValues();
        brand1.put("Name", "Nike");
        db.insert(brandTable, null, brand1);

        ContentValues brand2 = new ContentValues();
        brand2.put("Name", "Adidas");
        db.insert(brandTable, null, brand2);
    }

    private void insertSize(SQLiteDatabase db) {
        String[] sizes = {"S", "M", "L", "XL"};
        for (String size : sizes) {
            ContentValues sizeValue = new ContentValues();
            sizeValue.put("Name", size);
            db.insert(sizeTable, null, sizeValue);
        }
    }

    private void insertColor(SQLiteDatabase db) {
        ContentValues color1 = new ContentValues();
        color1.put("Name", "Đỏ");
        db.insert(colorTable, null, color1);

        ContentValues color2 = new ContentValues();
        color2.put("Name", "Xanh");
        db.insert(colorTable, null, color2);

        ContentValues color3 = new ContentValues();
        color3.put("Name", "Trắng");
        db.insert(colorTable, null, color3);
    }

    private void insertProduct(SQLiteDatabase db) {
        ContentValues product1 = new ContentValues();
        product1.put("Name", "Áo sơ mi");
        product1.put("ID_ProductType", 1);
        product1.put("ID_Brand", 1);

        long result = db.insert(productTable, null, product1);
        Log.d("ProductDao", "Inserted product result: " + result);
    }

    private void insertWarehouse(SQLiteDatabase db) {
        ContentValues warehouse = new ContentValues();
        warehouse.put("ID_Product", 1);
        warehouse.put("ID_Color", 3);
        warehouse.put("ID_Size", 4);
        warehouse.put("Quantity", 10);
        warehouse.put("Entry_Date", "2024-12-6");
        warehouse.put("Entry_Price", 100000);
        warehouse.put("Exit_Price", 200000);
        warehouse.put("isStill", 1);

        db.insert(warehouseTable, null, warehouse);
    }

    private void insertWarehouse2(SQLiteDatabase db) {
        ContentValues warehouse = new ContentValues();
        warehouse.put("ID_Product", 1);
        warehouse.put("ID_Color", 3);
        warehouse.put("ID_Size", 3);
        warehouse.put("Quantity", 0);
        warehouse.put("Entry_Date", "2023-12-6");
        warehouse.put("Entry_Price", 100000);
        warehouse.put("Exit_Price", 200000);
        warehouse.put("isStill", 0);

        db.insert(warehouseTable, null, warehouse);
    }
}