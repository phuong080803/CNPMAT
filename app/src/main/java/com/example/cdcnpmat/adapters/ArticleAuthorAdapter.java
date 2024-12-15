package com.example.cdcnpmat.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.DAOiplm.ArticlesDAOImpl;
import com.example.cdcnpmat.R;
import com.example.cdcnpmat.activities.ArticleDetailActivity;

import java.net.CookieHandler;
import java.util.List;

public class ArticleAuthorAdapter extends BaseAdapter {

    private Context context;
    private List<Articles> articlesList;

    public ArticleAuthorAdapter(Context context, List<Articles> articlesList) {
        this.context = context;
        this.articlesList = articlesList;
    }

    @Override
    public int getCount() {
        return articlesList.size();
    }

    @Override
    public Object getItem(int position) {
        return articlesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return articlesList.get(position).getId();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.article_item_author, parent, false);
        }

        // Ánh xạ các thành phần View
        TextView titleView = convertView.findViewById(R.id.articleTitle);
        TextView authorView = convertView.findViewById(R.id.authorName);
        TextView timestampView = convertView.findViewById(R.id.articleTimestamp);
        TextView statusView = convertView.findViewById(R.id.articleStatus);
        ImageView articleImage = convertView.findViewById(R.id.articleImage);

        // Lấy bài viết từ danh sách
        Articles article = articlesList.get(position);

        // Gán dữ liệu cho View
        authorView.setText(article.getWriterId());
        titleView.setText(article.getTitle());
        timestampView.setText("Ngày đăng: " + article.getPublishDate());
        articleImage.setImageResource(R.drawable.ic_sample_image); // Thay bằng logic ảnh thực tế.

        // Gán trạng thái và định dạng màu sắc
        String articleStatus = article.getStatus(); // Status: Pending, Approved, Denied
        statusView.setText(articleStatus);
        switch (articleStatus) {
            case "Pending":
                statusView.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "Approved":
                statusView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "Denied":
                statusView.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            default:
                statusView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }

        // Xử lý sự kiện khi click vào mục
        String imagePath = article.getImg();
        if (imagePath != null && !imagePath.isEmpty()) {
            Uri imageUri = Uri.parse(imagePath); // Chuyển chuỗi thành Uri
            Glide.with(context)
                    .load(imageUri) // Glide sẽ đọc content:// hoặc file://
                    .placeholder(R.drawable.ic_placeholder_image) // Hình tạm
                    .error(R.drawable.ic_error) // Hình lỗi
                    .into(articleImage);
        } else {
            articleImage.setImageResource(R.drawable.ic_placeholder_image); // Nếu không có ảnh
        }

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("article_id", article.getId());
            intent.putExtra("title", article.getTitle());
            intent.putExtra("author", article.getWriterId());
            intent.putExtra("timestamp", article.getPublishDate());
            intent.putExtra("content", article.getContent());
            intent.putExtra("img", article.getImg()); // Pass image URI
            context.startActivity(intent);
        });

        return convertView;
    }




}
