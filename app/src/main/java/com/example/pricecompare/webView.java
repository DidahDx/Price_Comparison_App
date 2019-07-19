package com.example.pricecompare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.analytics.FirebaseAnalytics;

public class webView extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        WebView web=findViewById(R.id.web);

        web.setWebViewClient(new WebViewClient());
        WebSettings webSettings=web.getSettings();
        webSettings.setJavaScriptEnabled(true);


        Bundle bundle=getIntent().getExtras();
        web.loadUrl(bundle.getString("UrlWebLink"));
    }
}
