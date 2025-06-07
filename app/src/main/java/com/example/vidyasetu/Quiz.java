package com.example.vidyasetu;

//import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//public class Quiz extends AppCompatActivity {
//
////    private static final int FILE_CHOOSER_REQUEST_CODE = 100;
////    private ValueCallback<Uri[]> fileChooserCallback;
//    private WebView webView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_quiz);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        webView = findViewById(R.id.webView);
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setAllowContentAccess(true);
//
//        webView.setWebViewClient(new WebViewClient());
////        webView.setWebChromeClient(new WebChromeClient() {
////            @Override
////            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
////                                             WebChromeClient.FileChooserParams fileChooserParams) {
////                if (fileChooserCallback != null) {
////                    fileChooserCallback.onReceiveValue(null);
////                }
////                fileChooserCallback = filePathCallback;
////
////                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////                intent.addCategory(Intent.CATEGORY_OPENABLE);
////                intent.setType("*/*"); // Accept all file types
////                startActivityForResult(Intent.createChooser(intent, "Choose File"), FILE_CHOOSER_REQUEST_CODE);
////                return true;
////            }
////        });
//
//        webView.loadUrl("https://quiz-generator-2-0.onrender.com");
//    }
//
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////
////        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
////            if (fileChooserCallback != null) {
////                Uri[] result = (resultCode == Activity.RESULT_OK && data != null) ?
////                        new Uri[]{data.getData()} : null;
////                fileChooserCallback.onReceiveValue(result);
////                fileChooserCallback = null;
////            }
////        }
////    }
//
//    // Handle back button using OnBackPressedDispatcher
//    getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//        @Override
//        public void handleOnBackPressed() {
//            if (webView.canGoBack()) {
//                webView.goBack(); // Navigate back in WebView
//            } else {
//                finish(); // Close activity if no history
//            }
//        }
//    });
//}

public class Quiz extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://quiz-generator-2-0.onrender.com");

        // Handle back button using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack(); // Navigate back in WebView
                } else {
                    finish(); // Close activity if no history
                }
            }
        });
    }
}

