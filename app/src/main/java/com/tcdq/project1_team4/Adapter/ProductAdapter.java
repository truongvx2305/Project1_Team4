package com.tcdq.project1_team4.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.BrandDao;
import com.tcdq.project1_team4.Dao.TypeDao;
import com.tcdq.project1_team4.Model.ProductModel;
import com.tcdq.project1_team4.R;

import java.util.List;

/** @noinspection ALL*/
public class ProductAdapter extends BaseAdapter {
    private final Context context;
    private List<ProductModel> productList;
    private final TypeDao typeDao;
    private final BrandDao brandDao;

    public ProductAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;

        // Khởi tạo Dao trong constructor
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        this.typeDao = new TypeDao(db);
        this.brandDao = new BrandDao(db);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_product, parent, false);
        }

        ProductModel product = productList.get(position);

        TextView tvName = convertView.findViewById(R.id.nameProduct);
        TextView tvType = convertView.findViewById(R.id.typeProduct);
        TextView tvBrand = convertView.findViewById(R.id.brandProduct);
        ImageView imgProduct = convertView.findViewById(R.id.imgProduct);

        // Hiển thị thông tin sản phẩm
        tvName.setText("Tên sản phẩm: " + product.getName());
        tvType.setText("Loại: " + typeDao.getTypeNameById(product.getIdProductType()));
        tvBrand.setText("Thương hiệu: " + brandDao.getBrandNameById(product.getIdBrand()));

        return convertView;
    }

    // Phương thức updateList để cập nhật lại danh sách khi tìm kiếm
    public void updateProductList(List<ProductModel> newList) {
        this.productList = newList;
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }

}
