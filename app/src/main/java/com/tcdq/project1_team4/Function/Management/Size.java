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
import androidx.fragment.app.Fragment;

import com.tcdq.project1_team4.Adapter.SizeAdapter;
import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.SizeDao;
import com.tcdq.project1_team4.Model.SizeModel;
import com.tcdq.project1_team4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @noinspection ALL
 */
public class Size extends Fragment {
    private final List<SizeModel> originalSizeList = new ArrayList<>(); // Danh sách gốc
    private final List<SizeModel> sizeList = new ArrayList<>(); // Danh sách hiển thị
    private SizeAdapter adapter;
    private ListView sizeView;
    private EditText searchSize;
    private ImageView filterSize;
    private final Integer currentFilterStatus = null; // Bộ lọc trạng thái
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.size, container, false);

        initializeUI(view);
        loadData();
        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        sizeView = view.findViewById(R.id.listSize);
        searchSize = view.findViewById(R.id.searchSize);
        FloatingActionButton btnAddSize = view.findViewById(R.id.btn_addSize);

        btnAddSize.setOnClickListener(v -> showDialogAddSize());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SizeDao sizeDao = new SizeDao(db);

        originalSizeList.clear(); // Làm trống danh sách gốc
        originalSizeList.addAll(sizeDao.getAlLSize()); // Lấy dữ liệu từ DB

        sizeList.clear(); // Làm trống danh sách hiển thị
        sizeList.addAll(originalSizeList); // Sao chép từ danh sách gốc

        adapter = new SizeAdapter(getContext(), sizeList);
        sizeView.setAdapter(adapter);
    }

    private void setupListeners() {
        searchSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSizesAndStatus(s.toString(), null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không xử lý
            }
        });
    }

    private void filterSizesAndStatus(String query, Integer statusFilter) {
        List<SizeModel> filteredList = new ArrayList<>();

        // Lọc danh sách từ originalSizeList
        for (SizeModel size : originalSizeList) {
            boolean matchesSearch = TextUtils.isEmpty(query)
                    || String.valueOf(size.getIdSize()).contains(query)
                    || size.getSizeName().toLowerCase().contains(query.toLowerCase());

            if (matchesSearch) {
                filteredList.add(size);
            }
        }

        adapter.updateList(filteredList);
    }

    private void showDialogAddSize() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_size, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameAddSize);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = nameField.getText().toString().trim();

                if (validateInput(nameField, name)) {
                    SizeModel newSize = new SizeModel();
                    newSize.setSizeName(name);

                    SizeDao sizeDao = new SizeDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (sizeDao.insert(newSize)) {
                        originalSizeList.add(newSize); // Cập nhật danh sách gốc
                        filterSizesAndStatus(searchSize.getText().toString(), null); // Cập nhật hiển thị
                        Toast.makeText(getContext(), "Thêm kích cỡ thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm kích cỡ!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateInput(EditText nameField, String name) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên kích cỡ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (originalSizeList.stream().anyMatch(size -> size.getSizeName().equalsIgnoreCase(name))) {
            Toast.makeText(getContext(), "Tên kích cỡ đã tồn tại!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
