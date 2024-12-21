package com.example.cdcnpmat.Model.DAOiplm;

import android.os.Handler;

import com.example.cdcnpmat.Model.Bean.Categories;
import com.example.cdcnpmat.Model.DAO.CategoriesDAO;
import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoriesDAOImpl implements CategoriesDAO {

    private static final String SUPABASE_URL = "https://nqgjdcjznjbqefgyoicd.supabase.co/rest/v1/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5xZ2pkY2p6bmpicWVmZ3lvaWNkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI2MzM2MTAsImV4cCI6MjA0ODIwOTYxMH0.quwMnNHNMUOQp3h92cdNkgk3y67Ufifiyut-MNDJBmQ";
    private final OkHttpClient client = new OkHttpClient();


    @Override
    public List<Categories> findAll() {
        String url = SUPABASE_URL + "categories";
        List<Categories> categories = new ArrayList<>();

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = response.body().string(); // Lưu vào biến tạm
                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String nameCategory = jsonObject.getString("name_category");
                    categories.add(new Categories(id, nameCategory));
                }
            } else {
                System.out.println("API Error: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }



    @Override
    public void addCate(String nameCate) throws JSONException {
        String url = SUPABASE_URL + "categories";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name_category", nameCate);

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Categories findById(int id) {
        String url = SUPABASE_URL + "categories?id=eq." + id;
        Categories category = null;

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONArray jsonArray = new JSONArray(jsonData);
                if (jsonArray.length() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int categoryId = jsonObject.getInt("id");
                    String nameCategory = jsonObject.getString("name_category");

                    category = new Categories(categoryId, nameCategory);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return category;
    }

    public void findByname(String categoryName, Handler handler) {
        new Thread(() -> {
            String url = SUPABASE_URL + "categories?name_category=eq." + categoryName;

            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonData);
                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int categoryId = jsonObject.getInt("id");

                        // Gửi kết quả về Handler
                        handler.sendMessage(handler.obtainMessage(1, categoryId));
                    } else {
                        handler.sendMessage(handler.obtainMessage(0, "Danh mục không tồn tại"));
                    }
                } else {
                    handler.sendMessage(handler.obtainMessage(0, "Lỗi API: " + response.code()));
                }
            } catch (IOException | JSONException e) {
                handler.sendMessage(handler.obtainMessage(0, e.getMessage()));
            }
        }).start();
    }

    @Override
    public int findByNameSync(String categoryName) {
        String url = SUPABASE_URL + "categories?name_category=eq." + categoryName;
        int categoryId = -1;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                JSONArray jsonArray = new JSONArray(response.body().string());
                if (jsonArray.length() > 0) {
                    categoryId = jsonArray.getJSONObject(0).getInt("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryId; // Trả về -1 nếu có lỗi
    }


    @Override
    public void updateCate(int idCate, String nameCate) throws JSONException {
        String url = SUPABASE_URL + "categories?id=eq." + idCate;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name_category", nameCate);


        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void deleteCate(int idCate) {
        String url = SUPABASE_URL + "categories?id=eq." + idCate;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Các phương thức khác tương tự như trên (addPCate, findAllByParentId, deletePCate, v.v.)
}
