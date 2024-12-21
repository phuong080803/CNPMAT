package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.Bean.Categories;
import com.example.cdcnpmat.Model.DAO.ArticlesDAO;
import com.example.cdcnpmat.Model.DAOiplm.ArticlesDAOImpl;
import com.example.cdcnpmat.Model.DAOiplm.CategoriesDAOImpl;
import com.example.cdcnpmat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityNewArticle extends AppCompatActivity {
    private ImageView homeIcon, profileIcon, authorArticle, settingIcon;
    private EditText title, abstractContent, content;
    private Spinner spinnerCategory;
    private LinearLayout btnSubmit;
    private ImageView articleImageView;
    private Button btnChooseImage;

    private Uri selectedImageUri = null;
    private CategoriesDAOImpl categoriesDAO;
    private ArticlesDAOImpl articlesDAO;

    // ActivityResultLauncher để chọn hình ảnh
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_new_article);

        // Ánh xạ các thành phần giao diện
        initializeViews();

        // Đăng ký ActivityResultLauncher với PickVisualMedia
        pickVisualMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        articleImageView.setImageURI(selectedImageUri); // Hiển thị ảnh đã chọn
                    } else {
                        Toast.makeText(this, "Không có hình ảnh được chọn", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Xử lý sự kiện
        btnChooseImage.setOnClickListener(v -> openImagePicker());
        btnSubmit.setOnClickListener(v -> addArticle());
        profileIcon.setOnClickListener(v -> navigateToProfile());
        homeIcon.setOnClickListener(v -> navigateToHomePage());
        authorArticle.setOnClickListener(v -> navigateToAuthorArticles());
        settingIcon.setOnClickListener(v -> navigateToSettings());

        // Khởi tạo DAO và load danh mục
        categoriesDAO = new CategoriesDAOImpl();
        articlesDAO = new ArticlesDAOImpl();
        loadCategories();
    }

    // Ánh xạ các thành phần giao diện
    private void initializeViews() {
        spinnerCategory = findViewById(R.id.spinner_category);
        profileIcon = findViewById(R.id.image_account_circle);
        homeIcon = findViewById(R.id.image_home);
        authorArticle = findViewById(R.id.image_list);
        settingIcon = findViewById(R.id.setting);
        btnSubmit = findViewById(R.id.container_button1);
        title = findViewById(R.id.text_title_content1);
        abstractContent = findViewById(R.id.text_abstract_content1);
        content = findViewById(R.id.text_main_content);
        articleImageView = findViewById(R.id.selectedImage);
        btnChooseImage = findViewById(R.id.btn_choose_image);
    }

    // Mở bộ chọn hình ảnh
    private void openImagePicker() {
        pickVisualMediaLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE) // Chỉ chọn hình ảnh
                        .build()
        );
    }

    // Load danh sách danh mục vào Spinner
    private void loadCategories() {
        new Thread(() -> {
            try {
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
                runOnUiThread(() -> Toast.makeText(this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Thêm bài viết mới
    private void addArticle() {
        String articleTitle = title.getText().toString().trim();
        String articleAbstractContent = abstractContent.getText().toString().trim();
        String articleContent = content.getText().toString().trim();
        String selectedCategory = (String) spinnerCategory.getSelectedItem();

        if (articleTitle.isEmpty() || articleAbstractContent.isEmpty() || articleContent.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Handler handler = new Handler(msg -> {
            if (msg.what == 1) {
                int categoryId = (int) msg.obj;
                SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
                String writerId = sharedPreferences.getString("email", "");
                int newId = sharedPreferences.getInt("max", 0);
                String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());

                Articles newArticle = new Articles(
                        newId + 1, articleTitle, currentTimestamp, 1, articleAbstractContent,
                        articleContent, categoryId, 1, writerId, 30,
                        selectedImageUri != null ? selectedImageUri.toString() : null
                );

                articlesDAO.add(newArticle);
                Toast.makeText(this, "Thêm bài viết thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String errorMessage = (String) msg.obj;
                Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        categoriesDAO.findByname(selectedCategory, handler);
    }private void navigateToProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "unknow");
        if (isUserLoggedIn()) {
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(ActivityNewArticle.this, ProfileAdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ActivityNewArticle.this, ProfileActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ActivityNewArticle.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToAuthorArticles() {
        if (isUserLoggedIn()) {
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "unknow");
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(ActivityNewArticle.this, AdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ActivityNewArticle.this, HomePageActivity.class);
                startActivity(intent);
            }

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

}
