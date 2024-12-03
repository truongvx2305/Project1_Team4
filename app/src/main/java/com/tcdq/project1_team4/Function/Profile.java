package com.tcdq.project1_team4.Function;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tcdq.project1_team4.DB.DatabaseHelper;
import com.tcdq.project1_team4.Dao.UserDao;
import com.tcdq.project1_team4.Model.UserModel;
import com.tcdq.project1_team4.Navigation;
import com.tcdq.project1_team4.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** @noinspection ALL */
public class Profile extends Fragment {

    private UserDao userDao;
    private UserModel userModel;
    private String username;
    private ImageView imageProfile;
    private ImageView updateImageProfile;
    private Bitmap selectedImageBitmap;

    // ActivityResultLauncher để chọn ảnh từ thư viện
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);

                        // Lấy kích thước ImageView
                        int imageViewWidth = updateImageProfile.getWidth();
                        int imageViewHeight = updateImageProfile.getHeight();

                        // Scale ảnh để vừa với ImageView
                        selectedImageBitmap = Bitmap.createScaledBitmap(originalBitmap, imageViewWidth, imageViewHeight, true);

                        // Hiển thị ảnh trong ImageView của dialog
                        updateImageProfile.setImageBitmap(selectedImageBitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    // Set username từ ngoài vào
    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);
        imageProfile = view.findViewById(R.id.imageProfile);
        TextView txv_idUser = view.findViewById(R.id.txv_idUser);
        TextView txv_nameUser = view.findViewById(R.id.txv_nameUser);
        TextView txv_emailUser = view.findViewById(R.id.txv_emailUser);
        TextView txv_phoneNumberUser = view.findViewById(R.id.txv_phoneNumberUser);
        TextView txv_roleUser = view.findViewById(R.id.txv_roleUser);
        Button btn_updateProfile = view.findViewById(R.id.btn_updateProfile);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db);

        // Tải thông tin profile vào giao diện
        uploadProfile(imageProfile, txv_idUser, txv_nameUser, txv_emailUser, txv_phoneNumberUser, txv_roleUser);

        // Sự kiện nhấn nút "Cập nhật"
        btn_updateProfile.setOnClickListener(v -> dialogUpdateProfile(txv_idUser, txv_nameUser, txv_emailUser, txv_phoneNumberUser, txv_roleUser));

        return view;
    }

    private void uploadProfile(ImageView imageProfile, TextView txv_idUser, TextView txv_nameUser, TextView txv_emailUser, TextView txv_phoneNumberUser, TextView txv_roleUser) {
        userModel = userDao.getProfileByUsername(username);
        if (userModel != null) {
            txv_idUser.setText(String.valueOf(userModel.getId()));
            txv_nameUser.setText(userModel.getName());
            txv_emailUser.setText(userModel.getEmail());
            txv_phoneNumberUser.setText(userModel.getPhoneNumber());
            txv_roleUser.setText(userModel.getRole());

            if (userModel.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(userModel.getImage(), 0, userModel.getImage().length);
                imageProfile.setImageBitmap(bitmap);
            }
        }
    }

    private void saveImageToDatabase(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        if (userModel != null) {
            userModel.setImage(imageBytes);
            userDao.updateUserProfile(userModel);

            // Lưu ảnh vào SharedPreferences để cập nhật Navigation
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("updated_image", Base64.encodeToString(imageBytes, Base64.DEFAULT));
            editor.apply();

            // Thông báo Navigation Activity cập nhật lại header
            if (getActivity() instanceof Navigation) {
                ((Navigation) getActivity()).setupNavigationHeader();
            }
        }
    }


    private void dialogUpdateProfile(TextView txvIdUser, TextView txvNameUser, TextView txvEmailUser, TextView txvPhoneNumberUser, TextView txvRoleUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_profile, null);
        builder.setView(view);

        updateImageProfile = view.findViewById(R.id.updateImageProfile); // Lưu tham chiếu của ImageView trong dialog
        EditText edt_updateName = view.findViewById(R.id.edt_updateName);
        EditText edt_updateEmail = view.findViewById(R.id.edt_updateEmail);
        EditText edt_updatePhoneNumber = view.findViewById(R.id.edt_updatePhoneNumber);
        Button btn_cancelProfile = view.findViewById(R.id.btn_cancelProfile);
        Button btn_saveProfile = view.findViewById(R.id.btn_saveProfile);

        // Điền sẵn thông tin vào các trường sửa
        edt_updateName.setText(txvNameUser.getText().toString());
        edt_updateEmail.setText(txvEmailUser.getText().toString());
        edt_updatePhoneNumber.setText(txvPhoneNumberUser.getText().toString());

        // Hiển thị ảnh hiện tại trong dialog
        if (userModel.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(userModel.getImage(), 0, userModel.getImage().length);
            updateImageProfile.setImageBitmap(bitmap);
        }

        // Sự kiện chọn ảnh mới
        updateImageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý khi dialog bị đóng
        dialog.setOnDismissListener(dialogInterface -> updateImageProfile = null);

        // Sự kiện nút hủy
        btn_cancelProfile.setOnClickListener(v -> dialog.dismiss());

        // Sự kiện nút lưu
        btn_saveProfile.setOnClickListener(v -> {
            String name = edt_updateName.getText().toString().trim();
            String email = edt_updateEmail.getText().toString().trim();
            String phoneNumber = edt_updatePhoneNumber.getText().toString().trim();

            // Kiểm tra dữ liệu hợp lệ
            if (name.isEmpty()) {
                edt_updateName.setError("Tên không được để trống!");
                return;
            }

            if (!email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                edt_updateEmail.setError("Email không hợp lệ!");
                return;
            }

            if (phoneNumber.length() != 10 || !phoneNumber.matches("\\d+")) {
                edt_updatePhoneNumber.setError("Số điện thoại phải có 10 chữ số!");
                return;
            }

            // Kiểm tra trùng lặp email và số điện thoại
            if (userDao.isEmailExists(email) && !email.equals(userModel.getEmail())) {
                edt_updateEmail.setError("Email này đã được sử dụng bởi người dùng khác!");
                return;
            }

            if (userDao.isPhoneNumberExists(phoneNumber) && !phoneNumber.equals(userModel.getPhoneNumber())) {
                edt_updatePhoneNumber.setError("Số điện thoại này đã được sử dụng bởi người dùng khác!");
                return;
            }

            // Cập nhật thông tin nếu hợp lệ
            userModel.setName(name);
            userModel.setEmail(email);
            userModel.setPhoneNumber(phoneNumber);

            // Lưu ảnh nếu có thay đổi
            if (selectedImageBitmap != null) {
                saveImageToDatabase(selectedImageBitmap);
                imageProfile.setImageBitmap(selectedImageBitmap); // Cập nhật ảnh hiển thị
            }

            // Cập nhật thông tin vào database
            userDao.updateUserProfile(userModel);
            uploadProfile(imageProfile, txvIdUser, txvNameUser, txvEmailUser, txvPhoneNumberUser, txvRoleUser);

            dialog.dismiss(); // Đóng dialog sau khi lưu
        });

    }
}
