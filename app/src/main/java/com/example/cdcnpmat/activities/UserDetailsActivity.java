package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.Model.Bean.Users;

import com.example.cdcnpmat.Model.DAOiplm.UsersDAOImpl;
import com.example.cdcnpmat.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView userName, userEmail, userPhone, userRole;
    private Button blockButton, promoteButton, deleteButton; // Thêm nút xóa
    private UsersDAOImpl userDAO;
    private String userId;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userRole = findViewById(R.id.userRole);
        deleteButton = findViewById(R.id.blockButton); // Ánh xạ nút xóa

        userDAO = new UsersDAOImpl();

        // Get user ID from Intent
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load user details
        loadUserDetails();

        // Xử lý sự kiện khi nhấn nút Xóa
        deleteButton.setOnClickListener(v -> deleteUser());
    }

    private void loadUserDetails() {
        executor.execute(() -> {
            Users user = userDAO.findById(userId);
            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    userName.setText(user.getName());
                    userEmail.setText(user.getEmail());
                    userPhone.setText(user.getPhone());
                    userRole.setText(user.getRole());
                }
            });
        });
    }

    private void deleteUser() {
        executor.execute(() -> {
            try {
                // Gọi findById để lấy thông tin người dùng
                Users userToDelete = userDAO.findById(userId);
                if (userToDelete != null) {
                    // Nếu tìm thấy user, thực hiện xóa
                    userDAO.delete(userToDelete);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Xóa người dùng thành công.", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình trước đó
                        loadUserDetails();
                    });
                } else {
                    // Nếu không tìm thấy user, thông báo lỗi
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Không tìm thấy người dùng để xóa.", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi xóa người dùng.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

}
