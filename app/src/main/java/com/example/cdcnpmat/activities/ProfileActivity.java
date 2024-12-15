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

    private ImageView homeicon, profileicon, authorArticle, settingicon;
    private TextView emailProfile, phoneProfile, usernameProfile, fullNameText, emailText, roleText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Đảm bảo `activity_profile.xml` tồn tại
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");
        String name = sharedPreferences.getString("name", "Unknown");
        String roleT = sharedPreferences.getString("role","unknown");
        // Ánh xạ nút đăng xuất
        // Đảm bảo nút này tồn tại trong `activity_profile.xml`
        profileicon = findViewById(R.id.image_account_circle);
        homeicon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingicon = findViewById(R.id.setting);
        usernameProfile = findViewById(R.id.profileName);
        emailProfile = findViewById(R.id.profileEmail);
        phoneProfile = findViewById(R.id.phone);
        fullNameText = findViewById(R.id.fullName);
        emailText = findViewById(R.id.email);
        roleText = findViewById(R.id.role);
        emailProfile.setText(email);
        phoneProfile.setText(phone);
        usernameProfile.setText(name);
        fullNameText.setText(name);
        emailText.setText(email);
        roleText.setText(roleT);
        // Thêm sự kiện nhấn nút
        profileicon.setOnClickListener(v -> {

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
        });
        authorArticle.setOnClickListener(v -> {
            if (isUserLoggedIn()) {

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
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
}
