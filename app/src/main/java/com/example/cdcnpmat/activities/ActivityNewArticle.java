package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.Bean.Categories;
import com.example.cdcnpmat.Model.DAO.ArticlesDAO;
import com.example.cdcnpmat.Model.DAOiplm.ArticlesDAOImpl;
import com.example.cdcnpmat.Model.DAOiplm.CategoriesDAOImpl;
import com.example.cdcnpmat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityNewArticle extends AppCompatActivity {
    private ImageView homeIcon, profileIcon, authorArticle, settingIcon;
    private EditText title, abstractContent, content;
    private Spinner spinnerCategory;
    private LinearLayout btnSubmit;
    private CategoriesDAOImpl categoriesDAO;
    private ArticlesDAOImpl articlesDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_new_article);

        categoriesDAO = new CategoriesDAOImpl();
        spinnerCategory = findViewById(R.id.spinner_category);

        // Initialize views
        profileIcon = findViewById(R.id.image_account_circle);
        homeIcon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingIcon = findViewById(R.id.setting);
        btnSubmit = findViewById(R.id.container_button1);
        title = findViewById(R.id.text_title_content1);
        abstractContent = findViewById(R.id.text_abstract_content1);
        content = findViewById(R.id.text_main_content);
        articlesDAO = new ArticlesDAOImpl();
        // Load categories
        loadCategories();

        // Navigation button functionalities
        profileIcon.setOnClickListener(v -> navigateToProfile());
        authorArticle.setOnClickListener(v -> navigateToAuthorArticles());
        homeIcon.setOnClickListener(v -> navigateToHomePage());
        settingIcon.setOnClickListener(v -> navigateToSettings());
        btnSubmit.setOnClickListener(v -> addArticle());
    }

    private void loadCategories() {
        new Thread(() -> {
            try {
                // Use the CategoriesDAOImpl to fetch categories
                List<Categories> categories = categoriesDAO.findAll();
                List<String> categoryNames = new ArrayList<>();
                for (Categories category : categories) {
                    categoryNames.add(category.getNameCategory());
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(ActivityNewArticle.this, "Failed to load categories", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void navigateToProfile() {
        if (isUserLoggedIn()) {
            Intent intent = new Intent(ActivityNewArticle.this, ProfileActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ActivityNewArticle.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToAuthorArticles() {
        if (isUserLoggedIn()) {
            Intent intent = new Intent(ActivityNewArticle.this, AuthorActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ActivityNewArticle.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(ActivityNewArticle.this, HomePageActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(ActivityNewArticle.this, SettingsActivity.class);
        startActivity(intent);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
    private void addArticle() {
        String articleTitle = title.getText().toString().trim();
        String articleAbstractContent = abstractContent.getText().toString().trim();
        String articleContent = content.getText().toString().trim();
        String selectedCategory = (String) spinnerCategory.getSelectedItem();

        if (articleTitle.isEmpty() || articleAbstractContent.isEmpty() || articleContent.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo Handler để xử lý kết quả từ luồng phụ
        Handler handler = new Handler(msg -> {
            if (msg.what == 1) { // Thành công
                int categoryId = (int) msg.obj;

                // Lấy thông tin từ SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
                String writerId = sharedPreferences.getString("email", "");
                int newId = sharedPreferences.getInt("max",0);
                String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
                Toast.makeText(this, "ID lớn nhất aa: " + newId+1, Toast.LENGTH_SHORT).show();
                // Tạo bài viết
                Articles newArticle = new Articles(newId+1,articleTitle,currentTimestamp, 1,articleAbstractContent,articleContent,categoryId,1,writerId,1);



                // Thêm bài viết vào cơ sở dữ liệu
                ArticlesDAO articlesDAO = new ArticlesDAOImpl();
                articlesDAO.add(newArticle);

                // Hiển thị thông báo thành công trên luồng chính
                Toast.makeText(ActivityNewArticle.this, "Thêm bài viết thành công!", Toast.LENGTH_SHORT).show();

                // Xóa dữ liệu trong các trường
                title.setText("");
                abstractContent.setText("");
                content.setText("");

            } else { // Lỗi
                String errorMessage = (String) msg.obj;
                Toast.makeText(ActivityNewArticle.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // Gọi phương thức findByname với Handler
        categoriesDAO.findByname(selectedCategory, handler);
    }


    // Method to fetch the new article ID





}
