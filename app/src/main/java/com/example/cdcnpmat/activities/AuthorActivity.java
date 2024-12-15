package com.example.cdcnpmat.activities;

import static com.example.cdcnpmat.R.id.container_dang_bai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.Bean.Categories;
import com.example.cdcnpmat.Model.DAOiplm.ArticlesDAOImpl;
import com.example.cdcnpmat.Model.DAOiplm.CategoriesDAOImpl;
import com.example.cdcnpmat.R;
import com.example.cdcnpmat.adapters.ArticleAuthorAdapter;
import com.example.cdcnpmat.adapters.ArticlesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthorActivity extends AppCompatActivity {
    private ListView lv_main;
    private ImageView menuIcon, searchIcon, filterIcon, homeicon, profileicon, settingicon, postArticle, authorArticle;
    private TextView authorName, authorEmail;
    private ArticleAuthorAdapter adapter;
    private ArrayList<Articles> list;
    private LinearLayout categoryContainer;
    private CategoriesDAOImpl categoriesDAO;
    private ArticlesDAOImpl articlesDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_article);

        // Ánh xạ các thành phần giao diện
        lv_main = findViewById(R.id.postListView);
        authorEmail = findViewById(R.id.Email);
        authorName = findViewById(R.id.authorName);
        menuIcon = findViewById(R.id.image_list);
        homeicon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingicon = findViewById(R.id.setting);
        postArticle = findViewById(container_dang_bai);
        postArticle.setOnClickListener(v -> {
            Intent intent = new Intent(AuthorActivity.this, ActivityNewArticle.class);
            startActivity(intent);
        });
        profileicon = findViewById(R.id.image_account_circle);
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String email2 =  sharedPreferences.getString("email","");
        String name = sharedPreferences.getString("name", "Unknown");
        profileicon.setOnClickListener(v -> {

            String role = sharedPreferences.getString("role", "unknow");
            if (isUserLoggedIn()) {
                if (role.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(AuthorActivity.this, ProfileAdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AuthorActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(AuthorActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        list = new ArrayList<>();
        adapter = new ArticleAuthorAdapter(this, list);
        lv_main.setAdapter(adapter);

        categoriesDAO = new CategoriesDAOImpl();
        articlesDAO = new ArticlesDAOImpl();

        // Handle clicks on ListView items
        lv_main.setOnItemClickListener((parent, view, position, id) -> {
            String email = getIntent().getStringExtra("authorEmail");
            Articles selectedArticle = list.get(position);
            Intent intent = new Intent(AuthorActivity.this, ArticleDetailAuthorActivity.class);
            String authorId = Long.toString(id);
            // Pass article details to the ArticleDetailActivity
            intent.putExtra("title", selectedArticle.getTitle());
            intent.putExtra("author", selectedArticle.getWriterId()); // Assuming writerId is the author
            intent.putExtra("timestamp", selectedArticle.getPublishDate());
            intent.putExtra("content", selectedArticle.getContent());

            startActivity(intent);
        });
        homeicon.setOnClickListener(v->{
            Intent intent = new Intent(AuthorActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
        settingicon.setOnClickListener(v->{
            Intent intent = new Intent(AuthorActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        authorArticle.setOnClickListener(v -> {
            if (isUserLoggedIn()) {

                String role = sharedPreferences.getString("role", "unknow");
                if (role.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(AuthorActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AuthorActivity.this, HomePageActivity.class);
                    startActivity(intent);
                }

            } else {
                Intent intent = new Intent(AuthorActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        AuthorArticle(email2);
        authorName.setText(name);
        authorEmail.setText("@"+email2);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private void loadArticles() {
        executor.execute(() -> {
            List<Articles> articlesList = articlesDAO.findAll();
            runOnUiThread(() -> {
                if (articlesList.isEmpty()) {
                    Toast.makeText(this, "Không có bài viết nào", Toast.LENGTH_SHORT).show();
                } else {
                    list.clear();
                    list.addAll(articlesList);
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }

    private void logoutUser() {
        // Xóa token khỏi SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Xóa tất cả các dữ liệu trong SharedPreferences
        editor.apply();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(AuthorActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack để không quay lại
        startActivity(intent);

        // Hiển thị thông báo
        finish();
    }
    private void loadArticlesByCategory(int categoryId) {
        new Thread(() -> {
            List<Articles> articlesList = articlesDAO.findByCategory(categoryId);
            runOnUiThread(() -> {
                if (articlesList.isEmpty()) {
                    Toast.makeText(this, "Không có bài viết nào trong danh mục này", Toast.LENGTH_SHORT).show();
                } else {
                    list.clear();
                    list.addAll(articlesList);
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }
    private void AuthorArticle(String email){
        new Thread(()->{
            List<Articles> articlesList = articlesDAO.findByAuthorEmail(email);
            runOnUiThread(()->{
                if (articlesList.isEmpty()) {
                    Toast.makeText(this, "Không có bài viết nào trong danh mục này", Toast.LENGTH_SHORT).show();
                } else {
                    list.clear();
                    list.addAll(articlesList);
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }

}
