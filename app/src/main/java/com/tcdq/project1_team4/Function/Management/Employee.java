package com.tcdq.project1_team4.Function.Management;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.tcdq.project1_team4.Adapter.EmployeeAdapter;
import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.UserDao;
import com.tcdq.project1_team4.Model.UserModel;
import com.tcdq.project1_team4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** @noinspection ALL */
public class Employee extends Fragment {
    private final List<UserModel> originalEmployeeList = new ArrayList<>(); // Danh sách gốc
    private final List<UserModel> employeeList = new ArrayList<>(); // Danh sách hiển thị
    private EmployeeAdapter adapter;
    private ListView listEmployee;
    private ImageView filterEmployee;
    private EditText searchEmployee;
    private Integer currentFilterStatus = null;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee, container, false);

        initializeUI(view);
        loadData();

        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        listEmployee = view.findViewById(R.id.listEmployee);
        filterEmployee = view.findViewById(R.id.filterEmployee);
        searchEmployee = view.findViewById(R.id.searchEmployee);
        FloatingActionButton btnAddEmployee = view.findViewById(R.id.btn_addEmployee);

        btnAddEmployee.setOnClickListener(v -> showDialogAddEmployee());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        UserDao userDao = new UserDao(db);

        originalEmployeeList.clear(); // Làm trống danh sách gốc
        originalEmployeeList.addAll(userDao.getAllEmployees()); // Lấy dữ liệu từ DB

        employeeList.clear(); // Làm trống danh sách hiển thị
        employeeList.addAll(originalEmployeeList); // Sao chép từ danh sách gốc

        adapter = new EmployeeAdapter(getContext(), employeeList);
        listEmployee.setAdapter(adapter);
        adapter.setUsername(username);
    }

    private void setupListeners() {
        searchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEmployeesAndStatus(s.toString(), currentFilterStatus);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không xử lý
            }
        });

        filterEmployee.setOnClickListener(this::filterClick);
    }

    private void filterEmployeesAndStatus(String query, Integer statusFilter) {
        List<UserModel> filteredList = new ArrayList<>();

        // Sử dụng originalEmployeeList làm nguồn để lọc
        for (UserModel employee : originalEmployeeList) {
            boolean matchesSearch = TextUtils.isEmpty(query)
                    || String.valueOf(employee.getId()).contains(query)
                    || employee.getName().toLowerCase().contains(query.toLowerCase())
                    || employee.getPhoneNumber().contains(query);

            boolean matchesStatus = (statusFilter == null)
                    || (statusFilter == R.id.filter_active && employee.isActive())
                    || (statusFilter == R.id.filter_inactive && !employee.isActive());

            if (matchesSearch && matchesStatus) {
                filteredList.add(employee);
            }
        }

        adapter.updateList(filteredList);
    }

    private void filterClick(View v) {
        PopupMenu popupMenu = new PopupMenu(Objects.requireNonNull(getContext()), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_status, popupMenu.getMenu());

        if (currentFilterStatus != null) {
            popupMenu.getMenu().findItem(currentFilterStatus).setChecked(true);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.filter_clear) {
                currentFilterStatus = null;
                filterEmployeesAndStatus(searchEmployee.getText().toString(), null);
            } else {
                currentFilterStatus = itemId;
                filterEmployeesAndStatus(searchEmployee.getText().toString(), itemId);
            }
            return true;
        });

        popupMenu.show();
    }

    private void showDialogAddEmployee() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_employee, null);
        builder.setView(dialogView);

        EditText usernameField = dialogView.findViewById(R.id.usernameAddEmployee);
        EditText passwordField = dialogView.findViewById(R.id.passwordAddEmployee);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (validateEmployeeInput(usernameField, passwordField, username, password)) {
                    UserModel newEmployee = new UserModel();
                    newEmployee.setUsername(username);
                    newEmployee.setPassword(password);
                    newEmployee.setActive(true);
                    newEmployee.setAdmin(false);

                    UserDao userDao = new UserDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (userDao.insert(newEmployee)) {
                        loadData();
                        Toast.makeText(getContext(), "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm nhân viên!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateEmployeeInput(EditText usernameField, EditText passwordField, String username, String password) {
        if (TextUtils.isEmpty(username)) {
            usernameField.setError("Vui lòng nhập tên đăng nhập!");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Vui lòng nhập mật khẩu!");
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9._-]{5,}$")) {
            usernameField.setError("Tên đăng nhập không hợp lệ!");
            return false;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$")) {
            passwordField.setError("Mật khẩu không đủ mạnh!");
            return false;
        }

        UserDao userDao = new UserDao(new DatabaseHelper(getContext()).getWritableDatabase());
        if (userDao.checkUsername(username)) {
            usernameField.setError("Tên đăng nhập đã tồn tại!");
            return false;
        }

        return true;
    }
}
