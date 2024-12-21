package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, phoneEditText, nameEditText, confirmpasswordEditText;
    private Button registerButton;
    private TextView loginLink;
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
        confirmpasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        loginLink.setOnClickListener(v -> {
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
        String confirmPassword = confirmpasswordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String role = "user"; // Role mặc định là "user"

        // Kiểm tra thông tin đầu vào
        if (!validateInput(email, password, confirmPassword, phone, name)) return;

        // Gọi SupabaseClient để thực hiện đăng ký
        supabaseClient.registerUser(email, password, phone, role, name, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    });
                } else {
                    String errorMessage = response.body().string();
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    /**
     * Hàm kiểm tra dữ liệu đầu vào
     */
    private boolean validateInput(String email, String password, String confirmPassword, String phone, String name) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Vui lòng nhập email.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email không hợp lệ.");
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Vui lòng nhập tên.");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Vui lòng nhập số điện thoại.");
            return false;
        }
        if (!phone.matches("^[0-9]{10,12}$")) { // Kiểm tra định dạng số điện thoại
            phoneEditText.setError("Số điện thoại không hợp lệ.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Vui lòng nhập mật khẩu.");
            return false;
        }
        if (password.length() < 8 || password.length() > 32) {
            passwordEditText.setError("Mật khẩu phải từ 8 đến 32 ký tự.");
            return false;
        }
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            passwordEditText.setError("Mật khẩu phải chứa ít nhất 1 chữ cái, 1 số và 1 ký tự đặc biệt.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmpasswordEditText.setError("Mật khẩu xác nhận không khớp.");
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
