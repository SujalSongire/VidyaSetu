package com.example.vidyasetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {
    Toolbar toolbar;
    Button btnlogout, save;
    EditText txtUname, txtEmail, txtProf, txtUniversity, txtCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        btnlogout = findViewById(R.id.btnlogout);
        save = findViewById(R.id.btnsave);
        txtUname = findViewById(R.id.txtUname);
        txtEmail = findViewById(R.id.txtEmail);
        txtProf = findViewById(R.id.prof);
        txtUniversity = findViewById(R.id.university);
        txtCourse = findViewById(R.id.course);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        // Fetch user details from Firebase (username & email)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Retrieve and display username and email
                String username = task.getResult().child("name").getValue(String.class);
                String email = task.getResult().child("email").getValue(String.class);

                // Set username and email to text fields
                txtUname.setText(username);
                txtEmail.setText(email);
            }
        });

        // Save profile details to SharedPreferences
        save.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            // Save entered profile data
            editor.putString("uname", txtUname.getText().toString());
            editor.putString("email", txtEmail.getText().toString());
            editor.putString("profession", txtProf.getText().toString());
            editor.putString("course", txtCourse.getText().toString());
            editor.putString("university", txtUniversity.getText().toString());
            editor.putBoolean("isProfileSaved", true);  // Flag to indicate profile is saved
            editor.apply();

            // Switch to Profile Saved screen
            Intent intent = new Intent(Profile.this, Profile_Saved.class);
            startActivity(intent);
            finish();
        });

        // Logout functionality
        btnlogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            SharedPreferences pref1 = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor1 = pref1.edit();
            editor1.putBoolean("flag", false);
            editor1.apply();

            Intent iStart = new Intent(Profile.this, Login_Screen.class);
            startActivity(iStart);
            finish();
        });
    }
}
