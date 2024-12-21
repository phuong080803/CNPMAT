package com.example.cdcnpmat.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.DAOiplm.ArticlesDAOImpl;
import com.example.cdcnpmat.R;
import com.example.cdcnpmat.activities.ArticleDetailActivity;

import java.net.CookieHandler;
import java.util.List;

public class ArticlesAdapter extends BaseAdapter {

    private Context context;
    private List<Articles> articlesList;

    public ArticlesAdapter(Context context, List<Articles> articlesList) {
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
     
    ArticleAuthorAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.article_item, parent, false);
            holder = new ArticleAuthorAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ArticleAuthorAdapter.ViewHolder) convertView.getTag();
        }
        // Ánh xạ các thành phần View
        TextView titleView = convertView.findViewById(R.id.articleTitle);
        TextView authorView = convertView.findViewById(R.id.authorName);
        TextView timestampView = convertView.findViewById(R.id.articleTimestamp);
        ImageView articleImage = convertView.findViewById(R.id.articleImage);

        // Lấy bài viết từ danh sách
        Articles article = articlesList.get(position);

        // Gán dữ liệu cho View
        authorView.setText(article.getWriterId());
        titleView.setText(article.getTitle());
        timestampView.setText("Ngày đăng: " + article.getPublishDate());
        articleImage.setImageResource(R.drawable.ic_sample_image); // Thay bằng logic ảnh thực tế.

        // Gán trạng thái và định dạng màu sắc


        // Xử lý sự kiện khi click vào mục

        loadArticleImage(holder.articleImage, article.getImg());
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
    }static class ViewHolder {
        TextView titleView, authorView, timestampView, statusView;
        ImageView articleImage;

        ViewHolder(View view) {
            titleView = view.findViewById(R.id.articleTitle);
            authorView = view.findViewById(R.id.authorName);
            timestampView = view.findViewById(R.id.articleTimestamp);
            statusView = view.findViewById(R.id.articleStatus);
            articleImage = view.findViewById(R.id.articleImage);
        }
    }
    private void loadArticleImage(ImageView imageView, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(imagePath);
                Glide.with(context)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_placeholder_image)
                        .error(R.drawable.ic_error)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("GlideError", "Tải ảnh thất bại: " + e.getMessage(), e);
                                Toast.makeText(context, "Không thể tải hình ảnh", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("GlideSuccess", "Tải ảnh thành công");
                                return false;
                            }
                        })
                        .into(imageView);
            } catch (Exception e) {
                Log.e("GlideError", "URI không hợp lệ: " + imagePath, e);
                imageView.setImageResource(R.drawable.ic_placeholder_image);
            }
        } else {
            imageView.setImageResource(R.drawable.ic_placeholder_image);
        }
    }



}
