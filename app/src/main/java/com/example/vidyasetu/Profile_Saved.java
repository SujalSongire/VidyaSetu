package com.example.vidyasetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Profile_Saved extends AppCompatActivity {
    Toolbar toolbar;
    Button btnlogout;
    TextView txtUname, txtEmail, txtProf, txtUniversity, txtCourse;
    Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_saved);

        toolbar = findViewById(R.id.toolbar);
        btnlogout = findViewById(R.id.btnlogout);
        txtUname = findViewById(R.id.txtUname);
        txtEmail = findViewById(R.id.txtEmail);
        edit = findViewById(R.id.btnEdit);
        txtProf = findViewById(R.id.prof);
        txtUniversity = findViewById(R.id.university);
        txtCourse = findViewById(R.id.course);

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile Saved");
        }

        // Retrieve saved profile details from SharedPreferences
        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);

        // Fetch username and email from SharedPreferences (assuming they're saved in Firebase previously)
        String username = pref.getString("uname", "Username");
        String email = pref.getString("email", "Email ID");

        // Fetch other details from SharedPreferences
        String profession = pref.getString("profession", "Profession");
        String course = pref.getString("course", "Course Taken");
        String university = pref.getString("university", "University");

        // Set the data into TextViews
        txtUname.setText(username);
        txtEmail.setText(email);
        txtProf.setText(profession);
        txtUniversity.setText(university);
        txtCourse.setText(course);

        // Logout functionality
        btnlogout.setOnClickListener(v -> {
            SharedPreferences pref1 = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref1.edit();
            editor.putBoolean("flag", false);
            editor.apply();

            // Navigate to Start Screen after logout
            Intent iStart = new Intent(Profile_Saved.this, Start_Screen.class);
            startActivity(iStart);
            finish();
        });

        // Edit profile functionality
        edit.setOnClickListener(v -> {
            SharedPreferences pref1 = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref1.edit();
            editor.putBoolean("isProfileSaved", false); // Flag for editing
            editor.apply();

            Intent iEdit = new Intent(Profile_Saved.this, Profile.class);
            startActivity(iEdit);
        });
    }
}
