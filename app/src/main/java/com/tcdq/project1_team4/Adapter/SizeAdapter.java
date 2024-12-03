package com.tcdq.project1_team4.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcdq.project1_team4.Model.SizeModel;
import com.tcdq.project1_team4.R;

import java.util.List;

public class SizeAdapter extends BaseAdapter {
    private final Context context;
    private final List<SizeModel> sizeList;

    public SizeAdapter(Context context, List<SizeModel> sizeList) {
        this.context = context;
        this.sizeList = sizeList;
    }

    public void updateList(List<SizeModel> newList) {
        sizeList.clear();
        sizeList.addAll(newList);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return sizeList.size();
    }

    @Override
    public Object getItem(int position) {
        return sizeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return sizeList.get(position).getIdSize();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate layout item cho Spinner hoặc ListView
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_size, parent, false);
            holder = new ViewHolder();
            holder.nameSize = convertView.findViewById(R.id.nameSize); // Kết nối TextView
            convertView.setTag(holder); // Gắn ViewHolder vào convertView
        } else {
            holder = (ViewHolder) convertView.getTag(); // Lấy lại ViewHolder nếu view đã tái sử dụng
        }

        // Gán dữ liệu vào view
        SizeModel sizeModel = sizeList.get(position);
        holder.nameSize.setText("Kích cỡ: " + sizeModel.getSizeName());


        return convertView;
    }

    // ViewHolder để tái sử dụng view
    static class ViewHolder {
        TextView nameSize;
    }
}
