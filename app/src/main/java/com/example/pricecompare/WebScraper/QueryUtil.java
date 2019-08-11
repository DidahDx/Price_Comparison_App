package com.example.pricecompare.WebScraper;

import android.util.Log;

import com.example.pricecompare.Products;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
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

    public static final String LOG_TAG =QueryUtil.class.getSimpleName();

    private static String url;
    private static String kilUrl;
    private static String masokUrl;
    static DecimalFormat df;

    public QueryUtil(){}
    /**
     * Query used to return  website data
     */
    public static List<Products> fetchWebsiteData(String requestUrl, String kiliUrl, String masokoUrl) {

        kilUrl=kiliUrl;
        url=requestUrl;
        masokUrl=masokoUrl;

        // Return the an arrayList
        return extractShoppingData();
    }

    //method is used to web scrape the online websites
    private static ArrayList<Products> extractShoppingData() {
        df = new DecimalFormat("#.#");
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Products> products = new ArrayList<>();

            // build up a list of Product objects with the corresponding data.
            Document doc= null;
            Document docKili=null;
            Document docMasoko=null;
            try {
                doc = Jsoup.connect(url).sslSocketFactory(socketFactory()).get();
                docKili=Jsoup.connect(kilUrl).sslSocketFactory(socketFactory()).get();
                docMasoko=Jsoup.connect(masokUrl).sslSocketFactory(socketFactory()).get();


        //kilimall web scraping content
        for (Element row:docKili.select("ul.list_pic li.item")) {
            Products pro1;
            String imageurl;

            if (row.select("em.sale-price").text().equals("")&& row.select("img[src]").attr("abs:src").equals("") &&
                    row.select("div.goods-pic a[data-src]").attr("abs:data-src").equals("") ){
                continue;
            }else{

                if (!row.select("div.goods-pic a[data-src]").attr("abs:data-src").equals("")){
                    imageurl=row.select("div.goods-pic a[data-src]").attr("abs:data-src");
                }else{
                    imageurl=row.select("img[src]").attr("abs:src");
                }

                String productLink=row.select("a.lazyload").attr("href");
                String NewPrice=row.select("em.sale-price").text();
                String priceOld=row.select("div.goods-discount").text();
                String percentageOff="";

                try{
                if (!priceOld.isEmpty()&& priceOld.indexOf('-') == -1){
                    priceOld=priceOld.toLowerCase().trim();

                    priceOld=priceOld.replace("save", "");
                    priceOld=priceOld.replace("Save KSh", "");
                   priceOld= priceOld.replace("ksh", "");
                   priceOld= priceOld.replace(",", "");
                   priceOld= priceOld.trim();
                    int Old=Integer.parseInt(priceOld);

                    int Nprice=Integer.parseInt(NewPrice.trim().replace("KSh","").replace(",","").trim());
                    percentageOff="";
                    priceOld="KSh "+ (Old + Nprice);

                    float dOld=Float.valueOf(priceOld.replace("KSh","").replace(",",""));
                    float dNew=Float.valueOf(NewPrice.replace("KSh","").replace(",",""));

                percentageOff+=df.format(100-((dNew/dOld)*100));
                percentageOff+="% OFF";

                }}
                catch (NumberFormatException e){
                    Log.d(LOG_TAG,"Error calculating Kilimall percentage "+e.getStackTrace(),e);
                }


                String productdecrption=row.select("a").text();
                productdecrption=productdecrption.replace("Add to cart","");

                String imglogo="https://image.kilimall.com/kenya/shop/common/05850520183675844.png";

                pro1 = new Products(productdecrption,priceOld,imageurl,productLink,imglogo,NewPrice,percentageOff);
            }

            products.add(pro1);
        }

        //masoko web scraping content
        for (Element row:docMasoko.select("ol.products.list.items.product-items li.item.product.product-item")) {
            Products pro2;
            String imageurl;

            if (row.select("a.product.photo.product-item-photo").attr("href").equals("") ){
                continue;
            }else{

                imageurl=row.select("img[src]").attr("abs:src");
                String productLink=row.select("a.product.photo.product-item-photo").attr("href");
                String priceOld=row.select("span.old-price").text();
                String productdecrption=row.select("a.product-item-link").text();
                String NewPrice=row.select("span.price").text();
                String imglogo="https://www.masoko.com/media/logo/stores/1/masoko_fest_logo.png";

                priceOld=priceOld.replace("Price","");
                NewPrice=NewPrice.replace(priceOld,"");

                if (NewPrice.indexOf('.') != -1){
                    NewPrice= NewPrice.substring(0,NewPrice.length()-3);
                }

                String percentageOff="";
                if (!priceOld.isEmpty() && priceOld.indexOf('-') == -1){

                    try {
                        float dOld=Float.valueOf(priceOld.replace("KES","").replace(",",""));
                        float dNew=Float.valueOf(NewPrice.replace("KES","").replace(",",""));

                        percentageOff+=df.format(100-((dNew/dOld)*100));
                        percentageOff+="% OFF";
                    }catch (NumberFormatException e){
                        Log.d(LOG_TAG,"Error in Caculation"+e.getStackTrace(),e);
                    }
                }


                pro2 = new Products(productdecrption,priceOld,imageurl,productLink,imglogo,NewPrice,percentageOff);
            }
            products.add(pro2);
        }

            //jumia web scraping content
            for (Element row:doc.select("section.products.-mabaya div.sku.-gallery")) {
                Products pro;

                if (row.select("span.name").text().equals("")){
                    continue;
                }else{
                    String imageurl=row.select("img[src]").attr("abs:src");
                    String productLink=row.select("a.link").attr("href");
                    String priceOld=row.select("span.price.-old").text();
                    String productdecrption=row.select("span.name").text();
                    String NewPrice=row.select("span.price").text();
                    String imglogo="https://static.jumia.co.ke/cms/icons/jumialogo-x-4.png";
                    String percentageOff=row.select("span.sale-flag-percent").text();
                    NewPrice=NewPrice.replace(priceOld,"");
                    String NewProduct=row.select("span.new-flag").text();

                    if (percentageOff.indexOf('-') != -1){
                        percentageOff=percentageOff.replace("-","");
                        percentageOff+=" OFF";
                    }


                    pro = new Products(productdecrption,priceOld,imageurl,productLink,imglogo,NewPrice,percentageOff);
                }

                products.add(pro);
            }

            } catch (IOException e) {
                e.printStackTrace();
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e(LOG_TAG, "Problem parsing  results", e);

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
}