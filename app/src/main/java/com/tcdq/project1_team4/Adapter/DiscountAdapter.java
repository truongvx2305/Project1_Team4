package com.tcdq.project1_team4.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.DiscountDao;
import com.tcdq.project1_team4.Model.DiscountModel;
import com.tcdq.project1_team4.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** @noinspection ALL */
public class DiscountAdapter extends BaseAdapter {
    private final Context context;
    private final List<DiscountModel> discountList;

    public DiscountAdapter(Context context, List<DiscountModel> discountList) {
        this.context = context;
        this.discountList = new ArrayList<>(discountList);
    }

    public void updateList(List<DiscountModel> newList) {
        discountList.clear();
        discountList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discountList.size();
    }

    @Override
    public Object getItem(int position) {
        return discountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForColorStateLists"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_discount, parent, false);

            holder = new ViewHolder();
            holder.imgDiscount = convertView.findViewById(R.id.imgDiscount);
            holder.nameDiscount = convertView.findViewById(R.id.nameDiscount);
            holder.minPriceDiscount = convertView.findViewById(R.id.minPriceDiscount);
            holder.quantityDiscount = convertView.findViewById(R.id.quantityDiscount);
            holder.endDateDiscount = convertView.findViewById(R.id.endDateDiscount);
            holder.statusDiscount = convertView.findViewById(R.id.statusDiscount);
            holder.linearLayoutDiscount = convertView.findViewById(R.id.linearLayoutDiscount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Gán dữ liệu từ DiscountModel vào View
        DiscountModel discount = discountList.get(position);
        holder.nameDiscount.setText(discount.getName());
        holder.minPriceDiscount.setText("Giá tối thiểu: " + discount.getMinOrderPrice() + " VND");
        holder.quantityDiscount.setText("Số lượng: " + discount.getQuantity());
        holder.endDateDiscount.setText("Ngày kết thúc: " + discount.getEndDate());
        holder.statusDiscount.setText("Trạng thái: " + discount.getStatus());

        holder.imgDiscount.setImageResource(R.drawable.discount2);

        // Đổi màu nền của LinearLayout dựa trên trạng thái
        if (discount.isValid()) {
            holder.linearLayoutDiscount.setBackgroundTintList(context.getResources().getColorStateList(R.color.white)); // Màu trắng
        } else {
            holder.linearLayoutDiscount.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray)); // Màu xám
        }

        // Xử lý sự kiện click để cập nhật
        convertView.setOnClickListener(v -> showUpdateDialog(discount));

        return convertView;
    }

    private void showUpdateDialog(DiscountModel discount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_discount, null);
        builder.setView(dialogView);

        EditText minPriceField = dialogView.findViewById(R.id.minPriceUpdateDiscount);
        EditText endDateField = dialogView.findViewById(R.id.endDateUpdateDiscount);
        EditText quantityField = dialogView.findViewById(R.id.quantityUpdateDiscount);

        minPriceField.setText(String.valueOf(discount.getMinOrderPrice()));
        endDateField.setText(discount.getEndDate());
        quantityField.setText(String.valueOf(discount.getQuantity()));

        // Thêm DatePickerDialog cho endDateField
        endDateField.setOnClickListener(v -> {
            String[] dateParts = endDateField.getText().toString().split("-");
            int year, month, day;

            // Nếu có giá trị hiện tại thì dùng làm ngày mặc định, nếu không dùng ngày hiện tại
            if (dateParts.length == 3) {
                year = Integer.parseInt(dateParts[0]);
                month = Integer.parseInt(dateParts[1]) - 1; // Tháng bắt đầu từ 0
                day = Integer.parseInt(dateParts[2]);
            } else {
                // Lấy ngày hiện tại
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, selectedYear, selectedMonth, selectedDay) -> {
                // Cập nhật ngày được chọn vào EditText
                @SuppressLint("DefaultLocale") String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                endDateField.setText(formattedDate);
            }, year, month, day);

            datePickerDialog.show();
        });

        builder.setPositiveButton("Cập nhật", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            updateButton.setOnClickListener(v -> {
                String newMinPrice = minPriceField.getText().toString().trim();
                String newEndDate = endDateField.getText().toString().trim();
                String newQuantity = quantityField.getText().toString().trim();

                if (validateInput(minPriceField, endDateField, quantityField, newMinPrice, newEndDate, newQuantity)) {
                    discount.setMinOrderPrice(Integer.parseInt(newMinPrice));
                    discount.setEndDate(newEndDate);
                    discount.setQuantity(Integer.parseInt(newQuantity));

                    DiscountDao discountDao = new DiscountDao(new DatabaseHelper(context).getWritableDatabase());
                    if (discountDao.update(discount)) {
                        updateList(discountDao.getAlLDiscount());
                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateInput(EditText minPriceField, EditText endDateField, EditText quantityField, String minPrice, String endDate, String quantity) {
        // Kiểm tra giá trị giá đơn hàng
        if (minPrice.isEmpty() || !minPrice.matches("\\d+(\\.\\d{1,2})?")) {
            minPriceField.setError("Giá đơn hàng không hợp lệ!");
            return false;
        }

        // Kiểm tra ngày kết thúc có đúng định dạng yyyy-MM-dd
        if (endDate.isEmpty()) {
            endDateField.setError("Vui lòng nhập ngày kết thúc!");
            return false;
        }

        // Kiểm tra ngày nhập có đúng định dạng yyyy-MM-dd
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            Date parsedEndDate = dateFormat.parse(endDate);
            Date currentDate = new Date();

            // Kiểm tra nếu ngày kết thúc nhỏ hơn ngày hiện tại
            if (parsedEndDate != null && parsedEndDate.before(currentDate)) {
                endDateField.setError("Ngày kết thúc phải lớn hơn hoặc bằng ngày hiện tại!");
                return false;
            }
        } catch (ParseException e) {
            endDateField.setError("Ngày phải có định dạng yyyy-MM-dd!");
            return false;
        }

        // Kiểm tra số lượng
        try {
            int qty = Integer.parseInt(quantity);
            if (qty < 0) {
                quantityField.setError("Số lượng không hợp lệ!");
                return false;
            }
        } catch (NumberFormatException e) {
            quantityField.setError("Số lượng phải là một số nguyên!");
            return false;
        }

        return true;
    }

    private static class ViewHolder {
        ImageView imgDiscount;
        TextView nameDiscount;
        TextView minPriceDiscount;
        TextView quantityDiscount;
        TextView endDateDiscount;
        TextView statusDiscount;
        LinearLayout linearLayoutDiscount;
    }
}
