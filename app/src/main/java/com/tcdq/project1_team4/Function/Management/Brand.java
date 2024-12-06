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

import com.tcdq.project1_team4.Adapter.BrandAdapter;
import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.BrandDao;
import com.tcdq.project1_team4.Dao.ProductDao;
import com.tcdq.project1_team4.Model.BrandModel;
import com.tcdq.project1_team4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @noinspection ALL
 */
public class Brand extends Fragment {
    private String username;
    private final List<BrandModel> originalBrandList = new ArrayList<>(); // Danh sách gốc
    private final List<BrandModel> brandList = new ArrayList<>(); // Danh sách hiển thị
    private BrandAdapter adapter;

    private ListView brandView;
    private EditText searchBrand;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.brand, container, false);

        initializeUI(view);
        loadData();
        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        brandView = view.findViewById(R.id.listBrand);
        searchBrand = view.findViewById(R.id.searchBrand);
        FloatingActionButton btnAddBrand = view.findViewById(R.id.btn_addBrand);

        btnAddBrand.setOnClickListener(v -> showDialogAddBrand());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        BrandDao brandDao = new BrandDao(db);

        originalBrandList.clear();
        originalBrandList.addAll(brandDao.getAlLBrand());

        brandList.clear();
        brandList.addAll(originalBrandList);

        adapter = new BrandAdapter(getContext(), brandList);
        brandView.setAdapter(adapter);
    }

    private void setupListeners() {
        searchBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBrandByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không xử lý
            }
        });

        brandView.setOnItemClickListener((parent, view, position, id) -> {
            BrandModel selectedBrand = brandList.get(position);
            showDeleteBrandDialog(selectedBrand);
        });
    }

    private void filterBrandByName(String query) {
        List<BrandModel> filteredList = new ArrayList<>();

        for (BrandModel brand : originalBrandList) {
            if (TextUtils.isEmpty(query)
                    || String.valueOf(brand.getIdBrand()).contains(query)
                    || brand.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(brand);
            }
        }

        adapter.updateList(filteredList);
    }

    private void showDialogAddBrand() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_brand, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameAddBrand);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = nameField.getText().toString().trim();

                if (validateInput(nameField, name)) {
                    BrandModel newBrand = new BrandModel();
                    newBrand.setName(name);

                    BrandDao brandDao = new BrandDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (brandDao.insert(newBrand)) {
                        originalBrandList.add(newBrand);
                        filterBrandByName(searchBrand.getText().toString());
                        Toast.makeText(getContext(), "Thêm thương hiệu thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm thương hiệu!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateInput(EditText nameField, String name) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên thương hiệu!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (originalBrandList.stream().anyMatch(brand -> brand.getName().equalsIgnoreCase(name))) {
            Toast.makeText(getContext(), "Tên thương hiệu đã tồn tại!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDeleteBrandDialog(BrandModel brand) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Xóa thương hiệu")
                .setMessage("Bạn có chắc chắn muốn xóa thương hiệu \"" + brand.getName() + "\" ?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ProductDao productDao = new ProductDao(db);

                    // Kiểm tra loại sản phẩm đã được dùng trong bảng sản phẩm chưa
                    int productCount = productDao.getProductCountByBrandId(brand.getIdBrand());
                    if (productCount > 0) {
                        Toast.makeText(getContext(), "Không thể xóa! Thương hiệu này đang được sử dụng!", Toast.LENGTH_SHORT).show();
                    } else {
                        BrandDao brandDao = new BrandDao(db);
                        if (brandDao.delete(brand.getIdBrand())) {
                            originalBrandList.remove(brand); // Cập nhật danh sách gốc
                            filterBrandByName(searchBrand.getText().toString()); // Cập nhật hiển thị
                            Toast.makeText(getContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi xóa thương hiệu!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
