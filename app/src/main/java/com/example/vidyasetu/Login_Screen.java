package com.example.vidyasetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Screen extends AppCompatActivity {

    Button btnLogin;
    EditText email, password;
    FirebaseAuth auth;
    TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        boolean isLoggedIn = pref.getBoolean("flag", false); // Check flag

        // ✅ Now check BOTH conditions
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && isLoggedIn) {
            Intent iHome = new Intent(Login_Screen.this, MainActivity.class);
            startActivity(iHome);
            finish();
        }

        btnLogin = findViewById(R.id.loginbtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pwd);
        signup = findViewById(R.id.txtsignup);

        signup.setOnClickListener(v -> {
            Intent iSignUp = new Intent(Login_Screen.this, Signup_Screen.class);
            startActivity(iSignUp);
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            String Email = email.getText().toString().trim();
            String Pass = password.getText().toString().trim();

            if (TextUtils.isEmpty(Email)) {
                Toast.makeText(Login_Screen.this, "Enter the Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(Pass)) {
                Toast.makeText(Login_Screen.this, "Enter the Password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                password.setError("Password must be at least 6 characters");
                return;
            }

            auth.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // ✅ When login successful, set flag true again
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("flag", true);
                    editor.apply();

                    Intent iHome = new Intent(Login_Screen.this, MainActivity.class);
                    startActivity(iHome);
                    finish();
                } else {
                    Toast.makeText(Login_Screen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
