package com.example.cdcnpmat.activities;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;

public class SupabaseClient {

    static final String SUPABASE_URL = "https://nqgjdcjznjbqefgyoicd.supabase.co";
    static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5xZ2pkY2p6bmpicWVmZ3lvaWNkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI2MzM2MTAsImV4cCI6MjA0ODIwOTYxMH0.quwMnNHNMUOQp3h92cdNkgk3y67Ufifiyut-MNDJBmQ";

    public final OkHttpClient client;
    public final String baseUrl;

    public SupabaseClient() {
        client = new OkHttpClient();
        baseUrl = SUPABASE_URL + "/rest/v1/";
    }

    public void getData(String tableName, Callback callback) {
        String url = baseUrl + tableName;

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }
    public void loginUser(String email, String password, Callback callback) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";

        // Tạo payload JSON cho đăng nhập
        JsonObject loginPayload = new JsonObject();
        loginPayload.addProperty("email", email);
        loginPayload.addProperty("password", password);

        RequestBody body = RequestBody.create(loginPayload.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
    public void registerUser(String email, String password, String phoneNumber, String role, String name, Callback callback) {
        String url = SUPABASE_URL + "/auth/v1/signup";

        // Tạo payload JSON cho đăng ký
        JsonObject registerPayload = new JsonObject();
        registerPayload.addProperty("email", email);
        registerPayload.addProperty("password", password);

        JsonObject userMetadata = new JsonObject();
        userMetadata.addProperty("phone_number", phoneNumber);
        userMetadata.addProperty("role", role);
        userMetadata.addProperty("password",password);
        userMetadata.addProperty("name",name);
        registerPayload.add("data", userMetadata);

        RequestBody body = RequestBody.create(registerPayload.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e); // Gọi callback để hiển thị lỗi đăng ký
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

                        // Supabase sẽ gửi email xác nhận, chỉ cần thông báo cho người dùng
                        callback.onResponse(call, response);

                    } catch (Exception e) {
                        callback.onFailure(call, new IOException("Error parsing registration response: " + e.getMessage()));
                    }
                } else {
                    callback.onResponse(call, response); // Trả về lỗi nếu đăng ký thất bại
                }
            }
        });
    }




}




