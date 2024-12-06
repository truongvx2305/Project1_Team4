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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tcdq.project1_team4.Adapter.TypeAdapter;
import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.ProductDao;
import com.tcdq.project1_team4.Dao.TypeDao;
import com.tcdq.project1_team4.Model.TypeModel;
import com.tcdq.project1_team4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @noinspection ALL
 */
public class Type extends Fragment {
    private String username;
    private final List<TypeModel> originalTypeList = new ArrayList<>(); // Danh sách gốc
    private final List<TypeModel> typeList = new ArrayList<>(); // Danh sách hiển thị
    private TypeAdapter adapter;

    private ListView typeView;
    private EditText searchType;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.type, container, false);

        initializeUI(view);
        loadData();
        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        typeView = view.findViewById(R.id.listType);
        searchType = view.findViewById(R.id.searchType);
        FloatingActionButton btnAddType = view.findViewById(R.id.btn_addType);

        btnAddType.setOnClickListener(v -> showDialogAddType());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        TypeDao typeDao = new TypeDao(db);

        originalTypeList.clear(); // Làm trống danh sách gốc
        originalTypeList.addAll(typeDao.getAlLType()); // Lấy dữ liệu từ DB

        typeList.clear(); // Làm trống danh sách hiển thị
        typeList.addAll(originalTypeList); // Sao chép từ danh sách gốc

        adapter = new TypeAdapter(getContext(), typeList);
        typeView.setAdapter(adapter);
    }

    private void setupListeners() {
        searchType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTypeByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không xử lý
            }
        });

        typeView.setOnItemClickListener((parent, view, position, id) -> {
            TypeModel selectedType = typeList.get(position);
            showDeleteTypeDialog(selectedType);
        });
    }

    private void filterTypeByName(String query) {
        List<TypeModel> filteredList = new ArrayList<>();

        // Lọc dữ liệu từ danh sách gốc
        for (TypeModel type : originalTypeList) {
            if (TextUtils.isEmpty(query)
                    || String.valueOf(type.getIdType()).contains(query)
                    || type.getTypeName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(type);
            }
        }

        adapter.updateList(filteredList);
    }

    private void showDialogAddType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_type, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameAddType);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = nameField.getText().toString().trim();

                if (validateInput(nameField, name)) {
                    TypeModel newType = new TypeModel();
                    newType.setTypeName(name);

                    TypeDao typeDao = new TypeDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (typeDao.insert(newType)) {
                        originalTypeList.add(newType); // Cập nhật danh sách gốc
                        filterTypeByName(searchType.getText().toString()); // Cập nhật hiển thị
                        Toast.makeText(getContext(), "Thêm loại hàng thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm loại hàng!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateInput(EditText nameField, String name) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên loại sản phẩm!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (originalTypeList.stream().anyMatch(type -> type.getTypeName().equalsIgnoreCase(name))) {
            Toast.makeText(getContext(), "Tên loại sản phẩm đã tồn tại!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDeleteTypeDialog(TypeModel type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Xóa loại sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa loại sản phẩm \"" + type.getTypeName() + "\" ?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ProductDao productDao = new ProductDao(db);

                    // Kiểm tra loại sản phẩm đã được dùng trong bảng sản phẩm chưa
                    int productCount = productDao.getProductCountByTypeId(type.getIdType());
                    if (productCount > 0) {
                        Toast.makeText(getContext(), "Không thể xóa! Loại này đang được sử dụng!", Toast.LENGTH_SHORT).show();
                    } else {
                        TypeDao typeDao = new TypeDao(db);
                        if (typeDao.delete(type.getIdType())) {
                            originalTypeList.remove(type); // Cập nhật danh sách gốc
                            filterTypeByName(searchType.getText().toString()); // Cập nhật hiển thị
                            Toast.makeText(getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi xóa loại sản phẩm!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
