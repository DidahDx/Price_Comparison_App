package com.example.pricecompare.WebScraper;

import android.util.Log;

import com.example.pricecompare.Products;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtil {

    public static final String LOG_TAG =QueryUtil.class.getSimpleName();

    static String url;
    static String kilUrl;
    static String masokUrl;

    public QueryUtil(){

    }


    /**
     * Query the online website and return an {@link List} object to represent a single earthquake.
     */
    public static List<Products> fetchWebsiteData(String requestUrl, String kiliUrl, String masokoUrl) {

        kilUrl=kiliUrl;
        url=requestUrl;
        masokUrl=masokoUrl;
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }


        // Return the {@link Event}
        return extractShoppingData(jsonResponse);
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String webScrapeResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return webScrapeResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                webScrapeResponse = readFromStream(inputStream);

            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }
        return webScrapeResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    public static ArrayList<Products> extractShoppingData(String sample) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Products> products = new ArrayList<>();

            // build up a list of Product objects with the corresponding data.
            Document doc= null;
            Document docKili=null;
            Document docMasoko=null;
            try {
//                 doc = Jsoup.connect("https://www.jumia.co.ke").data("header-search-input", "earphone samsung").post();
                doc = Jsoup.connect(url).get();
                docKili=Jsoup.connect(kilUrl).get();
                docMasoko=Jsoup.connect(masokUrl).get();





        //kilimall webscraping
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
                String priceOld=row.select("div.goods-discount").text();
                String productdecrption=row.select("a").text();
                productdecrption=productdecrption.replace("Add to cart","");
                String NewPrice=row.select("em.sale-price").text();
                String imglogo="https://image.kilimall.com/kenya/shop/common/05850520183675844.png";

                pro1 = new Products(productdecrption,priceOld,imageurl,productLink,imglogo,NewPrice);
            }

            products.add(pro1);
        }

        //masoko webscraping
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


                pro2 = new Products(productdecrption,priceOld,imageurl,productLink,imglogo,NewPrice);
            }
            products.add(pro2);
        }

            //jumia webscrapiing
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
                    NewPrice=NewPrice.replace(priceOld,"");


                    pro = new Products(productdecrption,priceOld,imageurl,productLink,imglogo,NewPrice);
                }

                products.add(pro);
            }

            } catch (IOException e) {
                e.printStackTrace();
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtil", "Problem parsing  results", e);

            }

        // Return the list of products
        return products;
    }

}