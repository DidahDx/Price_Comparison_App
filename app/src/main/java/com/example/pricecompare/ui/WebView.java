package com.example.pricecompare.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.example.pricecompare.R;
import com.google.firebase.analytics.FirebaseAnalytics;

public class WebView extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        android.webkit.WebView web=findViewById(R.id.web);

        web.setWebViewClient(new WebViewClient());
        WebSettings webSettings=web.getSettings();
        webSettings.setJavaScriptEnabled(true);


        Bundle bundle=getIntent().getExtras();
        web.loadUrl(bundle.getString("UrlWebLink"));
    }
}
