package com.tcdq.project1_team4.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.ColorDao;
import com.tcdq.project1_team4.Dao.SizeDao;
import com.tcdq.project1_team4.Dao.WarehouseDao;
import com.tcdq.project1_team4.Model.WarehouseModel;
import com.tcdq.project1_team4.R;

import java.util.List;

/**
 * @noinspection ALL
 */
public class WarehouseAdapter extends BaseAdapter {
    private final Context context;
    private List<WarehouseModel> warehouseList;
    private final ColorDao colorDao;
    private final SizeDao sizeDao;

    public WarehouseAdapter(Context context, List<WarehouseModel> warehouseList) {
        this.context = context;
        this.warehouseList = warehouseList;

        // Khởi tạo Dao trong constructor
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        this.colorDao = new ColorDao(db);
        this.sizeDao = new SizeDao(db);
    }

    @Override
    public int getCount() {
        return warehouseList.size();
    }

    @Override
    public Object getItem(int position) {
        return warehouseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return warehouseList.get(position).getIdProduct();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_warehouse, parent, false);
        }

        WarehouseModel product = warehouseList.get(position);

        ImageView imgProduct = convertView.findViewById(R.id.imgProductWarehouse);
        TextView tvName = convertView.findViewById(R.id.nameProductWarehouse);
        TextView tvColor = convertView.findViewById(R.id.colorProductWarehouse);
        TextView tvSize = convertView.findViewById(R.id.sizeProductWarehouse);
        TextView tvExitPrice = convertView.findViewById(R.id.exitPriceProductWarehouse);
        TextView tvQuantity = convertView.findViewById(R.id.quantityProductWarehouse);
        TextView tvStatus = convertView.findViewById(R.id.statusProductWarehouse);
        ImageView errorProductWarehouse = convertView.findViewById(R.id.errorProductWarehouse);
        LinearLayout linearLayoutWarehouse = convertView.findViewById(R.id.linearLayoutWarehouse);
        CheckBox checkBox = convertView.findViewById(R.id.checkBoxProductWarehouse);

        // Hiển thị thông tin sản phẩm
        byte[] imageBytes = product.getImage();
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgProduct.setImageBitmap(bitmap);
        } else {
            imgProduct.setImageResource(R.drawable.type);
        }

        WarehouseDao warehouseDao = new WarehouseDao(new DatabaseHelper(context).getReadableDatabase());
        tvName.setText(warehouseDao.getNameById(product.getIdProduct()));
        tvColor.setText("Màu: " + warehouseDao.getColorNameById(product.getIdColor()));
        tvSize.setText("Kích cỡ: " + warehouseDao.getSizeNameById(product.getIdSize()));
        tvExitPrice.setText("Giá xuất: " + product.getExitPrice() + " đ");
        tvQuantity.setText("Số lượng: " + product.getQuantity());
        tvStatus.setText("Trạng thái: " + (product.getStatus()));

        // Kiểm tra số lượng và thay đổi giao diện
        int quantity = product.getQuantity();
        if (quantity > 0 && quantity < 10) {
            errorProductWarehouse.setColorFilter(context.getResources().getColor(R.color.red)); // Đổi màu thành đỏ
            errorProductWarehouse.setVisibility(View.VISIBLE);

            // Xử lý khi nhấn vào biểu tượng lỗi
            errorProductWarehouse.setOnClickListener(v -> {
                Toast.makeText(context, "Sản phẩm còn ít. Vui lòng nhập thêm số lượng!", Toast.LENGTH_SHORT).show();
            });
        } else {
            errorProductWarehouse.setVisibility(View.GONE);
        }

        if (quantity == 0) {
            linearLayoutWarehouse.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray)); // Đổi màu nền thành xám
        } else {
            linearLayoutWarehouse.setBackgroundTintList(context.getResources().getColorStateList(R.color.white)); // Màu nền mặc định
        }

        checkBox.setOnClickListener(v -> {
            if (quantity > 0) {
                // Chuyển đổi trạng thái của checkbox
                boolean isChecked = checkBox.isChecked();
                checkBox.setChecked(isChecked); // Tự động thay đổi trạng thái
            } else {
                // Nếu hết hàng thì không cho phép chọn
                checkBox.setChecked(false);
                Toast.makeText(context, "Sản phẩm đã hết hàng, không thể chọn!", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    // Phương thức updateList để cập nhật lại danh sách khi tìm kiếm
    public void updateProductList(List<WarehouseModel> newList) {
        this.warehouseList = newList;
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }
}
