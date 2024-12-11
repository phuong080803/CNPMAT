package com.example.cdcnpmat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cdcnpmat.R;

public class Non_Login_Activity  extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;

    private SupabaseClient supabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_un_login);


        loginButton = findViewById(R.id.img_button_rectangle);


        supabaseClient = new SupabaseClient();

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(Non_Login_Activity.this, LoginActivity.class);
            startActivity(intent);
        });

    }
}
