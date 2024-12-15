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

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        supabaseClient.loginUser(email, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                intent.putExtra("islogin", false);
                startActivity(intent);
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String accessToken = jsonResponse.getString("access_token");
                        String refreshToken = jsonResponse.getString("refresh_token");
                        String userId = jsonResponse.getJSONObject("user").getString("id");
                        String phone = jsonResponse.getJSONObject("user").getJSONObject("user_metadata").getString("phone_number");
                        String role = jsonResponse.getJSONObject("user").getJSONObject("user_metadata").getString("role");
                        String name = jsonResponse.getJSONObject("user").getJSONObject("user_metadata").getString("name");

                        // Lưu token và thông tin người dùng vào SharedPreferences
                        saveToken(accessToken, refreshToken, userId, email, phone, role, name);

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Kiểm tra vai trò và chuyển hướng
                            Intent intent;

                                intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            intent.putExtra("islogin", true);
                            startActivity(intent);
                            finish(); // Đóng LoginActivity
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    String errorMessage = response.body().string();
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void saveToken(String accessToken, String refreshToken, String userId, String email, String phone, String role, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", accessToken);
        editor.putString("refresh_token", refreshToken);
        editor.putString("user_id", userId);
        editor.putString("email",email);
        editor.putString("phone", phone);
        editor.putString("role", role);
        editor.putString("name", name);
        editor.apply();
    }

}
