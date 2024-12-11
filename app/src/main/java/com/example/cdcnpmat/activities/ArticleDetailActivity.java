package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.R;

public class ArticleDetailActivity extends AppCompatActivity {
    private TextView articleTitle, authorName, articleTimestamp;
    private WebView articleContent;
    private ImageView articleImage, backbtn;
    private ImageView menuIcon, searchIcon, filterIcon, homeicon, profileicon, settingicon,authorArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

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



        authorArticle.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                // Nếu đã đăng nhập, chuyển đến ProfileActivity
                Intent intent = new Intent(ArticleDetailActivity.this, AuthorActivity.class);
                startActivity(intent);
            } else {
                // Nếu chưa đăng nhập, chuyển đến Non_Login_Activity
                Intent intent = new Intent(ArticleDetailActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        // Chuyển đến Profile hoặc Non-Login Activity dựa trên trạng thái đăng nhập
        profileicon.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                Intent intent = new Intent(ArticleDetailActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ArticleDetailActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });

        // Nhận dữ liệu từ Intent
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String timestamp = getIntent().getStringExtra("timestamp");
        String content = getIntent().getStringExtra("content");


        // Chuyển về HomePage khi nhấn HomeIcon
        homeicon.setOnClickListener(v -> {
            Intent intent = new Intent(ArticleDetailActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
        settingicon.setOnClickListener(v->{
            Intent intent = new Intent(ArticleDetailActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        // Hiển thị dữ liệu lên giao diện
        articleTitle.setText(title);
        authorName.setText(author);
        articleTimestamp.setText(timestamp);

        // Cấu hình và hiển thị nội dung HTML trong WebView
        WebSettings webSettings = articleContent.getSettings();
        webSettings.setJavaScriptEnabled(true); // Kích hoạt JavaScript nếu cần
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setUseWideViewPort(true);

        // Nội dung HTML được gán vào WebView
        String htmlHeader = "<html><head><meta charset=\"UTF-8\"><style>img{max-width:100%;height:auto;}</style></head><body style='font-size:30px;'>";
        String htmlFooter = "</body></html>";
        String fullContent = htmlHeader + content + htmlFooter;

        articleContent.loadData(fullContent, "text/html; charset=utf-8", "UTF-8");

        // Hiển thị hình ảnh
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
}
