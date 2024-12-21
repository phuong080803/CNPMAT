package com.example.cdcnpmat.activities;

import static com.example.cdcnpmat.activities.SupabaseClient.SUPABASE_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    public boolean islogin = false;
    private SupabaseClient supabaseClient;
    private static final int MAX_FAILED_ATTEMPTS = 5; // Số lần thử tối đa
    private static final long LOCKOUT_DURATION = 1 * 60 * 1000; // Thời gian khóa (1 phút)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);

        supabaseClient = new SupabaseClient();

        loginButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra trạng thái khóa đăng nhập
        if (isLoginBlocked()) {
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE);
            long lockoutEndTime = sharedPreferences.getLong("lockout_end_time", 0);
            long remainingTime = (lockoutEndTime - System.currentTimeMillis()) / 1000; // Thời gian còn lại (giây)

            // Thông báo cho người dùng
            Toast.makeText(this, "Đăng nhập bị khóa. Vui lòng thử lại sau " + remainingTime + " giây.", Toast.LENGTH_LONG).show();

            // Vô hiệu hóa nút đăng nhập
            loginButton.setEnabled(false);
        } else {
            // Cho phép người dùng đăng nhập lại sau khi hết thời gian khóa
            loginButton.setEnabled(true);
        }
    }

    private void loginUser() {
        if (isLoginBlocked()) {
            long lockoutEndTime = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE)
                    .getLong("lockout_end_time", 0);
            long remainingTime = (lockoutEndTime - System.currentTimeMillis()) / 1000; // Tính thời gian còn lại (giây)
            Toast.makeText(this, "Đăng nhập bị khóa. Thử lại sau " + remainingTime + " giây.", Toast.LENGTH_LONG).show();
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email không được để trống.");
            return;
        }
        if (!isValidEmail(email)) {
            emailEditText.setError("Email không hợp lệ.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Mật khẩu không được để trống.");
            return;
        }

        supabaseClient.loginUser(email, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    recordFailedAttempt(); // Ghi nhận lần thất bại
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    resetFailedAttempts(); // Reset số lần thất bại khi đăng nhập thành công
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String accessToken = jsonResponse.getString("access_token");
                        String refreshToken = jsonResponse.getString("refresh_token");
                        String userId = jsonResponse.getJSONObject("user").getString("id");
                        String phone = jsonResponse.getJSONObject("user").getJSONObject("user_metadata").getString("phone_number");
                        String role = jsonResponse.getJSONObject("user").getJSONObject("user_metadata").getString("role");
                        String name = jsonResponse.getJSONObject("user").getJSONObject("user_metadata").getString("name");

                        saveToken(accessToken, refreshToken, userId, email, phone, role, name, password);

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            intent.putExtra("islogin", true);
                            startActivity(intent);
                            finish();
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Lỗi phân tích phản hồi.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show();
                        recordFailedAttempt(); // Ghi nhận lần thất bại
                    });
                }
            }
        });
    }

    // Hàm kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    // Hàm kiểm tra mật khẩu an toàn
    private boolean isPasswordSafe(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";
        return password.matches(passwordPattern);
    }


    private void saveToken(String accessToken, String refreshToken, String userId, String email, String phone, String role, String name, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", accessToken);
        editor.putString("refresh_token", refreshToken);
        editor.putString("user_id", userId);
        editor.putString("email",email);
        editor.putString("password",password);
        editor.putString("phone", phone);
        editor.putString("role", role);
        editor.putString("name", name);
        editor.apply();
    }
    private void recordFailedAttempt() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int failedAttempts = sharedPreferences.getInt("failed_attempts", 0) + 1;

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            long lockoutEndTime = System.currentTimeMillis() + LOCKOUT_DURATION; // Khóa trong 1 phút
            editor.putLong("lockout_end_time", lockoutEndTime);
            failedAttempts = 0; // Reset số lần thử
            Toast.makeText(this, "Bạn đã bị khóa đăng nhập trong 1 phút.", Toast.LENGTH_LONG).show();
        }

        editor.putInt("failed_attempts", failedAttempts);
        editor.apply();
    }
    private boolean isLoginBlocked() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE);
        long lockoutEndTime = sharedPreferences.getLong("lockout_end_time", 0);
        return System.currentTimeMillis() < lockoutEndTime; // Trả về true nếu đang bị khóa
    }
    private void resetFailedAttempts() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("failed_attempts", 0);
        editor.apply();
    }


}
