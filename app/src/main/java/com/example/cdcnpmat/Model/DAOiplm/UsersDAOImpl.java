package com.example.cdcnpmat.Model.DAOiplm;

import android.util.Log;

import com.example.cdcnpmat.Model.Bean.Users;
import com.example.cdcnpmat.Model.DAO.UsersDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class UsersDAOImpl implements UsersDAO {

    private static final String SUPABASE_URL = "https://nqgjdcjznjbqefgyoicd.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5xZ2pkY2p6bmpicWVmZ3lvaWNkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI2MzM2MTAsImV4cCI6MjA0ODIwOTYxMH0.quwMnNHNMUOQp3h92cdNkgk3y67Ufifiyut-MNDJBmQ"; // Use the service role key
    private static final String AUTH_ENDPOINT = "auth/v1/admin/users";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public int add(Users user) {
        String url = SUPABASE_URL + AUTH_ENDPOINT;

        JSONObject json = new JSONObject();
        try {
            json.put("email", user.getEmail());
            json.put("password", user.getPassword());
            json.put("email_confirm", true); // Automatically confirm the email
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Log.d("UsersDAOImpl", "User added successfully");
                return 1;
            } else {
                Log.e("UsersDAOImpl", "Failed to add user: " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Users findById(int id) {
        throw new UnsupportedOperationException("Finding users by ID is not supported in Supabase Auth.");
    }

    @Override
    public void delete(Users user) {
        String url = SUPABASE_URL + AUTH_ENDPOINT + "/" + user.getId();

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Log.d("UsersDAOImpl", "User deleted successfully");
            } else {
                Log.e("UsersDAOImpl", "Failed to delete user: " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Users> findAll() {
        String url = SUPABASE_URL + AUTH_ENDPOINT;

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        List<Users> usersList = new ArrayList<>();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONArray array = new JSONArray(response.body().string());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    usersList.add(new Users(
                            json.getString("id"),
                            json.optString("user_metadata.password", null),
                            json.optString("user_metadata.role", null),
                            json.optString("user_metadata.email", null)
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return usersList;
    }

    @Override
    public void assignCategories(int editor_id, int[] catesId) {
        throw new UnsupportedOperationException("Assigning categories is not supported in Supabase Auth.");
    }

    @Override
    public void deleteEditorCategories(int editor_id) {
        throw new UnsupportedOperationException("Deleting editor categories is not supported in Supabase Auth.");
    }

    @Override
    public Users findByUsername(String username) {
        throw new UnsupportedOperationException("Finding users by username is not supported in Supabase Auth.");
    }

    @Override
    public List<Users> findAllByRole(int role) {
        throw new UnsupportedOperationException("Finding users by role is not supported in Supabase Auth.");
    }

    @Override
    public Users findByEmail(String email) {
        throw new UnsupportedOperationException("Finding users by email is not supported in Supabase Auth.");
    }

    @Override
    public void updateProfile(int id, String fullName, int role, String email) {
        throw new UnsupportedOperationException("Updating profiles is not supported in Supabase Auth.");
    }

    @Override
    public void changePassword(int id, String password) {
        throw new UnsupportedOperationException("Changing passwords is not supported in Supabase Auth.");
    }
}
