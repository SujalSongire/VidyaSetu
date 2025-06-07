package com.example.vidyasetu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.parser.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class Reader extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private TextView txtPdfContent;
    private Button btnPickPdf, btnListen, btnPause, btnPlay;

    private String pdfText = ""; // Stores the extracted text
    private int currentSentenceIndex = 0;  // Track the current sentence index being read
    private boolean isPaused = false;      // Flag to check if reading is paused
    private String[] sentences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        txtPdfContent = findViewById(R.id.txtPdfContent);
        btnPickPdf = findViewById(R.id.btnPickPdf);
        btnListen = findViewById(R.id.btnListen);
        btnPause = findViewById(R.id.btnPause);
        btnPlay = findViewById(R.id.btnPlay);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                } else {
                    Toast.makeText(Reader.this, "TTS Initialization Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPickPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPdf();
            }
        });

        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenFromStart();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseReading();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeReading();
            }
        });
    }

    // Function to open file picker
    private void pickPdf() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 101);
    }

    // Handle the PDF file selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    pdfText = extractTextFromPdf(inputStream);
                    txtPdfContent.setText(pdfText); // Display text on screen
                } catch (Exception e) {
                    Toast.makeText(this, "Error reading PDF", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Extract text from PDF using iText7
    private String extractTextFromPdf(InputStream inputStream) {
        StringBuilder text = new StringBuilder();
        try {
            PdfReader reader = new PdfReader(inputStream);
            PdfDocument pdfDoc = new PdfDocument(reader);

            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                String pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
                text.append(pageText).append("\n");
            }
            pdfDoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    // Listen from the beginning when the Listen button is clicked
    private void listenFromStart() {
        currentSentenceIndex = 0;  // Reset the sentence index to the beginning
        isPaused = false;
        speakText();  // Start from the first sentence
    }

    // Speak text sentence by sentence
    private void speakText() {
        if (pdfText.isEmpty()) {
            Toast.makeText(this, "No text to read", Toast.LENGTH_SHORT).show();
            return;
        }

        // Split the text into sentences using a regex pattern
        sentences = pdfText.split("(?<=[.!?])\\s*");

        // Start speaking from the current sentence index
        if (currentSentenceIndex < sentences.length) {
            textToSpeech.speak(sentences[currentSentenceIndex], TextToSpeech.QUEUE_FLUSH, null, null);
            currentSentenceIndex++;  // Move to the next sentence after speaking
        }
    }

    // Pause reading
    private void pauseReading() {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            isPaused = true;  // Set the paused flag to true
        }
    }

    // Play button to resume from the last position
    private void resumeReading() {
        if (isPaused && currentSentenceIndex < sentences.length) {
            isPaused = false;
            speakText();  // Resume from the last paused sentence
        }
    }
    @Override
    protected void onDestroy() {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        textToSpeech.shutdown();
        super.onDestroy();
    }
}
