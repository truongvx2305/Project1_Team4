package com.tcdq.project1_team4.Function;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tcdq.project1_team4.Dao.UserDao;
import com.tcdq.project1_team4.Model.UserModel;
import com.tcdq.project1_team4.R;
import com.tcdq.project1_team4.DB.DatabaseHelper;

public class UpdatePassword extends Fragment {
    private EditText edtOldPassword, edtNewPassword;
    private Button btnUpdatePassword;
    private UserDao userDao;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_password, container, false);

        edtOldPassword = view.findViewById(R.id.edt_oldPassword);
        edtNewPassword = view.findViewById(R.id.edt_newPassword);
        btnUpdatePassword = view.findViewById(R.id.btn_updatePassword);

        // Lấy instance của Database
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        userDao = new UserDao(dbHelper.getWritableDatabase());

        btnUpdatePassword.setOnClickListener(v -> updatePassword());

        return view;
    }

    private void updatePassword() {
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();

        // Kiểm tra dữ liệu nhập
        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userDao.checkPassword(username, oldPassword)) {
            Toast.makeText(getContext(), "Mật khẩu cũ không chính xác!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu
        boolean isUpdated = userDao.updatePassword(username, newPassword);
        if (isUpdated) {
            Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            edtOldPassword.setText("");
            edtNewPassword.setText("");
        } else {
            Toast.makeText(getContext(), "Đổi mật khẩu thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }
}
