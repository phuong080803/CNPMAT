package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.cdcnpmat.Model.Bean.Users;

import com.example.cdcnpmat.Model.DAOiplm.UsersDAOImpl;
import com.example.cdcnpmat.R;
import com.example.cdcnpmat.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileAdminActivity extends AppCompatActivity {
    private ImageView homeIcon, profileIcon, authorArticle, settingIcon;
    private ListView userListView;
    private UserAdapter userAdapter;
    private List<Users> userList;
    private UsersDAOImpl userDAO;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        userListView = findViewById(R.id.userListView);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        userListView.setAdapter(userAdapter);
        profileIcon = findViewById(R.id.image_account_circle);
        homeIcon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingIcon = findViewById(R.id.setting);
        userDAO = new UsersDAOImpl();

        loadUsers();

//        userListView.setOnItemClickListener((parent, view, position, id) -> {
//            Users selectedUser = userList.get(position);
//            Intent intent = new Intent(ProfileAdminActivity.this, UserDetailsActivity.class);
//            intent.putExtra("userId", selectedUser.getId());
//            startActivity(intent);
//        });
        profileIcon.setOnClickListener(v -> navigateToProfile());
        authorArticle.setOnClickListener(v -> navigateToAuthorArticles());
        homeIcon.setOnClickListener(v -> navigateToHomePage());
        settingIcon.setOnClickListener(v -> navigateToSettings());

    }

    private void loadUsers() {
        executor.execute(() -> {
            List<Users> fetchedUsers = userDAO.findAll();
            runOnUiThread(() -> {
                if (fetchedUsers.isEmpty()) {
                    Toast.makeText(this, "Không có người dùng nào.", Toast.LENGTH_SHORT).show();
                } else {
                    userList.clear();
                    userList.addAll(fetchedUsers);
                    userAdapter.notifyDataSetChanged();
                }
            });
        });
    }private void navigateToProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "unknow");
        if (isUserLoggedIn()) {
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(ProfileAdminActivity.this, ProfileAdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ProfileAdminActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ProfileAdminActivity.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToAuthorArticles() {
        if (isUserLoggedIn()) {
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "unknow");
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(ProfileAdminActivity.this, AdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ProfileAdminActivity.this, HomePageActivity.class);
                startActivity(intent);
            }

        } else {
            Intent intent = new Intent(ProfileAdminActivity.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(ProfileAdminActivity.this, HomePageActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(ProfileAdminActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
}
