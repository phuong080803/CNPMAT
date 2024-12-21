package com.example.cdcnpmat.Model.DAOiplm;

import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.Bean.Categories;
import com.example.cdcnpmat.Model.DAO.ArticlesDAO;
import com.example.cdcnpmat.Model.DAO.UpdateCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArticlesDAOImpl implements ArticlesDAO {
    private final String SUPABASE_URL = "https://nqgjdcjznjbqefgyoicd.supabase.co/rest/v1/";
    private final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5xZ2pkY2p6bmpicWVmZ3lvaWNkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI2MzM2MTAsImV4cCI6MjA0ODIwOTYxMH0.quwMnNHNMUOQp3h92cdNkgk3y67Ufifiyut-MNDJBmQ";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public  List<Articles> findAll() {
        List<Articles> articlesList = new ArrayList<>();
        String url = SUPABASE_URL + "articles";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Articles article = new Articles(
                            obj.getInt("id"),
                            obj.getString("title"),
                            obj.optString("publish_date"),
                            obj.optInt("views", 0),
                            obj.optString("abstract_content", ""),
                            obj.optString("content", ""),
                            obj.optInt("categories_id", 0),
                            obj.optInt("kinds_id", 0),
                            obj.optString("writer_id", ""),
                            obj.optInt("status_id", 0),
                            obj.optString("img")
                    );
                    articlesList.add(article);
                }
            } else {
                System.err.println("Fetch Articles API Error: " + response.code() + " - " + response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articlesList;
    }
    @Override
    public List<Articles> searchArticles(String key) {
        String url = SUPABASE_URL + "articles?or=(title.ilike.%25" + key + "%25,abstract_content.ilike.%25" + key + "%25,content.ilike.%25" + key + "%25)";
        return fetchArticles(url);
    }

    @Override
    public Articles findById(int id) {
        String url = SUPABASE_URL + "articles?id=eq." + id;
        List<Articles> articles = fetchArticles(url);
        return articles.isEmpty() ? null : articles.get(0);
    }

    @Override
    public List<Articles> top10AllCate() {
        String url = SUPABASE_URL + "articles?status_id=eq.1&order=views.desc&limit=10";
        return fetchArticles(url);
    }

    @Override
    public List<Articles> top5AllCateInWeek() {
        String url = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            url = SUPABASE_URL + "articles?status_id=eq.1&publish_date=gte." +
                    LocalDateTime.now().minusWeeks(1).toString() + "&order=views.desc&limit=5";
        }
        return fetchArticles(url);
    }

    @Override
    public void edit(int id, String title, String abstractContent, String content, int cateId) {
        String url = SUPABASE_URL + "articles?id=eq." + id;
        try {
            JSONObject body = new JSONObject();
            body.put("title", title);
            body.put("abstract_content", abstractContent);
            body.put("content", content);
            body.put("categories_id", cateId);

            RequestBody requestBody = RequestBody.create(body.toString(), okhttp3.MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .patch(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Articles article) {
        String url = SUPABASE_URL + "articles";
        try {
            JSONObject body = new JSONObject();
            body.put("title", article.getTitle());
            body.put("publish_date", article.getPublishDate());
            body.put("views",article.getViews());
            body.put("abstract_content", article.getAbstractContent());
            body.put("content", article.getContent());
            body.put("categories_id", article.getCategoriesId());
            body.put("writer_id", article.getWriterId().toString()); // UUID as string
            body.put("kinds_id", article.getKindsId());
            body.put("status_id", article.getStatusId());
            body.put("img",article.getImg());
            RequestBody requestBody = RequestBody.create(body.toString(), okhttp3.MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<Articles> fetchArticles(String url) {
        List<Articles> articlesList = new ArrayList<>();
        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Articles article = new Articles(
                            obj.getInt("id"),
                            obj.getString("title"),
                            obj.optString("publish_date", null),
                            obj.optInt("views", 0),
                            obj.getString("abstract_content"),
                            obj.getString("content"),
                            obj.getInt("categories_id"),
                            obj.optInt("kinds_id", 0),
                            obj.getString("writer_id"), // Parse writer_id as UUID
                            obj.optInt("status_id", 0),
                            obj.optString("img")
                    );
                    articlesList.add(article);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articlesList;
    }


    @Override
    public List<Articles> findByCategory(int categoryId) {
        String url = SUPABASE_URL + "articles?categories_id=eq." + categoryId;
        List<Articles> articlesList = new ArrayList<>();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Articles article = new Articles(
                            obj.getInt("id"),
                            obj.getString("title"),
                            obj.optString("publish_date"),
                            obj.optInt("views", 0),
                            obj.optString("abstract_content", ""),
                            obj.optString("content", ""),
                            obj.optInt("categories_id", 0),
                            obj.optInt("kinds_id", 0),
                            obj.optString("writer_id", ""),
                            obj.optInt("status_id", 0),
                            obj.optString("img")
                    );
                    articlesList.add(article);
                }
            } else {
                System.err.println("Fetch Articles API Error: " + response.code() + " - " + response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articlesList;
    }
    public List<Articles> findByAuthorEmail(String email) {
        List<Articles> articlesList = new ArrayList<>();
        String url = SUPABASE_URL + "articles?writer_id=eq." + email;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Articles article = new Articles(
                            obj.getInt("id"),
                            obj.getString("title"),
                            obj.optString("publish_date"),
                            obj.optInt("views", 0),
                            obj.optString("abstract_content", ""),
                            obj.optString("content", ""),
                            obj.optInt("categories_id", 0),
                            obj.optInt("kinds_id", 0),
                            obj.optString("writer_id", ""),
                            obj.optInt("status_id", 0),
                            obj.optString("img")
                    );
                    articlesList.add(article);
                }
            } else {
                System.err.println("API Error: " + response.code() + " - " + response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articlesList;
    }

    public void edit(int id, String title, String abstractContent, String content, int categoryId, int statusId) {
        String url = SUPABASE_URL + "articles?id=eq." + id;

        try {
            JSONObject body = new JSONObject();
            if (title != null) body.put("title", title);
            if (abstractContent != null) body.put("abstract_content", abstractContent);
            if (content != null) body.put("content", content);
            if (categoryId != -1) body.put("categories_id", categoryId);
            body.put("status_id", statusId);

            RequestBody requestBody = RequestBody.create(body.toString(), okhttp3.MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .patch(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void update(Articles article, String accessToken, UpdateCallback callback) {
        String url = SUPABASE_URL + "articles?id=eq." + article.getId();

        try {
            // Tạo JSON body cho yêu cầu PATCH
            JSONObject body = new JSONObject();
            body.put("title", article.getTitle());
            body.put("abstract_content", article.getAbstractContent());
            body.put("content", article.getContent());
            body.put("categories_id", article.getCategoriesId());
            body.put("img", article.getImg());

            // Tạo request body
            RequestBody requestBody = RequestBody.create(body.toString(), okhttp3.MediaType.get("application/json"));

            // Tạo request với header chứa access token
            Request request = new Request.Builder()
                    .url(url)
                    .patch(requestBody)
                    .header("Authorization", "Bearer " + accessToken) // Truyền access token
                    .header("apikey", SUPABASE_KEY)
                    .build();

            // Thực thi yêu cầu trong background thread
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    callback.onUpdateResult(false); // Trả về kết quả thất bại
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onUpdateResult(true); // Trả về kết quả thành công
                    } else {
                        System.err.println("Update failed: " + response.code() + " - " + response.message());
                        callback.onUpdateResult(false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onUpdateResult(false);
        }
    }





    // Other methods to implement as per the interface
}
