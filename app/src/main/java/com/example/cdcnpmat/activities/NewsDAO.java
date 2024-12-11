package com.example.cdcnpmat.activities;

import androidx.annotation.NonNull;

import com.example.cdcnpmat.Model.News;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsDAO {
    private static final String TABLE_NAME = "articles"; // Tên bảng trong Supabase
    private static final String SUPABASE_URL = "https://smmxytaylcidtfxpmlic.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNtbXh5dGF5bGNpZHRmeHBtbGljIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzIwMDYyNTcsImV4cCI6MjA0NzU4MjI1N30.r2YpsTPnrjRdGCSKV_QLtFnYDAzjLyKeZVTEGjXWhrg";
    private final OkHttpClient client;
    private final Gson gson;

    public NewsDAO() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    // Thêm bài báo vào Supabase
    public void insert(News news, OnCompleteListener listener) {
        if (news.getId() == null || news.getId().isEmpty()) {
            news.setId(java.util.UUID.randomUUID().toString()); // Tạo ID ngẫu nhiên
        }

        String url = SUPABASE_URL + TABLE_NAME;
        String json = gson.toJson(news);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onComplete(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                listener.onComplete(response.isSuccessful());
            }
        });
    }

    // Cập nhật bài báo
    public void update(News news, OnCompleteListener listener) {
        if (news.getId() == null || news.getId().isEmpty()) {
            listener.onComplete(false);
            return;
        }

        String url = SUPABASE_URL + TABLE_NAME + "?id=eq." + news.getId();
        String json = gson.toJson(news);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onComplete(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                listener.onComplete(response.isSuccessful());
            }
        });
    }

    // Xóa bài báo
    public void delete(String id, OnCompleteListener listener) {
        if (id == null || id.isEmpty()) {
            listener.onComplete(false);
            return;
        }

        String url = SUPABASE_URL + TABLE_NAME + "?id=eq." + id;
        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onComplete(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                listener.onComplete(response.isSuccessful());
            }
        });
    }

    // Lấy toàn bộ bài báo
    public void getAll(OnDataFetchListener<List<News>> listener) {
        String url = SUPABASE_URL + TABLE_NAME;
        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFetch(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Type listType = new TypeToken<ArrayList<News>>() {}.getType();
                    List<News> newsList = gson.fromJson(json, listType);
                    listener.onFetch(newsList);
                } else {
                    listener.onFetch(null);
                }
            }
        });
    }

    // Lấy một bài báo theo ID
    public void getNews(String id, OnDataFetchListener<News> listener) {
        if (id == null || id.isEmpty()) {
            listener.onFetch(null);
            return;
        }

        String url = SUPABASE_URL + TABLE_NAME + "?id=eq." + id;
        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                listener.onFetch(null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    News news = gson.fromJson(json, News.class);
                    listener.onFetch(news);
                } else {
                    listener.onFetch(null);
                }
            }
        });
    }

    // Listener để xử lý callback khi hoàn thành
    public interface OnCompleteListener {
        void onComplete(boolean success);
    }

    // Listener để xử lý callback khi lấy dữ liệu
    public interface OnDataFetchListener<T> {
        void onFetch(T data);
    }
}
