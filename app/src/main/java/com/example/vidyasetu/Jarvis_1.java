package com.example.vidyasetu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Jarvis_1 extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jarvis1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button uploadPdfButton = findViewById(R.id.uploadPdfButton);
        Button directChat = findViewById(R.id.directChat);
        uploadPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdfFromStorage();
            }
        });

        directChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat = new Intent(Jarvis_1.this , Jarvis.class);
                startActivity(chat);
            }
        });
//        directChat.setOnClickListener(v -> askLlama("Explain polymorphism in Java."));
    }

    private void selectPdfFromStorage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri pdfUri = data.getData();
            if (pdfUri != null) {
                // Move to the next screen without displaying the PDF
                Toast.makeText(this, "PDF Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Jarvis.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Failed to get PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }
}