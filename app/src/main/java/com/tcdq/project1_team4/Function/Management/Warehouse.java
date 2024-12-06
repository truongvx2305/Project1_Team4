// Warehouse.java
package com.tcdq.project1_team4.Function.Management;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tcdq.project1_team4.Adapter.WarehouseAdapter;
import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.ProductDao;
import com.tcdq.project1_team4.Dao.WarehouseDao;
import com.tcdq.project1_team4.Model.WarehouseModel;
import com.tcdq.project1_team4.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Warehouse extends Fragment {
    private final List<WarehouseModel> originalWarehouseList = new ArrayList<>();
    private final List<WarehouseModel> warehouseList = new ArrayList<>();
    private WarehouseAdapter adapter;
    private ListView warehouseView;
    private EditText searchWarehouse;
    private TextView emptyTextView;
    private Bitmap selectedImageBitmap;
    private ImageView updateImageProduct;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                        if (updateImageProduct != null) {
                            updateImageProduct.post(() -> {
                                int width = updateImageProduct.getWidth();
                                int height = updateImageProduct.getHeight();
                                selectedImageBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
                                updateImageProduct.setImageBitmap(selectedImageBitmap);
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.warehouse, container, false);

        initializeUI(view);
        loadData();
        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        warehouseView = view.findViewById(R.id.listWarehouse);
        searchWarehouse = view.findViewById(R.id.searchWarehouse);
        FloatingActionButton btnAddProduct = view.findViewById(R.id.btn_addWarehouse);

        btnAddProduct.setOnClickListener(v -> showDialogAddProduct());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        WarehouseDao warehouseDao = new WarehouseDao(db);

        originalWarehouseList.clear();
        originalWarehouseList.addAll(warehouseDao.getAllProduct());

        warehouseList.clear();
        warehouseList.addAll(originalWarehouseList);

        if (warehouseList.isEmpty()) {
            warehouseView.setVisibility(View.GONE);
        } else {
            warehouseView.setVisibility(View.VISIBLE);
        }

        adapter = new WarehouseAdapter(getContext(), warehouseList);
        warehouseView.setAdapter(adapter);
    }

    private void setupListeners() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        WarehouseDao warehouseDao = new WarehouseDao(db);

        searchWarehouse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString(), warehouseDao);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        warehouseView.setOnItemClickListener((parent, view, position, id) -> {
            WarehouseModel product = warehouseList.get(position);
        });
    }

    private void filterProducts(String query, WarehouseDao warehouseDao) {
        List<WarehouseModel> filteredList = new ArrayList<>();
        for (WarehouseModel product : originalWarehouseList) {
            if (TextUtils.isEmpty(query) ||
                    product.getName(warehouseDao).toLowerCase().contains(query.toLowerCase()) ||
                    product.getColor(warehouseDao).toLowerCase().contains(query.toLowerCase()) ||
                    product.getSize(warehouseDao).toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateProductList(filteredList);
    }

    private void showDialogAddProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_warehouse, null);
        builder.setView(dialogView);

        updateImageProduct = dialogView.findViewById(R.id.warehouseImageAdd);
        Spinner nameSpinner = dialogView.findViewById(R.id.nameProductSp);
        TextView txvType = dialogView.findViewById(R.id.txv_typeWarehouseAdd);
        TextView txvBrand = dialogView.findViewById(R.id.txv_brandWarehouseAdd);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSp);
        Spinner sizeSpinner = dialogView.findViewById(R.id.sizeSp);
        EditText quantityField = dialogView.findViewById(R.id.edt_quantityWarehouseAdd);
        EditText entryPriceField = dialogView.findViewById(R.id.edt_entryPriceWarehouseAdd);
        EditText exitPriceField = dialogView.findViewById(R.id.edt_exitPriceWarehouseAdd);

        selectedImageBitmap = null;
        updateImageProduct.setImageResource(R.drawable.type);

        WarehouseDao warehouseDao = new WarehouseDao(new DatabaseHelper(getContext()).getReadableDatabase());
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, warehouseDao.getAllName());
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, warehouseDao.getAllColor());
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, warehouseDao.getAllSize());

        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(nameAdapter);
        colorSpinner.setAdapter(colorAdapter);
        sizeSpinner.setAdapter(sizeAdapter);

        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = nameSpinner.getSelectedItem().toString();
                String[] brandAndType = warehouseDao.getBrandAndTypeByName(selectedName);
                if (brandAndType != null) {
                    txvType.setText(brandAndType[0]); // Loại sản phẩm
                    txvBrand.setText(brandAndType[1]); // Thương hiệu
                } else {
                    txvType.setText("");
                    txvBrand.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                txvType.setText("");
                txvBrand.setText("");
            }
        });

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String quantity = quantityField.getText().toString().trim();
            String entryPrice = entryPriceField.getText().toString().trim();
            String exitPrice = exitPriceField.getText().toString().trim();

            if (TextUtils.isEmpty(quantity)) {
                Toast.makeText(getContext(), "Vui lòng nhập số lượng!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(entryPrice)) {
                Toast.makeText(getContext(), "Vui lòng nhập giá nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(exitPrice)) {
                Toast.makeText(getContext(), "Vui lòng nhập giá xuất!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xử lý thêm sản phẩm
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
