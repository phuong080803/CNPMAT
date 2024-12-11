package com.example.cdcnpmat.activities;

import static com.example.cdcnpmat.activities.SupabaseClient.SUPABASE_KEY;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.R;
import com.google.gson.JsonObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, phoneEditText, nameEditText, confirmpasswordEditText;
    private Button registerButton;
    private TextView loginlink;
    private SupabaseClient supabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ các thành phần trong giao diện
        emailEditText = findViewById(R.id.register_email);
        nameEditText = findViewById(R.id.register_username);
        passwordEditText = findViewById(R.id.register_password);
        phoneEditText = findViewById(R.id.register_phone);
        loginlink = findViewById(R.id.login_link);
        confirmpasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);
        loginlink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        // Khởi tạo SupabaseClient
        supabaseClient = new SupabaseClient();

        // Đặt sự kiện click cho nút đăng ký
        registerButton.setOnClickListener(v -> registerUser());
    }

    /**
     * Hàm xử lý đăng ký người dùng
     */
    private void registerUser() {
        // Lấy thông tin từ các EditText
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String name =  nameEditText.getText().toString().trim();
        String role = "user"; // Role mặc định là "user"

        // Kiểm tra thông tin đầu vào
        if (!validateInput(email, password, phone, name)) return;

        // Gọi SupabaseClient để thực hiện đăng ký
        supabaseClient.registerUser(email, password, phone, role, name, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // Xử lý khi có lỗi xảy ra trong quá trình đăng ký
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Nếu đăng ký thành công
                    runOnUiThread(() -> {

                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    });
                } else {
                    // Nếu đăng ký thất bại
                    String errorMessage = response.body().string();
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    /**
     * Hàm kiểm tra dữ liệu đầu vào
     */
    private boolean validateInput(String email, String password, String phone, String name) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Phone is required");
            return false;
        }
        return true;
    }

    /**
     * Hàm điều hướng đến màn hình đăng nhập
     */
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
