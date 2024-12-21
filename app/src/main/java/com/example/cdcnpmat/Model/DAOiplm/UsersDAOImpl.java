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
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5xZ2pkY2p6bmpicWVmZ3lvaWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTczMjYzMzYxMCwiZXhwIjoyMDQ4MjA5NjEwfQ.Jg1umrIaOic8pIMCJ_NnreWacTchydC9H8gMSzfZ6t0"; // Use the service role key
    private static final String AUTH_ENDPOINT = "/auth/v1/admin/users";
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
    public Users findById(String id) {
        String url = SUPABASE_URL + AUTH_ENDPOINT + "?id=eq." + id; // API endpoint với filter ID

        Request request = new Request.Builder()
                .url(url)
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                // Chuyển đổi JSON response thành JSONObject
                JSONObject jsonObject = new JSONObject(responseBody);

                // Truy cập mảng "users" từ JSONObject
                JSONArray usersArray = jsonObject.getJSONArray("users");

                if (usersArray.length() > 0) {
                    // Lấy user đầu tiên từ mảng
                    JSONObject userObject = usersArray.getJSONObject(0);
                    JSONObject userMetadata = userObject.getJSONObject("user_metadata");

                    // Tạo đối tượng Users và trả về
                    return new Users(
                            userObject.getString("id"),
                            userMetadata.getString("password"),
                            userMetadata.getString("role"),
                            userMetadata.getString("email"),
                            userMetadata.getString("name"),
                            userMetadata.getString("phone_number")
                    );
                } else {
                    Log.e("UsersDAOImpl", "No user found with ID: " + id);
                }
            } else {
                Log.e("UsersDAOImpl", "Failed to fetch user by ID: " + response.message());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy user
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
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONArray usersArray = jsonObject.getJSONArray("users");

                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userObject = usersArray.getJSONObject(i);
                    JSONObject userMetadata = userObject.getJSONObject("user_metadata");

                    // Lấy role của user
                    String role = userMetadata.getString("role");

                    // Loại bỏ user có role là admin
                    if (!"admin".equalsIgnoreCase(role)) {
                        Users user = new Users(
                                userObject.getString("id"),
                                userMetadata.getString("password"),
                                role,
                                userMetadata.getString("email"),
                                userMetadata.getString("name"),
                                userMetadata.getString("phone_number")
                        );

                        usersList.add(user);
                    }
                }
            } else {
                Log.e("UsersDAOImpl", "Failed to fetch users: " + response.message());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
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
    public List<Users> findAllByRole(String role) {
        throw new UnsupportedOperationException("Finding users by role is not supported in Supabase Auth.");
    }

    @Override
    public Users findByEmail(String email) {
        throw new UnsupportedOperationException("Finding users by email is not supported in Supabase Auth.");
    }

    @Override
    public void updateProfile(String id, String fullName, String phone) {
        String url = SUPABASE_URL + AUTH_ENDPOINT + "/" + id;

        try {
            JSONObject metadata = new JSONObject();
            metadata.put("name", fullName);
            metadata.put("phone_number", phone);

            JSONObject payload = new JSONObject();
            payload.put("user_metadata", metadata);

            RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .header("apikey", SUPABASE_KEY)
                    .header("Authorization", "Bearer " + SUPABASE_KEY)
                    .put(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.d("UsersDAOImpl", "Cập nhật thông tin thành công");
                } else {
                    Log.e("UsersDAOImpl", "Cập nhật thông tin thất bại: " + response.message());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void changePassword(String id, String password) {
        throw new UnsupportedOperationException("Changing passwords is not supported in Supabase Auth.");
    }
}
