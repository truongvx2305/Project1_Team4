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
import android.widget.ImageView;
import android.widget.TextView;

import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.BrandDao;
import com.tcdq.project1_team4.Dao.ColorDao;
import com.tcdq.project1_team4.Dao.SizeDao;
import com.tcdq.project1_team4.Dao.TypeDao;
import com.tcdq.project1_team4.Dao.WarehouseDao;
import com.tcdq.project1_team4.Model.ProductModel;
import com.tcdq.project1_team4.Model.WarehouseModel;
import com.tcdq.project1_team4.R;

import java.util.List;

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

        // Hiển thị thông tin sản phẩm
        // Hiển thị ảnh sản phẩm
        byte[] imageBytes = product.getImage();
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgProduct.setImageBitmap(bitmap);
        } else {
            imgProduct.setImageResource(R.drawable.type); // Ảnh mặc định
        }

        // Hiển thị các thông tin khác
        WarehouseDao warehouseDao = new WarehouseDao(new DatabaseHelper(context).getReadableDatabase());
        tvName.setText(warehouseDao.getNameById(product.getIdProduct()));
        tvColor.setText("Màu: " + warehouseDao.getColorNameById(product.getIdColor()));
        tvSize.setText("Kích cỡ: " + warehouseDao.getSizeNameById(product.getIdSize()));
        tvExitPrice.setText("Giá xuất: " + product.getExitPrice() + " đ");
        tvQuantity.setText("Số lượng: " + product.getQuantity());
        tvStatus.setText("Trạng thái: " + (product.isStill() ? "Còn hàng" : "Hết hàng"));

        return convertView;
    }

    // Phương thức updateList để cập nhật lại danh sách khi tìm kiếm
    public void updateProductList(List<WarehouseModel> newList) {
        this.warehouseList = newList;
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }

}
