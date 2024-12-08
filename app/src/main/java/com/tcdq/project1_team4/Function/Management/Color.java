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

import com.tcdq.project1_team4.Adapter.ColorAdapter;
import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.ColorDao;
import com.tcdq.project1_team4.Model.ColorModel;
import com.tcdq.project1_team4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @noinspection ALL
 */
public class Color extends Fragment {
    private String username;
    private final List<ColorModel> originalColorList = new ArrayList<>(); // Danh sách gốc
    private final List<ColorModel> colorList = new ArrayList<>(); // Danh sách hiển thị
    private ColorAdapter adapter;

    private ListView colorView;
    private EditText searchColor;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.color, container, false);

        initializeUI(view);
        loadData();
        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        colorView = view.findViewById(R.id.listColor);
        searchColor = view.findViewById(R.id.searchColor);
        FloatingActionButton btnAddColor = view.findViewById(R.id.btn_addColor);

        btnAddColor.setOnClickListener(v -> showDialogAddColor());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ColorDao colorDao = new ColorDao(db);

        originalColorList.clear();
        originalColorList.addAll(colorDao.getAlLColor());

        colorList.clear();
        colorList.addAll(originalColorList);

        adapter = new ColorAdapter(getContext(), colorList);
        colorView.setAdapter(adapter);
    }

    private void setupListeners() {
        searchColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterColorByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không xử lý
            }
        });

        colorView.setOnItemClickListener((parent, view, position, id) -> {
            ColorModel selectedColor = colorList.get(position);
            showDeleteColorDialog(selectedColor);
        });
    }

    private void filterColorByName(String query) {
        List<ColorModel> filteredList = new ArrayList<>();

        for (ColorModel color : originalColorList) {
            if (TextUtils.isEmpty(query)
                    || String.valueOf(color.getIdColor()).contains(query)
                    || color.getColorName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(color);
            }
        }

        adapter.updateList(filteredList);
    }

    private void showDialogAddColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_color, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameAddColor);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = nameField.getText().toString().trim();

                if (validateInput(nameField, name)) {
                    ColorModel newColor = new ColorModel();
                    newColor.setColorName(name);

                    ColorDao colorDao = new ColorDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (colorDao.insert(newColor)) {
                        originalColorList.add(newColor);
                        filterColorByName(searchColor.getText().toString());
                        Toast.makeText(getContext(), "Thêm màu sắc thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm màu!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateInput(EditText nameField, String name) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Vui lòng nhập tên màu!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (originalColorList.stream().anyMatch(color -> color.getColorName().equalsIgnoreCase(name))) {
            Toast.makeText(getContext(), "Tên màu đã tồn tại!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showDeleteColorDialog(ColorModel color) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Xóa màu sắc");
        builder.setMessage("Bạn có chắc chắn muốn xóa màu \"" + color.getColorName() + "\" ?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            ColorDao colorDao = new ColorDao(new DatabaseHelper(getContext()).getWritableDatabase());
            if (colorDao.isColorUsedInWarehouse(color.getIdColor())) {
                Toast.makeText(getContext(), "Không thể xóa. Đang được sử dụng cho sản phẩm!", Toast.LENGTH_SHORT).show();
            } else {
                if (colorDao.deleteColor(color.getIdColor())) {
                    originalColorList.remove(color);
                    filterColorByName(searchColor.getText().toString());
                    Toast.makeText(getContext(), "Xóa màu sắc thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi xóa màu!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
