package com.example.cdcnpmat.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cdcnpmat.Model.Bean.Articles;
import com.example.cdcnpmat.Model.DAOiplm.ArticlesDAOImpl;
import com.example.cdcnpmat.R;
import com.example.cdcnpmat.activities.ArticleDetailActivity;

import java.util.List;

public class ArticleAdminAdapter extends BaseAdapter {

    private Context context;
    private List<Articles> articlesList;

    public ArticleAdminAdapter(Context context, List<Articles> articlesList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.article_item_admin, parent, false);
        }

        TextView titleView = convertView.findViewById(R.id.articleTitle);
        TextView authorView = convertView.findViewById(R.id.authorName);
        TextView timestampView = convertView.findViewById(R.id.articleTimestamp);
        ImageView articleImage = convertView.findViewById(R.id.articleImage);
        Button approveButton = convertView.findViewById(R.id.approveButton);
        Button denyButton = convertView.findViewById(R.id.denyButton);

        Articles article = articlesList.get(position);

        titleView.setText(article.getTitle());
        authorView.setText(article.getWriterId());
        timestampView.setText("Published on: " + article.getPublishDate());
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

        approveButton.setOnClickListener(v -> {
            article.setStatusId(100); // Set trạng thái thành 'approved'
            Toast.makeText(context, "Article approved!", Toast.LENGTH_SHORT).show();
            // Gửi yêu cầu cập nhật trạng thái lên server
            updateArticleStatus(article.getId(), 100);
        });

        denyButton.setOnClickListener(v -> {
            article.setStatusId(0); // Set trạng thái thành 'denied'
            Toast.makeText(context, "Article denied!", Toast.LENGTH_SHORT).show();
            // Gửi yêu cầu cập nhật trạng thái lên server
            updateArticleStatus(article.getId(), 0);
        });

        return convertView;
    }

    private void updateArticleStatus(int articleId, int statusId) {
        ArticlesDAOImpl articlesDAO = new ArticlesDAOImpl();
        articlesDAO.edit(articleId, null, null, null, -1, statusId);
    }

}
