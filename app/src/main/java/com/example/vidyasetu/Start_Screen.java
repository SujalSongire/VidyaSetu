package com.example.vidyasetu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Start_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button login = findViewById(R.id.login);
        Button sign = findViewById(R.id.signup);

        login.setOnClickListener(v -> {
            Log.d("StartScreen", "Login button clicked");
            startActivity(new Intent(Start_Screen.this, Login_Screen.class));
            finish();
        });

        sign.setOnClickListener(v -> {
            Log.d("StartScreen", "Sign Up button clicked");
            startActivity(new Intent(Start_Screen.this, Signup_Screen.class));
            finish();
        });

    }
}