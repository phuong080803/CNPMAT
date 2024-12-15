package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.DAOiplm.ArticlesDAOImpl;
import com.example.cdcnpmat.R;
import com.example.cdcnpmat.adapters.ArticleAdminAdapter;
import com.example.cdcnpmat.adapters.ArticlesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {
    private ImageView homeIcon, profileIcon, authorArticle, settingIcon;
    private ListView lvArticles;
    private ArticleAdminAdapter adapter;
    private ArrayList<Articles> articlesList;
    private ArticlesDAOImpl articlesDAO;
    private TextView btnLogout, btnAddArticle;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Ánh xạ các thành phần giao diện
        lvArticles = findViewById(R.id.lvArticles);
        btnAddArticle = findViewById(R.id.btnAddArticle);

        articlesList = new ArrayList<>();
        adapter = new ArticleAdminAdapter(this, articlesList);
        lvArticles.setAdapter(adapter);
        articlesDAO = new ArticlesDAOImpl();
        profileIcon = findViewById(R.id.image_account_circle);
        homeIcon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingIcon = findViewById(R.id.setting);
        // Tải danh sách bài viết
        loadArticles();

        // Thêm bài viết mới
//        btnAddArticle.setOnClickListener(v -> {
//            Intent intent = new Intent(AdminActivity.this, AddArticleActivity.class);
//            startActivity(intent);
//        });
        profileIcon.setOnClickListener(v -> navigateToProfile());
        authorArticle.setOnClickListener(v -> navigateToAuthorArticles());
        homeIcon.setOnClickListener(v -> navigateToHomePage());
        settingIcon.setOnClickListener(v -> navigateToSettings());

    }

    private void loadArticles() {
        executor.execute(() -> {
            // Lấy tất cả bài viết từ cơ sở dữ liệu
            List<Articles> allArticles = articlesDAO.findAll();

            // Lọc bài viết có status_id = 30
            List<Articles> pendingArticles = new ArrayList<>();
            for (Articles article : allArticles) {
                if (article.getStatusId() == 30) {
                    pendingArticles.add(article);
                }
            }

            // Cập nhật giao diện
            runOnUiThread(() -> {
                articlesList.clear();
                articlesList.addAll(pendingArticles);
                adapter.notifyDataSetChanged();

                if (pendingArticles.isEmpty()) {
                    Toast.makeText(this, "Không có bài viết nào đang chờ duyệt.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void navigateToProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "unknow");
        if (isUserLoggedIn()) {
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(AdminActivity.this, ProfileAdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AdminActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(AdminActivity.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToAuthorArticles() {
        if (isUserLoggedIn()) {
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "unknow");
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(AdminActivity.this, AdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(AdminActivity.this, HomePageActivity.class);
                startActivity(intent);
            }

        } else {
            Intent intent = new Intent(AdminActivity.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(AdminActivity.this, HomePageActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(AdminActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }


}
