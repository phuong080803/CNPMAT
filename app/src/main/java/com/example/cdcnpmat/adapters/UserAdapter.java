package com.example.cdcnpmat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cdcnpmat.Model.Bean.Users;
import com.example.cdcnpmat.R;
import com.example.cdcnpmat.activities.UserDetailsActivity;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private List<Users> userList;

    public UserAdapter(Context context, List<Users> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.userName);
        TextView userEmail = convertView.findViewById(R.id.userEmail);
        Button blockButton = convertView.findViewById(R.id.delUserButton);

        Users user = userList.get(position);
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        convertView.setOnClickListener(v -> {
            // Ví dụ: Mở màn hình chi tiết người dùng
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra("userId", user.getId()); // Truyền ID của user
            context.startActivity(intent);
        });
        blockButton.setOnClickListener(v -> {
            // Handle user blocking
        });

        return convertView;
    }
}
