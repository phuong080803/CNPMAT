package com.example.cdcnpmat.activities;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SupabaseClient {

    static final String SUPABASE_URL = "https://nqgjdcjznjbqefgyoicd.supabase.co";
    static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5xZ2pkY2p6bmpicWVmZ3lvaWNkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI2MzM2MTAsImV4cCI6MjA0ODIwOTYxMH0.quwMnNHNMUOQp3h92cdNkgk3y67Ufifiyut-MNDJBmQ";
    private static final int MAX_REQUESTS_PER_MINUTE = 5; // Giới hạn số request mỗi phút
    private static final RateLimiter loginLimiter = new RateLimiter(TimeUnit.MINUTES.toMillis(1), MAX_REQUESTS_PER_MINUTE);
    private static final RateLimiter registerLimiter = new RateLimiter(TimeUnit.MINUTES.toMillis(1), MAX_REQUESTS_PER_MINUTE);
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
        if (!loginLimiter.allowRequest(email)) {
            callback.onFailure(null, new IOException("Too many requests. Please try again later."));
            return;
        }

        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";
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
        if (!registerLimiter.allowRequest(email)) {
            callback.onFailure(null, new IOException("Too many requests. Please try again later."));
            return;
        }

        String url = SUPABASE_URL + "/auth/v1/signup";
        JsonObject registerPayload = new JsonObject();
        registerPayload.addProperty("email", email);
        registerPayload.addProperty("password", password);

        JsonObject userMetadata = new JsonObject();
        userMetadata.addProperty("phone_number", phoneNumber);
        userMetadata.addProperty("role", role);
        userMetadata.addProperty("password", password);
        userMetadata.addProperty("name", name);
        registerPayload.add("data", userMetadata);

        RequestBody body = RequestBody.create(registerPayload.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }




}




