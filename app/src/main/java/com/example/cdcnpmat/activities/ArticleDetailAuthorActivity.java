package com.example.cdcnpmat.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cdcnpmat.Model.Bean.Comments;
import com.example.cdcnpmat.Model.DAOiplm.CommentDAOImpl;
import com.example.cdcnpmat.R;
import com.example.cdcnpmat.adapters.CommentAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArticleDetailAuthorActivity extends AppCompatActivity {
    private TextView articleTitle, authorName, articleTimestamp;
    private WebView articleContent;
    private ListView commentsListView;
    private EditText commentInput;
    private Button commentSubmitButton, editButton;
    private ImageView menuIcon, homeicon, profileicon, settingicon, authorArticle;

    private CommentDAOImpl commentDAO;
    private List<Comments> commentList;
    private CommentAdapter commentAdapter;


    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_author_edit);

        // Bind views
        menuIcon = findViewById(R.id.image_list);
        homeicon = findViewById(R.id.image_home);
        settingicon = findViewById(R.id.setting);
        profileicon = findViewById(R.id.image_account_circle);
        authorArticle = findViewById(R.id.image_list);
        articleTitle = findViewById(R.id.articleTitle);
        authorName = findViewById(R.id.authorName);
        articleTimestamp = findViewById(R.id.articleTimestamp);
        articleContent = findViewById(R.id.articleContent);
        commentsListView = findViewById(R.id.commentsListView);
        commentInput = findViewById(R.id.commentInput);
        commentSubmitButton = findViewById(R.id.commentSubmitButton);
        editButton = findViewById(R.id.editArticleButton);
        commentDAO = new CommentDAOImpl();
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        commentsListView.setAdapter(commentAdapter);

        // Receive data from Intent
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String timestamp = getIntent().getStringExtra("timestamp");
        String content = getIntent().getStringExtra("content");
        Integer articleId = getIntent().getIntExtra("article_id", -1);

        // Display data on UI
        articleTitle.setText(title);
        authorName.setText(author);
        articleTimestamp.setText(timestamp);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        // Configure WebView
        WebSettings webSettings = articleContent.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        String htmlHeader = "<html><head><meta charset=\"UTF-8\"><style>img{max-width:100%;height:auto;}</style></head><body style='font-size:30px;'>";
        String htmlFooter = "</body></html>";
        String fullContent = htmlHeader + content + htmlFooter;
        articleContent.loadData(fullContent, "text/html; charset=utf-8", "UTF-8");

        // Load comments for the article
        loadComments(articleId);

        // Handle comment submission
        commentSubmitButton.setOnClickListener(v -> addComment(articleId));
        editButton.setOnClickListener(v -> {
            if (articleId != -1) { // Ensure the articleId is valid
                Intent intent = new Intent(ArticleDetailAuthorActivity.this, ActivityEditArticle.class);
                intent.putExtra("article_id", articleId); // Pass the article ID to the edit screen
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to edit this article", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up navigation
        setUpNavigation();
    }

    private void setUpNavigation() {
        authorArticle.setOnClickListener(v -> navigateToAuthorArticles());
        profileicon.setOnClickListener(v -> navigateToProfile());
        homeicon.setOnClickListener(v -> navigateTo(HomePageActivity.class));
        settingicon.setOnClickListener(v -> navigateTo(SettingsActivity.class));
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(ArticleDetailAuthorActivity.this, targetActivity);
        startActivity(intent);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
    private void navigateToProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "unknow");
        if (isUserLoggedIn()) {
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(ArticleDetailAuthorActivity.this, ProfileAdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ArticleDetailAuthorActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ArticleDetailAuthorActivity.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }
    private void loadComments(Integer articleId) {
        executorService.execute(() -> {
            List<Comments> fetchedComments = commentDAO.findByArtId(articleId);

            mainHandler.post(() -> {
                commentList.clear();
                commentList.addAll(fetchedComments);
                commentAdapter.notifyDataSetChanged();
            });
        });
    }
    private void navigateToAuthorArticles() {
        if (isUserLoggedIn()) {
            SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "unknow");
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(ArticleDetailAuthorActivity.this, AdminActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ArticleDetailAuthorActivity.this, AuthorActivity.class);
                startActivity(intent);
            }

        } else {
            Intent intent = new Intent(ArticleDetailAuthorActivity.this, Non_Login_Activity.class);
            startActivity(intent);
        }
    }
    private void addComment(Integer articleId) {
        String commentText = commentInput.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Bình luận không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        String userId = sharedPreferences.getString("email", "");

        if (userId == "") {
            Toast.makeText(this, "Bạn phải đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                commentDAO.add(userId, articleId, commentText);
                mainHandler.post(() -> {
                    commentInput.setText("");
                    Toast.makeText(this, "Bình luận đã được thêm"+articleId+commentText, Toast.LENGTH_SHORT).show();
                    loadComments(articleId);
                });
            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(this, "Lỗi khi thêm bình luận", Toast.LENGTH_SHORT).show());
            }
        });
    }



}
