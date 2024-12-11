package com.example.cdcnpmat.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;

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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.article_item, parent, false);
        }

        TextView titleView = convertView.findViewById(R.id.articleTitle);
        TextView authorView = convertView.findViewById(R.id.authorName);
        TextView timestampView = convertView.findViewById(R.id.articleTimestamp);
        ImageView articleImage = convertView.findViewById(R.id.articleImage);

        Articles article = articlesList.get(position);
        authorView.setText(article.writerId);
        titleView.setText(article.getTitle());
        timestampView.setText("Published on: " + article.getPublishDate());
        articleImage.setImageResource(R.drawable.ic_sample_image); // Replace with actual image logic.

        // Fetch writer email

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("title", article.getTitle());
            intent.putExtra("author", article.getWriterId()); // Pass the fetched email.
            intent.putExtra("timestamp", article.getPublishDate());
            intent.putExtra("content", article.getContent());
            intent.putExtra("imageResId", R.drawable.ic_sample_image); // Replace with actual image logic.
            context.startActivity(intent);
        });

        return convertView;
    }



}
