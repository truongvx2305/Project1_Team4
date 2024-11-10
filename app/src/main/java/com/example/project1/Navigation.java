package com.example.project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.UserDao;
import com.example.project1.Function.Login;
import com.google.android.material.navigation.NavigationView;

public class Navigation extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private DrawerLayout drawerLayout;
    private String username;
    private TextView toolbarTitle;
    private NavigationView navigationView;
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        // Khởi tạo DatabaseHelper và UserDao
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db);

        // Lấy thông tin người dùng từ SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        username = sharedPreferences.getString(KEY_USERNAME, null);

        // Khởi tạo các thành phần của giao diện
        toolbarTitle = findViewById(R.id.toolbar_title);
        drawerLayout = findViewById(R.id.navigation);
        setUpToolbar();

        // Hiển thị tên người dùng
        navigationView = findViewById(R.id.navigation_view);
        setupNavigationHeader();

        // Đăng ký sự kiện cho các item trong NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_logout) {
                logout();
                return true;
            }
            return false;
        });
    }

    private void setUpToolbar() {
        ImageButton toolbarButton = findViewById(R.id.action_toolbar);
        toolbarButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupNavigationHeader() {
        View view = navigationView.getHeaderView(0);
        TextView showUsername = view.findViewById(R.id.showUsername);
        if (username != null) {
            showUsername.setText(username);
        }
    }

    // Hàm xử lý đăng xuất
    private void logout() {
        // Đặt lại trạng thái đăng nhập thành false
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();

        // Thông báo đăng xuất thành công
        Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(Navigation.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
