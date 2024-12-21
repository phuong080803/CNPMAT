package com.example.cdcnpmat.activities;
import android.content.SharedPreferences;
import android.os.Handler;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.Bean.Categories;
import com.example.cdcnpmat.Model.DAOiplm.ArticlesDAOImpl;
import com.example.cdcnpmat.Model.DAOiplm.CategoriesDAOImpl;
import com.example.cdcnpmat.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityEditArticle extends AppCompatActivity {

    private EditText title, abstractContent, content;
    private Spinner spinnerCategory;
    private LinearLayout buttonSave;
    private ImageView articleImageView;
    private Button btnChooseImage;

    private Uri selectedImageUri = null;
    private ArticlesDAOImpl articlesDAO;
    private CategoriesDAOImpl categoriesDAO;
    private Articles currentArticle;

    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);

        initializeViews();

        // Initialize image picker launcher
        pickVisualMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        articleImageView.setImageURI(selectedImageUri);
                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Initialize DAOs
        articlesDAO = new ArticlesDAOImpl();
        categoriesDAO = new CategoriesDAOImpl();
        int articleId = getIntent().getIntExtra("article_id", -1);

        if (articleId != -1) {
            loadArticleData(articleId); // Load article data for editing
        } else {
            Toast.makeText(this, "Invalid article ID", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no valid ID is found
        }
        // Set click listeners
        btnChooseImage.setOnClickListener(v -> openImagePicker());
        buttonSave.setOnClickListener(v -> updateArticle());
    }

    private void initializeViews() {
        title = findViewById(R.id.text_title_content1);
        abstractContent = findViewById(R.id.text_abstract_content1);
        content = findViewById(R.id.text_main_content);
        spinnerCategory = findViewById(R.id.spinner_category);
        articleImageView = findViewById(R.id.selectedImage);
        btnChooseImage = findViewById(R.id.btn_choose_image);
        buttonSave = findViewById(R.id.buttonSave);
    }

    private void openImagePicker() {
        pickVisualMediaLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }

    private void loadArticleData(int articleId) {
        if (articleId != -1) {
            new Thread(() -> {
                currentArticle = articlesDAO.findById(articleId);

                if (currentArticle != null) {
                    List<Categories> categories = categoriesDAO.findAll();

                    runOnUiThread(() -> {
                        // Populate article data
                        title.setText(currentArticle.getTitle());
                        abstractContent.setText(currentArticle.getAbstractContent());
                        content.setText(currentArticle.getContent());
                        if (currentArticle.getImg() != null) {
                            Glide.with(this)
                                    .load(Uri.parse(currentArticle.getImg()))
                                    .into(articleImageView);
                        }

                        // Populate spinner with categories
                        List<String> categoryNames = new ArrayList<>();
                        int selectedCategoryPosition = 0;
                        for (int i = 0; i < categories.size(); i++) {
                            categoryNames.add(categories.get(i).getNameCategory());
                            if (categories.get(i).getId() == currentArticle.getCategoriesId()) {
                                selectedCategoryPosition = i;
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                        spinnerCategory.setSelection(selectedCategoryPosition);
                    });
                }
            }).start();
        } else {
            Toast.makeText(this, "Unable to load article data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateArticle() {
        String updatedTitle = title.getText().toString().trim();
        String updatedAbstractContent = abstractContent.getText().toString().trim();
        String updatedContent = content.getText().toString().trim();
        String selectedCategory = (String) spinnerCategory.getSelectedItem();

        // Lấy access token từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", "");

        if (updatedTitle.isEmpty() || updatedAbstractContent.isEmpty() || updatedContent.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tìm categoryId và cập nhật bài viết
        new Thread(() -> {
            int categoryId = categoriesDAO.findByNameSync(selectedCategory);
            if (categoryId != -1) {
                // Set dữ liệu mới cho bài viết
                currentArticle.setTitle(updatedTitle);
                currentArticle.setAbstractContent(updatedAbstractContent);
                currentArticle.setContent(updatedContent);
                currentArticle.setCategoriesId(categoryId);

                if (selectedImageUri != null) {
                    currentArticle.setImg(selectedImageUri.toString());
                }

                // Gọi phương thức update từ ArticlesDAO
                articlesDAO.update(currentArticle, accessToken, success -> {
                    // Cập nhật giao diện trên Main Thread
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(ActivityEditArticle.this, "Cập nhật bài viết thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActivityEditArticle.this, "Lỗi khi cập nhật bài viết", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Danh mục không tồn tại", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


}
