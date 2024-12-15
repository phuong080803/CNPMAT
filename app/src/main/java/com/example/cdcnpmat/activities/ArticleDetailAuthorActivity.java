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

public class ArticleDetailAuthorActivity extends AppCompatActivity {
    private TextView articleTitle, authorName, articleTimestamp;
    private ImageView articleImage, backbtn;
    private ImageView menuIcon, searchIcon, filterIcon, homeicon, profileicon, settingicon, authorArticle;
    private WebView articleContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_author_edit);
        menuIcon = findViewById(R.id.image_list);
        homeicon = findViewById(R.id.image_home);
        settingicon = findViewById(R.id.setting);
        authorArticle = findViewById(R.id.image_list);
        profileicon = findViewById(R.id.image_account_circle);
        articleContent = findViewById(R.id.articleContent);
        // Bind views
        articleTitle = findViewById(R.id.articleTitle);
        authorName = findViewById(R.id.authorName);
        articleTimestamp = findViewById(R.id.articleTimestamp);
        backbtn.setOnClickListener(v->{
            Intent intent = new Intent(ArticleDetailAuthorActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
        profileicon.setOnClickListener(v->{
            Intent intent = new Intent(ArticleDetailAuthorActivity.this,Non_Login_Activity.class);
            startActivity(intent);
        });
        authorArticle.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
                String role = sharedPreferences.getString("role", "unknow");
                if (role.equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(ArticleDetailAuthorActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ArticleDetailAuthorActivity.this, HomePageActivity.class);
                    startActivity(intent);
                }

            } else {
                Intent intent = new Intent(ArticleDetailAuthorActivity.this, Non_Login_Activity.class);
                startActivity(intent);
            }
        });
        // Get data from intent
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String timestamp = getIntent().getStringExtra("timestamp");
        String content = getIntent().getStringExtra("content");

        homeicon.setOnClickListener(v->{
            Intent intent = new Intent(ArticleDetailAuthorActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
        settingicon.setOnClickListener(v->{
            Intent intent = new Intent(ArticleDetailAuthorActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        // Populate views
        articleTitle.setText(title);
        authorName.setText(author);
        articleTimestamp.setText(timestamp);
        WebSettings webSettings = articleContent.getSettings();
        webSettings.setJavaScriptEnabled(true); // Kích hoạt JavaScript nếu cần
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setUseWideViewPort(true);

        // Nội dung HTML được gán vào WebView
        String htmlHeader = "<html><head><meta charset=\"UTF-8\"><style>img{max-width:100%;height:auto;}</style></head><body style='font-size:30px;'>";
        String htmlFooter = "</body></html>";
        String fullContent = htmlHeader + content + htmlFooter;

        articleContent.loadData(fullContent, "text/html; charset=utf-8", "UTF-8");


    }
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("supabase_auth", MODE_PRIVATE);
        return sharedPreferences.contains("access_token");
    }
}
