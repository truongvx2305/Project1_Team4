package com.tcdq.project1_team4.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcdq.project1_team4.Model.BrandModel;
import com.tcdq.project1_team4.R;

import java.util.List;

public class BrandAdapter extends BaseAdapter {

    private final Context context;             // Ngữ cảnh của Activity hoặc Fragment
    private final List<BrandModel> brandList;  // Danh sách thương hiệu

    public void updateList(List<BrandModel> newList) {
        brandList.clear();
        brandList.addAll(newList);
        notifyDataSetChanged();
    }

    // Constructor
    public BrandAdapter(Context context, List<BrandModel> brandList) {
        this.context = context;
        this.brandList = brandList;
    }

    @Override
    public int getCount() {
        return brandList.size(); // Số lượng thương hiệu trong danh sách
    }

    @Override
    public Object getItem(int position) {
        return brandList.get(position); // Lấy đối tượng BrandModel tại vị trí position
    }

    @Override
    public long getItemId(int position) {
        return brandList.get(position).getIdBrand(); // Trả về ID của thương hiệu
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate layout item cho Spinner hoặc ListView
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_brand, parent, false);
            holder = new ViewHolder();
            holder.nameBrand = convertView.findViewById(R.id.nameBrand); // Kết nối TextView
            convertView.setTag(holder); // Gắn ViewHolder vào convertView
        } else {
            holder = (ViewHolder) convertView.getTag(); // Lấy lại ViewHolder nếu view đã tái sử dụng
        }

        // Gán dữ liệu vào view
        BrandModel brand = brandList.get(position);
        holder.nameBrand.setText("Thương hiệu: " + brand.getName());


        return convertView;
    }

    // ViewHolder để tái sử dụng view
    static class ViewHolder {
        TextView nameBrand;
    }
}
