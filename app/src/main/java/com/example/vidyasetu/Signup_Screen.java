package com.example.vidyasetu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Signup_Screen extends AppCompatActivity {

    TextView login;
    EditText username, email, password, re_password;
    Button signUp;
    FirebaseAuth auth;
    ProgressBar progressBar;
    DatabaseReference databaseReference;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";  // Email validation pattern

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        login = findViewById(R.id.txtlogin);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);
        signUp = findViewById(R.id.signupbtn);
        progressBar = findViewById(R.id.progressBar);

        signUp.setOnClickListener(v -> {
            Log.d("SIGNUP", "Button clicked");

            String uname = username.getText().toString().trim();
            String Email = email.getText().toString().trim();
            String Password = password.getText().toString().trim();
            String C_password = re_password.getText().toString().trim();

            progressBar.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(uname) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(C_password)) {
                Toast.makeText(Signup_Screen.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (!Email.matches(emailPattern)) {
                email.setError("Enter a valid email");
                progressBar.setVisibility(View.GONE);
            } else if (Password.length() < 6) {
                password.setError("Password must be 6 characters or more");
                progressBar.setVisibility(View.GONE);
            } else if (!Password.equals(C_password)) {
                re_password.setError("Passwords do not match");
                progressBar.setVisibility(View.GONE);
            } else {
                auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserToDatabase(firebaseUser.getUid(), uname, Email);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.d("SIGNUP", task.getException().getMessage());
                        Toast.makeText(Signup_Screen.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        login.setOnClickListener(v -> {
            Intent iLogin = new Intent(Signup_Screen.this, Login_Screen.class);
            startActivity(iLogin);
            finish();
        });
    }

    private void saveUserToDatabase(String id, String uname, String Email) {
        // Store UID along with name & email
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", id);
        userMap.put("name", uname);
        userMap.put("email", Email);

        databaseReference.child(id).setValue(userMap)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup_Screen.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Signup_Screen.this, Login_Screen.class));
                        finish();
                    } else {
                        Toast.makeText(Signup_Screen.this, "Database error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
