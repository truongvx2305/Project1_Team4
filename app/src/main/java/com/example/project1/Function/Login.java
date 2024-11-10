package com.example.project1.Function;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.UserDao;
import com.example.project1.Model.User;
import com.example.project1.Navigation;
import com.example.project1.R;
import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {
    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin;
    private CheckBox remember;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Ánh xạ
        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        remember = findViewById(R.id.remember);

        // Khởi tạo Database và UserDao
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Nếu đã chọn checkbox, tự động điền thông tin tài khoản
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            edtUsername.setText(sharedPreferences.getString(KEY_USERNAME, ""));
            edtPassword.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            remember.setChecked(true);
        }

        // Sự kiện khi nhấn nút đăng nhập
        btnLogin.setOnClickListener(v -> loginClick());
    }

    // Hàm xử lý khi nhấn nút đăng nhập
    private void loginClick() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng User với thông tin đăng nhập
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        // Kiểm tra mật khẩu
        if (userDao.checkPassword(user)) {
            // Đăng nhập thành công
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Lưu trạng thái đăng nhập
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            // Lưu thông tin người dùng nếu chọn checkbox
            if (remember.isChecked()) {
                editor.putString(KEY_USERNAME, username);
                editor.putString(KEY_PASSWORD, password);
                editor.putBoolean(KEY_REMEMBER, true);
            } else {
                // Nếu không chọn checkbox, chỉ xóa thông tin tài khoản nhưng vẫn giữ trạng thái đăng nhập
                editor.remove(KEY_USERNAME);
                editor.remove(KEY_PASSWORD);
                editor.putBoolean(KEY_REMEMBER, false);
            }
            editor.apply();

            // Chuyển đến Navigation
            Intent intent = new Intent(Login.this, Navigation.class);
            startActivity(intent);
            finish();
        } else {
            // Đăng nhập thất bại
            Toast.makeText(this, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
        }
    }
}
