package com.tcdq.project1_team4.Function.Management;

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
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
import com.tcdq.project1_team4.Model.ProductModel;
import com.tcdq.project1_team4.Model.WarehouseModel;
import com.tcdq.project1_team4.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @noinspection ALL, unused , unused , unused , unused
 */
public class Warehouse extends Fragment {
    private final List<WarehouseModel> originalWarehouseList = new ArrayList<>();
    private final List<WarehouseModel> warehouseList = new ArrayList<>();
    private WarehouseAdapter adapter;
    private ListView warehouseView;
    private EditText searchWarehouse;
    FloatingActionButton btnAddProduct;
    CheckBox checkBox;
    /**
     * @noinspection unused
     */
    private TextView emptyTextView;
    private Bitmap selectedImageBitmap;
    private ImageView updateImageProduct;
    /**
     * @noinspection unused
     */
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
        btnAddProduct = view.findViewById(R.id.btn_addWarehouse);
        checkBox = view.findViewById(R.id.checkBoxProductWarehouse);
    }

    /**
     * @noinspection resource
     */
    private void loadData() {
        //noinspection resource
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

    /**
     * @noinspection resource, unused
     */
    private void setupListeners() {
        //noinspection resource
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

        btnAddProduct.setOnClickListener(v -> showDialogAddProduct());

        warehouseView.setOnItemClickListener((parent, view, position, id) -> {
            WarehouseModel product = warehouseList.get(position);
            showActionDialog(product);
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

    /**
     * @noinspection resource, resource , DataFlowIssue
     */
    private void showDialogAddProduct() {
        //noinspection DataFlowIssue
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

        //noinspection resource
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = nameSpinner.getSelectedItem().toString();
                String[] brandAndType = warehouseDao.getBrandAndTypeByName(selectedName);
                if (brandAndType != null) {
                    txvType.setText("Loại sản phẩm: " + brandAndType[0]); // Loại sản phẩm
                    txvBrand.setText("Thương hiệu: " + brandAndType[1]); // Thương hiệu
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

        updateImageProduct.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String quantity = quantityField.getText().toString().trim();
            String entryPrice = entryPriceField.getText().toString().trim();
            String exitPrice = exitPriceField.getText().toString().trim();
            String selectedName = nameSpinner.getSelectedItem().toString();
            String selectedColor = colorSpinner.getSelectedItem().toString();
            String selectedSize = sizeSpinner.getSelectedItem().toString();

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

            if (selectedImageBitmap == null) {
                Toast.makeText(getContext(), "Vui lòng chọn ảnh sản phẩm!", Toast.LENGTH_SHORT).show();
                return;
            }

            //noinspection resource
            WarehouseDao warehouseWrite = new WarehouseDao(new DatabaseHelper(getContext()).getWritableDatabase());
            int productId = warehouseWrite.getProductIdByName(selectedName);
            int colorId = warehouseWrite.getColorIdByName(selectedColor);
            int sizeId = warehouseWrite.getSizeIdByName(selectedSize);

            if (productId == -1 || colorId == -1 || sizeId == -1) {
                Toast.makeText(getContext(), "Tên, màu sắc hoặc kích thước sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            WarehouseModel newProduct = new WarehouseModel();
            newProduct.setIdProduct(productId);
            newProduct.setImage(convertBitmapToByteArray(selectedImageBitmap));
            newProduct.setIdColor(colorId);
            newProduct.setIdSize(sizeId);
            newProduct.setQuantity(Integer.parseInt(quantity));
            newProduct.setEntryDate(new Date().toString());
            newProduct.setEntryPrice(Float.parseFloat(entryPrice));
            newProduct.setExitPrice(Float.parseFloat(exitPrice));
            newProduct.setStill(true);

            if (warehouseWrite.insertOrUpdateProduct(newProduct)) {
                selectedImageBitmap = null; // Reset sau khi thêm/cập nhật thành công
                loadData();
                Toast.makeText(getContext(), "Thêm hoặc cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            } else {
                selectedImageBitmap = null; // Reset ngay cả khi thất bại
                Toast.makeText(getContext(), "Thêm hoặc cập nhật sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showActionDialog(WarehouseModel product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn hành động");
        builder.setItems(new String[]{"Sửa", "Xóa"}, (dialog, which) -> {
            if (which == 0) {
                // Sửa sản phẩm
                showDialogUpdateProduct(product);
            } else if (which == 1) {
                // Xóa sản phẩm
                confirmDeleteProduct(product);
            }
        });
        builder.show();
    }

    private void showDialogUpdateProduct(WarehouseModel product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_warehouse, null);
        builder.setView(dialogView);

        updateImageProduct = dialogView.findViewById(R.id.warehouseImageUpdate);
        TextView txvName = dialogView.findViewById(R.id.txv_nameProductUpdate);
        TextView txvType = dialogView.findViewById(R.id.txv_typeWarehouseUpdate);
        TextView txvBrand = dialogView.findViewById(R.id.txv_brandWarehouseUpdate);
        TextView txvColor = dialogView.findViewById(R.id.txv_colorWarehouseUpdate);
        TextView txvSize = dialogView.findViewById(R.id.txv_sizeWarehouseUpdate);
        TextView txvOldQuantity = dialogView.findViewById(R.id.txv_oldQuantityWarehouse);
        TextView txvOldEntryDate = dialogView.findViewById(R.id.txv_oldEntryDateWarehouse);
        TextView txvOldExitPrice = dialogView.findViewById(R.id.txv_oldExitPriceWarehouse);
        EditText newQuantity = dialogView.findViewById(R.id.edt_newQuantityWarehouse);
        EditText newEntryPrice = dialogView.findViewById(R.id.edt_newEntryPriceWarehouse);
        EditText newExitPrice = dialogView.findViewById(R.id.edt_newExitPriceWarehouse);

        // Hiển thị thông tin sản phẩm hiện tại
        if (product.getImage() != null) {
            updateImageProduct.setImageBitmap(convertByteArrayToBitmap(product.getImage()));
        } else {
            updateImageProduct.setImageResource(R.drawable.type);
        }
        txvName.setText("Tên sản phẩm: " + product.getName(new WarehouseDao(new DatabaseHelper(getContext()).getReadableDatabase())));

        // Lấy brand và type từ productId
        WarehouseDao warehouseDao = new WarehouseDao(new DatabaseHelper(getContext()).getReadableDatabase());
        String[] brandAndType = warehouseDao.getBrandAndTypeByProductId(product.getIdProduct());
        if (brandAndType != null && brandAndType.length == 2) {
            txvType.setText("Loại sản phẩm: " + brandAndType[0]);
            txvBrand.setText("Thương hiệu: " + brandAndType[1]);
        } else {
            txvType.setText("Loại sản phẩm: Không xác định");
            txvBrand.setText("Thương hiệu: Không xác định");
        }

        txvColor.setText("Màu: " + product.getColor(new WarehouseDao(new DatabaseHelper(getContext()).getReadableDatabase())));
        txvSize.setText("Kích cỡ: " + product.getSize(new WarehouseDao(new DatabaseHelper(getContext()).getReadableDatabase())));
        txvOldQuantity.setText("Số lượng: " + String.valueOf(product.getQuantity()));
        txvOldEntryDate.setText("Ngày nhập gần nhất: " + product.getEntryDate());
        txvOldExitPrice.setText("Giá xuất cũ: " + String.valueOf(product.getExitPrice()) + " đ");

        // Cập nhật sản phẩm
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            // Thực hiện cập nhật ở đây
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void confirmDeleteProduct(WarehouseModel product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            WarehouseDao warehouseDao = new WarehouseDao(new DatabaseHelper(getContext()).getWritableDatabase());
            if (warehouseDao.delete(product.getIdProduct())) {
                Toast.makeText(getContext(), "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                loadData();
            } else {
                Toast.makeText(getContext(), "Xóa sản phẩm thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * @noinspection unused
     */
    private Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        return null; // Trả về null nếu byteArray là null
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
