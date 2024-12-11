package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.R;

public class ProfileActivity extends AppCompatActivity {
    private Button logoutButton;
    private ImageView homeicon, profileicon, authorArticle, settingicon;
    private TextView emailProfile, phoneProfile, usernameProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Đảm bảo `activity_profile.xml` tồn tại
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");
        String name = sharedPreferences.getString("name", "Unknown");
        // Ánh xạ nút đăng xuất
        logoutButton = findViewById(R.id.logoutButton); // Đảm bảo nút này tồn tại trong `activity_profile.xml`
        profileicon = findViewById(R.id.image_account_circle);
        homeicon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingicon = findViewById(R.id.setting);
        usernameProfile = findViewById(R.id.profileName);
        emailProfile = findViewById(R.id.profileEmail);
        phoneProfile = findViewById(R.id.profilePhone);
        emailProfile.setText(email);
        phoneProfile.setText(phone);
        usernameProfile.setText(name);
        // Thêm sự kiện nhấn nút
        logoutButton.setOnClickListener(v -> logoutUser());
        profileicon.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                // Nếu đã đăng nhập, chuyển đến ProfileActivity
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else {
                // Nếu chưa đăng nhập, chuyển đến Non_Login_Activity
                Intent intent = new Intent(ProfileActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        authorArticle.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                // Nếu đã đăng nhập, chuyển đến ProfileActivity
                Intent intent = new Intent(ProfileActivity.this, AuthorActivity.class);
                startActivity(intent);
            } else {
                // Nếu chưa đăng nhập, chuyển đến Non_Login_Activity
                Intent intent = new Intent(ProfileActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        homeicon.setOnClickListener(v->{
            Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
        settingicon.setOnClickListener(v->{
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void logoutUser() {
        // Xóa token khỏi SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa tất cả các dữ liệu trong SharedPreferences
        editor.apply();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack để không quay lại
        startActivity(intent);

        // Hiển thị thông báo
        finish();
    }
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
}
