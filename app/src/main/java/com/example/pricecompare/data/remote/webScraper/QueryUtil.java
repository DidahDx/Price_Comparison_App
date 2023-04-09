package com.example.pricecompare.data.remote.webScraper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pricecompare.data.model.Products;
import com.example.pricecompare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class QueryUtil {

    public static final String LOG_TAG = QueryUtil.class.getSimpleName();

    private static String url;
    private static String kilUrl;
    private static String masokUrl;
    private static String jumiaPriceNew, jumiaPriceOld, jumiaDescription,
            jumiaUrlLink, jumiaImgUrl, jumiaImgLogoUrl, jumiaDiscountPercent, jumiaContainer,

    kilimallPriceNew, kilimallPriceOld, kilimallDescription,
            kilimallUrlLink, kilimallImgUrl, kilimallImgUrl2, kilimallImgLogoUrl, kilimallContainer,

    masokoPriceNew, masokoPriceOld, masokoDescription,
            masokoUrlLink, masokoImgUrl, masokoImgLogoUrl, masokoContainer;


    static DecimalFormat df;
    static FirebaseRemoteConfig mFirebaseRemoteConfig;

    public QueryUtil() {
    }

    /**
     * Query used to return  website data
     */
    public static List<Products> fetchWebsiteData(String requestUrl, String kiliUrl, String masokoUrl) {

        kilUrl = kiliUrl;
        url = requestUrl;
        masokUrl = masokoUrl;

        // Return the an arrayList
        return extractShoppingData();
    }

    //method is used to web scrape the online websites
    private static ArrayList<Products> extractShoppingData() {
        df = new DecimalFormat("#");
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Products> products = new ArrayList<>();

        // build up a list of Product objects with the corresponding data.
        Document doc = null;
        Document docKili = null;
        Document docMasoko = null;
//            try {
        fetchScrappingConfig();


        try {

            docKili = Jsoup.connect(kilUrl)
                    .sslSocketFactory(socketFactory())
                    .header("authority", "www.kilimall.co.ke")
                    .header("method", "GET")
                    .header("scheme", "https")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "en-US,en;q=0.9")
                    .header("cache-control", "max-age=0")
                    .header("Connection", "keep-alive")
                    .header("Host", "www.kilimall.co.ke")
                    .referrer(kilUrl)
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
                    .header("upgrade-insecure-requests", "1")
                    .maxBodySize(0)
                    .timeout(60000)
                    .get();

            //kilimall web scraping content
            Log.e("Kilimall", String.valueOf(docKili));
            Log.e("Kilimall", String.valueOf(docKili.select(kilimallContainer)));
            for (Element row : docKili.select(kilimallContainer)) {
                Products pro1;
                String imageurl;

                if (row.select(kilimallPriceNew).text().equals("") && row.select(kilimallImgUrl2).attr("abs:src").equals("") &&
                        row.select(kilimallImgUrl).attr("abs:data-src").equals("")) {
                    continue;
                } else {

                    if (!row.select(kilimallImgUrl).attr("abs:data-src").equals("")) {
                        imageurl = row.select(kilimallImgUrl).attr("abs:data-src");
                    } else {
                        imageurl = row.select(kilimallImgUrl2).attr("abs:src");
                    }

                    String productLink = row.select(kilimallUrlLink).attr("href");
                    String NewPrice = row.select(kilimallPriceNew).text();
                    String priceOld = row.select(kilimallPriceOld).text();
                    String percentageOff = "";

                    try {
                        if (!priceOld.isEmpty() && priceOld.indexOf('-') == -1) {
                            priceOld = priceOld.toLowerCase().trim();

                            priceOld = priceOld.replace("save", "");
                            priceOld = priceOld.replace("Save KSh", "");
                            priceOld = priceOld.replace("ksh", "");
                            priceOld = priceOld.replace(",", "");
                            priceOld = priceOld.trim();
                            int Old = Integer.parseInt(priceOld);

                            int Nprice = Integer.parseInt(NewPrice.trim().replace("KSh", "").replace(",", "").trim());

                            priceOld = "KSh " + (Old + Nprice);

                            float dOld = Float.valueOf(priceOld.replace("KSh", "").replace(",", ""));
                            float dNew = Float.valueOf(NewPrice.replace("KSh", "").replace(",", ""));
                            percentageOff = "-";
                            percentageOff += df.format(100 - ((dNew / dOld) * 100));
                            percentageOff += "%";

                        }
                    } catch (NumberFormatException e) {
                        Log.d(LOG_TAG, "Error calculating Kilimall percentage " + e.getStackTrace(), e);
                    }


                    String productdecrption = row.select(kilimallDescription).text();
                    productdecrption = productdecrption.replace("Add to cart", "");

                    String imglogo = kilimallImgLogoUrl;

                    pro1 = new Products(productdecrption, priceOld, imageurl, productLink, imglogo, NewPrice, percentageOff, "Kilimall");
                }

                products.add(pro1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            docMasoko = Jsoup.connect(masokUrl).get();
            Log.e("masoko tag",docMasoko.toString());

            //masoko web scraping content
            for (Element row : docMasoko.select(masokoContainer)) {
                Products pro2;
                String imageurl;

                if (row.select(masokoUrlLink).attr("href").equals("")) {
                    continue;
                } else {

                    imageurl = row.select(masokoImgUrl).attr("abs:src");
                    String productLink = row.select(masokoUrlLink).attr("href");
                    String priceOld = row.select(masokoPriceOld).text();
                    String productdecrption = row.select(masokoDescription).text();
                    String NewPrice = row.select(masokoPriceNew).text();
                    String imglogo = masokoImgLogoUrl;

                    priceOld = priceOld.replace("Price", "");
                    NewPrice = NewPrice.replace(priceOld, "");

                    if (NewPrice.indexOf('.') != -1) {
                        NewPrice = NewPrice.substring(0, NewPrice.length() - 3);
                    }

                    String percentageOff = "";
                    if (!priceOld.isEmpty() && priceOld.indexOf('-') == -1) {


                        try {
                            float dOld = Float.valueOf(priceOld.replace("KES", "").replace(",", ""));
                            float dNew = Float.valueOf(NewPrice.replace("KES", "").replace(",", ""));
                            percentageOff = "-";
                            percentageOff += df.format(100 - ((dNew / dOld) * 100));
                            percentageOff += "%";
                        } catch (NumberFormatException e) {
                            Log.d(LOG_TAG, "Error in Caculation " + e.getStackTrace().toString());
                        }
                    }


                    pro2 = new Products(productdecrption, priceOld, imageurl, productLink, imglogo, NewPrice, percentageOff, "Masoko");
                }
                products.add(pro2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            doc = Jsoup.connect(url).sslSocketFactory(socketFactory()).get();

            //jumia web scraping content
            for (Element row : doc.select(jumiaContainer)) {
                Products pro;

                if (row.select(jumiaDescription).text().equals("")) {
                    continue;
                } else {
                    String imageurl = row.select(jumiaImgUrl).attr("data-src");
                    String productLink = row.select(jumiaUrlLink).attr("href");
                    String priceOld = row.select(jumiaPriceOld).text();
                    String productdecrption = row.select(jumiaDescription).text();
                    String NewPrice = row.select(jumiaPriceNew).text();
                    String imglogo = jumiaImgLogoUrl;
                    String percentageOff = row.select(jumiaDiscountPercent).text();
                    NewPrice = NewPrice.replace(priceOld, "");
                    String NewProduct = row.select("span.new-flag").text();


                    pro = new Products(productdecrption, priceOld, imageurl, "https://www.jumia.co.ke"+productLink, imglogo, NewPrice, percentageOff, "Jumia");
                }

                products.add(pro);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Return the list of products
        return products;
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


    private static void fetchScrappingConfig() {

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        long cacheExpiration = 10200;
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activate();
                        }
                    }
                });

        String scrapConfig = mFirebaseRemoteConfig.getString("webscraping_css");

        initializeScrappingConfig(scrapConfig);

    }

    private static void initializeScrappingConfig(String scrapJson) {
        try {
            JSONObject baseJSONresponse = new JSONObject(scrapJson);
            JSONObject websites = baseJSONresponse.getJSONObject("websites");

            JSONObject jumiaCss = websites.getJSONObject("jumia");
            jumiaPriceNew = jumiaCss.getString("priceNew");
            jumiaPriceOld = jumiaCss.getString("priceOld");
            jumiaDescription = jumiaCss.getString("description");
            jumiaUrlLink = jumiaCss.getString("urlLink");
            jumiaImgUrl = jumiaCss.getString("imgUrl");
            jumiaImgLogoUrl = jumiaCss.getString("imgLogoUrl");
            jumiaDiscountPercent = jumiaCss.getString("discountPercent");
            jumiaContainer = jumiaCss.getString("container");

            JSONObject kilimallCss = websites.getJSONObject("kilimall");
            kilimallPriceNew = kilimallCss.getString("priceNew");
            kilimallPriceOld = kilimallCss.getString("priceOld");
            kilimallDescription = kilimallCss.getString("description");
            kilimallUrlLink = kilimallCss.getString("urlLink");
            kilimallImgUrl = kilimallCss.getString("imgUrl");
            kilimallImgUrl2 = kilimallCss.getString("imgUrl2");
            kilimallImgLogoUrl = kilimallCss.getString("imgLogoUrl");
            kilimallContainer = kilimallCss.getString("container");

            JSONObject masokoCss = websites.getJSONObject("masoko");
            masokoPriceNew = masokoCss.getString("priceNew");
            masokoPriceOld = masokoCss.getString("priceOld");
            masokoDescription = masokoCss.getString("description");
            masokoUrlLink = masokoCss.getString("urlLink");
            masokoImgUrl = masokoCss.getString("imgUrl");
            masokoImgLogoUrl = masokoCss.getString("imgLogoUrl");
            masokoContainer = masokoCss.getString("container");

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
}