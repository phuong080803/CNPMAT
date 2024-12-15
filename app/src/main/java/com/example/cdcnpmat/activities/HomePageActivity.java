package com.example.cdcnpmat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
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
import com.example.cdcnpmat.adapters.ArticlesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomePageActivity extends AppCompatActivity {
    private ListView lv_main;
    private ImageView authorArticle, searchIcon, filterIcon, homeicon, profileicon, settingicon;
    private EditText searchInput;
    private ArticlesAdapter adapter;
    private ArrayList<Articles> list;
    private LinearLayout categoryContainer;
    private CategoriesDAOImpl categoriesDAO;
    private ArticlesDAOImpl articlesDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Ánh xạ các thành phần giao diện
        lv_main = findViewById(R.id.postListView);
        searchIcon = findViewById(R.id.searchIcon);
        searchInput = findViewById(R.id.searchInput);
        categoryContainer = findViewById(R.id.categoryContainer);
        authorArticle = findViewById(R.id.image_list);
        homeicon = findViewById(R.id.image_home);
        settingicon = findViewById(R.id.setting);
        profileicon = findViewById(R.id.image_account_circle);

        list = new ArrayList<>();
        adapter = new ArticlesAdapter(this, list);
        lv_main.setAdapter(adapter);

        categoriesDAO = new CategoriesDAOImpl();
        articlesDAO = new ArticlesDAOImpl();

        // Handle clicks on ListView items
        
        homeicon.setOnClickListener(v->loadArticles());
        profileicon.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "unknow");
            if (isUserLoggedIn()) {
                if (role.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(HomePageActivity.this, ProfileAdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(HomePageActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        authorArticle.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
                String role = sharedPreferences.getString("role", "unknow");
                if (role.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(HomePageActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomePageActivity.this, AuthorActivity.class);
                    startActivity(intent);
                }

            } else {
                Intent intent = new Intent(HomePageActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        settingicon.setOnClickListener(v->{
            Intent intent = new Intent(HomePageActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                String query = searchInput.getText().toString().trim();
                searchArticles(query);
                return true; // Xử lý sự kiện đã hoàn tất
            }
            return false;
        });
        // Load categories and articles
        loadCategories();
        loadArticles();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
    private void loadCategories() {
        new Thread(() -> {
            List<Categories> categoriesList = categoriesDAO.findAll();
            runOnUiThread(() -> {
                categoryContainer.removeAllViews();
                if (categoriesList.isEmpty()) {
                    Toast.makeText(this, "Không có danh mục nào", Toast.LENGTH_SHORT).show();
                } else {
                    for (Categories category : categoriesList) {
                        TextView categoryView = new TextView(this);
                        categoryView.setText(category.getNameCategory());
                        categoryView.setPadding(16, 8, 16, 8);
                        categoryView.setBackgroundResource(R.drawable.category_item_background);
                        categoryView.setOnClickListener(v -> loadArticlesByCategory(category.getId()));
                        categoryContainer.addView(categoryView);
                    }
                }
            });
        }).start();
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private void loadArticles() {
        executor.execute(() -> {
            List<Articles> articlesList = articlesDAO.findAll();
            List<Articles> approvedArticles = new ArrayList<>();
            int maxArticleId = 0;

            for (Articles article : articlesList) {
                if (article.getStatusId() == 100) { // Lọc bài viết có trạng thái approved
                    approvedArticles.add(article);
                    if (article.getId() > maxArticleId) {
                        maxArticleId = article.getId();
                    }
                }
            }

            int finalMaxArticleId = maxArticleId;
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("max", finalMaxArticleId);
            editor.apply();

            runOnUiThread(() -> {
                if (approvedArticles.isEmpty()) {
                    Toast.makeText(this, "Không có bài viết nào", Toast.LENGTH_SHORT).show();
                } else {
                    list.clear();
                    list.addAll(approvedArticles);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "ID lớn nhất: " + finalMaxArticleId, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }



    private void loadArticlesByCategory(int categoryId) {
        new Thread(() -> {
            List<Articles> articlesList = articlesDAO.findByCategory(categoryId);
            List<Articles> approvedArticles = new ArrayList<>();

            for (Articles article : articlesList) {
                if (article.getStatusId() == 100) { // Lọc bài viết có trạng thái approved
                    approvedArticles.add(article);
                }
            }

            runOnUiThread(() -> {
                if (approvedArticles.isEmpty()) {
                    Toast.makeText(this, "Không có bài viết nào trong danh mục này", Toast.LENGTH_SHORT).show();
                } else {
                    list.clear();
                    list.addAll(approvedArticles);
                    adapter.notifyDataSetChanged();
                }
            });
        }).start();
    }
    private void searchArticles(String query) {
        if (query == null || query.trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập từ khóa để tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            // Lấy tất cả bài viết từ cơ sở dữ liệu
            List<Articles> articlesList = articlesDAO.findAll();
            List<Articles> matchingArticles = new ArrayList<>();

            for (Articles article : articlesList) {
                if (article.getStatusId() == 100) { // Chỉ tìm kiếm trong các bài đã được duyệt
                    String title = article.getTitle().toLowerCase();
                    String lowerQuery = query.toLowerCase();
                    if (title.contains(lowerQuery)) { // Kiểm tra xem tiêu đề có chứa từ khóa không
                        matchingArticles.add(article);
                    }
                }
            }

            runOnUiThread(() -> {
                if (matchingArticles.isEmpty()) {
                    Toast.makeText(this, "Không tìm thấy bài viết nào phù hợp", Toast.LENGTH_SHORT).show();
                } else {
                    list.clear();
                    list.addAll(matchingArticles);
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }
}
