package com.tcdq.project1_team4.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcdq.project1_team4.Model.TypeModel;
import com.tcdq.project1_team4.R;

import java.util.List;

public class TypeAdapter extends BaseAdapter {
    private final Context context;             // Ngữ cảnh của Activity hoặc Fragment
    private final List<TypeModel> typeList;  // Danh sách thương hiệu

    public void updateList(List<TypeModel> newList) {
        typeList.clear();
        typeList.addAll(newList);
        notifyDataSetChanged();
    }

    // Constructor
    public TypeAdapter(Context context, List<TypeModel> typeList) {
        this.context = context;
        this.typeList = typeList;
    }

    @Override
    public int getCount() {
        return typeList.size(); // Số lượng thương hiệu trong danh sách
    }

    @Override
    public Object getItem(int position) {
        return typeList.get(position); // Lấy đối tượng BrandModel tại vị trí position
    }

    @Override
    public long getItemId(int position) {
        return typeList.get(position).getIdType(); // Trả về ID của thương hiệu
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate layout item cho Spinner hoặc ListView
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_type, parent, false);
            holder = new ViewHolder();
            holder.nameType = convertView.findViewById(R.id.nameType); // Kết nối TextView
            convertView.setTag(holder); // Gắn ViewHolder vào convertView
        } else {
            holder = (ViewHolder) convertView.getTag(); // Lấy lại ViewHolder nếu view đã tái sử dụng
        }

        // Gán dữ liệu vào view
        TypeModel type = typeList.get(position);
        holder.nameType.setText("Loại sản phẩm: " + type.getTypeName());


        return convertView;
    }

    // ViewHolder để tái sử dụng view
    static class ViewHolder {
        TextView nameType;
    }
}
