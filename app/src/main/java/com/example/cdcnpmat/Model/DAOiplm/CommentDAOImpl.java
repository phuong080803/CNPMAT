package com.example.cdcnpmat.Model.DAOiplm;

import android.os.Build;

import com.example.cdcnpmat.Model.Bean.Comments;
import com.example.cdcnpmat.Model.DAO.CommentDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDAOImpl implements CommentDAO {

    private static final String SUPABASE_URL = "https://<your-project-ref>.supabase.co/rest/v1/";
    private static final String SUPABASE_KEY = "<your-supabase-key>";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void add(int user_id, int article_id, String content) throws JSONException {
        String url = SUPABASE_URL + "comments";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", user_id);
        jsonObject.put("article_id", article_id);
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
