package com.tcdq.project1_team4.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcdq.project1_team4.Model.ColorModel;
import com.tcdq.project1_team4.R;

import java.util.List;

public class ColorAdapter extends BaseAdapter {
    private final Context context;             // Ngữ cảnh của Activity hoặc Fragment
    private final List<ColorModel> colorList;  // Danh sách thương hiệu

    public void updateList(List<ColorModel> newList) {
        colorList.clear();
        colorList.addAll(newList);
        notifyDataSetChanged();
    }

    // Constructor


    public ColorAdapter(Context context, List<ColorModel> colorList) {
        this.context = context;
        this.colorList = colorList;
    }

    @Override
    public int getCount() {
        return colorList.size(); // Số lượng thương hiệu trong danh sách
    }

    @Override
    public Object getItem(int position) {
        return colorList.get(position); // Lấy đối tượng BrandModel tại vị trí position
    }

    @Override
    public long getItemId(int position) {
        return colorList.get(position).getIdColor(); // Trả về ID của thương hiệu
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate layout item cho Spinner hoặc ListView
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_color, parent, false);
            holder = new ViewHolder();
            holder.nameColor = convertView.findViewById(R.id.nameColor); // Kết nối TextView
            convertView.setTag(holder); // Gắn ViewHolder vào convertView
        } else {
            holder = (ViewHolder) convertView.getTag(); // Lấy lại ViewHolder nếu view đã tái sử dụng
        }

        // Gán dữ liệu vào view
        ColorModel color = colorList.get(position);
        holder.nameColor.setText("Màu: " + color.getColorName());


        return convertView;
    }

    // ViewHolder để tái sử dụng view
    static class ViewHolder {
        TextView nameColor;
    }
}
