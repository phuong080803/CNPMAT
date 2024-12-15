package com.example.cdcnpmat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cdcnpmat.Model.Bean.Comments;
import com.example.cdcnpmat.R;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<Comments> comments;

    public CommentAdapter(Context context, List<Comments> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        }

        Comments comment = comments.get(position);

        TextView commentUser = convertView.findViewById(R.id.commentUser);
        TextView commentText = convertView.findViewById(R.id.commentContent);
        TextView commentDate = convertView.findViewById(R.id.commentDate);

        commentUser.setText(comment.getUserId());
        commentText.setText(comment.getComment());
        commentDate.setText(comment.getDate());

        return convertView;
    }
}
