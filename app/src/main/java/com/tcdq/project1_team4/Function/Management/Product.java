package com.tcdq.project1_team4.Function.Management;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tcdq.project1_team4.Adapter.ProductAdapter;
import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.ProductDao;
import com.tcdq.project1_team4.Model.ProductModel;
import com.tcdq.project1_team4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection ALL
 */
public class Product extends Fragment {
    private final List<ProductModel> originalProductList = new ArrayList<>();
    private final List<ProductModel> productList = new ArrayList<>();
    private ProductAdapter adapter;
    private ListView productView;
    private EditText searchProduct;
    private TextView emptyTextView;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product, container, false);

        initializeUI(view);
        loadData();
        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        productView = view.findViewById(R.id.listProduct);
        searchProduct = view.findViewById(R.id.searchProduct);
        FloatingActionButton btnAddProduct = view.findViewById(R.id.btn_addProduct);

        btnAddProduct.setOnClickListener(v -> showDialogAddProduct());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ProductDao productDao = new ProductDao(db);

        originalProductList.clear();
        originalProductList.addAll(productDao.getAllProduct());

        productList.clear();
        productList.addAll(originalProductList);

        if (productList.isEmpty()) {
            productView.setVisibility(View.GONE);
        } else {
            productView.setVisibility(View.VISIBLE);
        }

        adapter = new ProductAdapter(getContext(), productList);
        productView.setAdapter(adapter);


    }

    private void setupListeners() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ProductDao productDao = new ProductDao(db);

        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString(), productDao);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        productView.setOnItemClickListener((parent, view, position, id) -> {
            ProductModel product = productList.get(position);
            showDialogDeleteProduct(product);
        });
    }

    private void filterProducts(String query, ProductDao productDao) {
        List<ProductModel> filteredList = new ArrayList<>();
        for (ProductModel product : originalProductList) {
            if (TextUtils.isEmpty(query) ||
                    product.getName().toLowerCase().contains(query.toLowerCase()) ||
                    product.getTypeName(productDao).toLowerCase().contains(query.toLowerCase()) ||
                    product.getBrandName(productDao).toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateProductList(filteredList);
    }


    private void showDialogAddProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameProductAdd);
        Spinner typeSpinner = dialogView.findViewById(R.id.typeProductSp);
        Spinner brandSpinner = dialogView.findViewById(R.id.brandProductSp);

        ProductDao productDao = new ProductDao(new DatabaseHelper(getContext()).getReadableDatabase());
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, productDao.getAllProductTypes());
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, productDao.getAllBrands());

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        brandSpinner.setAdapter(brandAdapter);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = nameField.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "Tên sản phẩm không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            int typeId = productDao.getProductTypeIdByName(typeSpinner.getSelectedItem().toString());
            int brandId = productDao.getBrandIdByName(brandSpinner.getSelectedItem().toString());

            if (productDao.isProductExists(name, typeId, brandId)) {
                Toast.makeText(getContext(), "Sản phẩm đã tồn tại!", Toast.LENGTH_SHORT).show();
            } else {
                ProductModel newProduct = new ProductModel();
                newProduct.setName(name);
                newProduct.setType(typeId);
                newProduct.setBrand(brandId);

                ProductDao productDaoWrite = new ProductDao(new DatabaseHelper(getContext()).getWritableDatabase());
                if (productDaoWrite.insertProduct(newProduct)) {
                    loadData();
                    Toast.makeText(getContext(), "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Thêm sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDialogDeleteProduct(ProductModel product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa sản phẩm");
        builder.setMessage("Bạn có chắc chắn muốn sản phẩm \"" + product.getName() + "\" ?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Kiểm tra xem sản phẩm đã được dùng trong bảng kho hàng chưa
            ProductDao dao = new ProductDao(new DatabaseHelper(getContext()).getReadableDatabase());
            boolean isProductUsed = dao.isProductUsedInWarehouse(product.getId());

            if (isProductUsed) {
                Toast.makeText(getContext(), "Không thể xóa. Đang được sử dụng trong kho hàng!", Toast.LENGTH_LONG).show();
            } else {
                // Xóa sản phẩm
                ProductDao productDao = new ProductDao(new DatabaseHelper(getContext()).getWritableDatabase());
                if (productDao.delete(product.getId())) {
                    loadData();
                    Toast.makeText(getContext(), "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Xóa sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}