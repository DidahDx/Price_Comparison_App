package com.example.pricecompare.WebScraper;

import com.example.pricecompare.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ScannerQuery {

    public ScannerQuery(){

    }

    //this method
    public static String getBarcodeName(String google, String databaseWeb){
        String productName=" Not Found";
        Document Google=null;
        Document DatabaseWeb=null;
        String googleName=" ";

        try {
            Google = Jsoup.connect(google).sslSocketFactory(socketFactory()).get();
            DatabaseWeb = Jsoup.connect(databaseWeb).sslSocketFactory(socketFactory()).get();


             googleName=Google.select("h3.LC20lb").text();

            if (googleName.indexOf('-') != -1) {
                googleName = googleName.replace(googleName.substring(googleName.indexOf('-') + 1), "");
                googleName = googleName.replace("-", "");
            }

            String webName=DatabaseWeb.select("h1.pageTitle").text();

            if (webName.indexOf('-') != -1) {
                webName = webName.replace(webName.substring(webName.indexOf('-') + 1), "");
                webName = webName.replace("-", "");
            }else if ( webName.indexOf(':')!= -1){
                    webName="";
            }

            if (googleName.matches("[0-9]+")){
                googleName="";
            }

            if (!webName.trim().isEmpty()){
                productName=webName;

            }else if (!googleName.isEmpty() && productName.isEmpty()){
                productName=googleName;

            }else{
                productName=" Not Found";
            }


        } catch (IOException e) {
            productName="Something went wrong, Check your Internet Connection and Try again";
            e.printStackTrace();
        }


        return productName;
    }



    private static SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }
}
