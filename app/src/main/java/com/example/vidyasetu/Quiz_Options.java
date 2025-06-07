package com.example.vidyasetu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Quiz_Options extends AppCompatActivity {

    Button GenerateQuiz, SelectTopic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_options);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GenerateQuiz = findViewById(R.id.generate_quiz);
        SelectTopic = findViewById(R.id.quiz_game);

        GenerateQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent generateQuiz = new Intent(Quiz_Options.this, Generate_Quiz.class);
                startActivity(generateQuiz);
            }
        });
        SelectTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent generateQuiz = new Intent(Quiz_Options.this, Quiz.class);
                startActivity(generateQuiz);
            }
        });
    }
}