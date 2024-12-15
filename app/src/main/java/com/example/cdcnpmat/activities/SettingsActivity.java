package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.R;

public class SettingsActivity extends AppCompatActivity {
    private ImageView homeicon, profileicon, authorArticle, settingicon;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        profileicon = findViewById(R.id.image_account_circle);
        homeicon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingicon = findViewById(R.id.setting);
        // Initialize navigation buttons


        // Add functionality to individual settings options
        findViewById(R.id.container_group_Notifications);
        findViewById(R.id.container_group_Security);
        findViewById(R.id.container_group_Appearance);
        findViewById(R.id.container_group_help);
        findViewById(R.id.container_group_Logout).setOnClickListener(v->logoutUser());
        profileicon.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "unknow");
            if (isUserLoggedIn()) {
                if (role.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(SettingsActivity.this, ProfileAdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(SettingsActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        authorArticle.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
                String role = sharedPreferences.getString("role", "unknow");
                if (role.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(SettingsActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SettingsActivity.this, HomePageActivity.class);
                    startActivity(intent);
                }

            } else {
                Intent intent = new Intent(SettingsActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        homeicon.setOnClickListener(v->{
            Intent intent = new Intent(SettingsActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
        settingicon.setOnClickListener(v->{
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
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
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
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
