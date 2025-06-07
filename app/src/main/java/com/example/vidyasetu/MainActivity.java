package com.example.vidyasetu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    TextView uname;
    TextView txtEmail;

    LinearLayout profile;
    CardView boardView;
    CardView quizView, chatView;
    CardView jarvisView , readerView;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uname = findViewById(R.id.username);
        txtEmail = findViewById(R.id.txtEmail);
        profile = findViewById(R.id.profile);
        chatView = findViewById(R.id.chatView);
        boardView = findViewById(R.id.boardView);
        quizView = findViewById(R.id.quizView);
        jarvisView = findViewById(R.id.jarvisView);
        readerView = findViewById(R.id.readerView);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("name").getValue(String.class);
                    uname.setText(username);  // Set group name dynamically
                    String email = snapshot.child("email").getValue(String.class);
                    txtEmail.setText(email);
                } else {
                    uname.setText("Username");
                    txtEmail.setText("Email ID");// Default value if not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                uname.setText("Username");
                txtEmail.setText("Email ID");// Default value on error
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                boolean check = pref.getBoolean("isProfileSaved",false);

                if(!check){
                    Intent iProfile = new Intent(MainActivity.this, Profile.class);
                    startActivity(iProfile);
                }
                else{
                    Intent iSave = new Intent(MainActivity.this, Profile_Saved.class);
                    startActivity(iSave);
                }
            }
        });

        chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iBoard = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(iBoard);
            }
        });

        boardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iBoard = new Intent(MainActivity.this, Board.class);
                startActivity(iBoard);
            }
        });

        quizView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iQuiz = new Intent(MainActivity.this, Quiz_Options.class);
                startActivity(iQuiz);
            }
        });

        jarvisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iJarvis = new Intent(MainActivity.this,Jarvis_1.class);
                startActivity(iJarvis);
            }
        });
        readerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iReader = new Intent(MainActivity.this, Reader.class);
                startActivity(iReader);
            }
        });
    }
}


