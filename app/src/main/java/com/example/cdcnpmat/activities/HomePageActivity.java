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
        authorArticle = findViewById(R.id.menuIcon);
        searchIcon = findViewById(R.id.searchIcon);
        filterIcon = findViewById(R.id.filterIcon);
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
        lv_main.setOnItemClickListener((parent, view, position, id) -> {

            Articles selectedArticle = list.get(position);
            Intent intent = new Intent(HomePageActivity.this, ArticleDetailActivity.class);
            String authorId = Long.toString(id);
            // Pass article details to the ArticleDetailActivity
            intent.putExtra("title", selectedArticle.getTitle());
            intent.putExtra("author", selectedArticle.getWriterId()); // Assuming writerId is the author
            intent.putExtra("timestamp", selectedArticle.getPublishDate());
            intent.putExtra("content", selectedArticle.getContent());
            intent.putExtra("imageResId", R.drawable.ic_sample_image); // Replace with actual image logic if available

            startActivity(intent);
        });
        homeicon.setOnClickListener(v->loadArticles());
        profileicon.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                // Nếu đã đăng nhập, chuyển đến ProfileActivity
                Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else {
                // Nếu chưa đăng nhập, chuyển đến Non_Login_Activity
                Intent intent = new Intent(HomePageActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        authorArticle.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                // Nếu đã đăng nhập, chuyển đến ProfileActivity
                Intent intent = new Intent(HomePageActivity.this, AuthorActivity.class);
                startActivity(intent);
            } else {
                // Nếu chưa đăng nhập, chuyển đến Non_Login_Activity
                Intent intent = new Intent(HomePageActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        settingicon.setOnClickListener(v->{
            Intent intent = new Intent(HomePageActivity.this, SettingsActivity.class);
            startActivity(intent);
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
            int maxArticleId = 0;
            for (Articles article : articlesList) {
                if (article.getId() > maxArticleId) {
                    maxArticleId = article.getId();
                }
            }

            int finalMaxArticleId = maxArticleId;
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("max",finalMaxArticleId);
            runOnUiThread(() -> {
                if (articlesList.isEmpty()) {
                    Toast.makeText(this, "Không có bài viết nào", Toast.LENGTH_SHORT).show();
                } else {
                    list.clear();
                    list.addAll(articlesList);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "ID lớn nhất: " + finalMaxArticleId, Toast.LENGTH_SHORT).show();
                }
            });
        });
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
}
