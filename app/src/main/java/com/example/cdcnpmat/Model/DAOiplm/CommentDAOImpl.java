package com.example.cdcnpmat.Model.DAOiplm;

import android.os.Build;

import com.example.cdcnpmat.Model.Bean.Comments;
import com.example.cdcnpmat.Model.DAO.CommentDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentDAOImpl implements CommentDAO {

    private static final String SUPABASE_URL = "https://nqgjdcjznjbqefgyoicd.supabase.co/rest/v1/";
    private static  String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5xZ2pkY2p6bmpicWVmZ3lvaWNkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI2MzM2MTAsImV4cCI6MjA0ODIwOTYxMH0.quwMnNHNMUOQp3h92cdNkgk3y67Ufifiyut-MNDJBmQ";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void add(String user_id, int article_id, String content) throws JSONException {
        String url = SUPABASE_URL + "comments";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", user_id);
        jsonObject.put("article_id", article_id);
        jsonObject.put("comment", content);
        String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
        jsonObject.put("date", currentTimestamp);
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
    public void updateComment(int id, String content) throws JSONException {
        String url = SUPABASE_URL + "comments?id=eq." + id;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comment", content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            jsonObject.put("date", LocalDateTime.now().toString());
        }

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
    public Comments findById(int id) {
        String url = SUPABASE_URL + "comments?id=eq." + id;
        Comments comment = null;

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
                    int commentId = jsonObject.getInt("id");
                    int articleId = jsonObject.getInt("article_id");
                    String content = jsonObject.getString("comment");
                    String date = jsonObject.getString("date");
                    String userId = jsonObject.getString("user_id");

                    comment = new Comments(commentId,userId, articleId, content, date );
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return comment;
    }

    @Override
    public List<Comments> findByArtId(int artId) {
        String url = SUPABASE_URL + "comments?article_id=eq." + artId;
        List<Comments> comments = new ArrayList<>();

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                JSONArray jsonArray = new JSONArray(jsonData);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String comment = jsonObject.getString("comment");
                    String date = jsonObject.getString("date");
                    String userId = jsonObject.getString("user_id");

                    comments.add(new Comments(id,userId, artId, comment, date));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return comments;
    }

    @Override
    public void delete(int commentId) {
        String url = SUPABASE_URL + "comments?id=eq." + commentId;

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
}
