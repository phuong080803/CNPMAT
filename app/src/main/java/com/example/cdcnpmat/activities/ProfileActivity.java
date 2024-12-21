package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.Model.DAOiplm.UsersDAOImpl;
import com.example.cdcnpmat.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView homeIcon, profileIcon, authorArticle, settingIcon, editButton;
    private EditText fullNameText, phoneText;
    private TextView emailText, usernameText, profileEmail;
    private Button saveButton;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ View
        profileEmail = findViewById(R.id.profileEmail);
        homeIcon = findViewById(R.id.image_home);
        profileIcon = findViewById(R.id.image_account_circle);
        authorArticle = findViewById(R.id.image_list);
        settingIcon = findViewById(R.id.setting);
        editButton = findViewById(R.id.editIcon);
        fullNameText = findViewById(R.id.fullName);
        phoneText = findViewById(R.id.phone);
        emailText = findViewById(R.id.email);
        usernameText = findViewById(R.id.profileName);
        saveButton = findViewById(R.id.save_button);
        profileIcon = findViewById(R.id.image_account_circle);
        homeIcon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingIcon = findViewById(R.id.setting);
        // Lấy dữ liệu người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");
        String name = sharedPreferences.getString("name", "Unknown");
        profileEmail.setText(email);
        profileIcon.setOnClickListener(v -> navigateToProfile());
        authorArticle.setOnClickListener(v -> navigateToAuthorArticles());
        homeIcon.setOnClickListener(v -> navigateToHomePage());
        settingIcon.setOnClickListener(v -> navigateToSettings());
        // Đặt dữ liệu vào View
        emailText.setText(email);
        phoneText.setText(phone);
        fullNameText.setText(name);
        usernameText.setText(name);

        // Ban đầu vô hiệu hóa chỉnh sửa
        setEditable(false);

        // Nút Edit
        editButton.setOnClickListener(v -> toggleEditMode());

        // Nút Save
        saveButton.setOnClickListener(v -> saveProfile());
    }
    private void navigateToProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "unknow");
        if (isUserLoggedIn()) {
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(ProfileActivity.this, ProfileAdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ProfileActivity.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToAuthorArticles() {
        if (isUserLoggedIn()) {
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "unknow");
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(ProfileActivity.this, AdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                startActivity(intent);
            }

        } else {
            Intent intent = new Intent(ProfileActivity.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
    private void toggleEditMode() {
        isEditing = !isEditing;
        setEditable(isEditing);
    }

    private void setEditable(boolean enabled) {
        fullNameText.setEnabled(enabled);
        phoneText.setEnabled(enabled);
        saveButton.setVisibility(enabled ? View.VISIBLE : View.GONE); // Hiển thị nút Save khi Edit mode

    }

    private void saveProfile() {
        String fullName = fullNameText.getText().toString().trim();
        String phone = phoneText.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy ID người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", phone);
        editor.putString("name",fullName);
        editor.apply();
        usernameText.setText(fullName);
        // Gọi DAO để cập nhật
        UsersDAOImpl usersDAO = new UsersDAOImpl();
        new Thread(() -> {
            usersDAO.updateProfile(userId, fullName, phone);
            runOnUiThread(() -> {
                Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                toggleEditMode(); // Thoát khỏi chế độ chỉnh sửa
            });
        }).start();
    }
}
