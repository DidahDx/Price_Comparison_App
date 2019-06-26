package com.example.pricecompare.jumia;

import android.util.Log;

import com.example.pricecompare.Products;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class jumiaScrape {

    //used to get the data from website
    public static ArrayList<Products> getData(){

        ArrayList<Products> Jumiaproduct = null;

        try {
            Document doc= Jsoup.connect(buildUrl()).get();

//            for (Element row:doc.select("section.products.-mabaya div.sku.-gallery")){
//
//                String imageurl=row.select("img.lazy.image.-loaded").attr("src");
//                String productLink=row.select("a.link").attr("href");
//                String productdecrption=row.select("span.name").text();
//                String productPrice=row.select("span.price").text();
//                    if (doc.select("section.products.-mabaya div").equals("")){
//
//                    }else{
//                        Jumiaproduct.add(new Products(productdecrption,productPrice));
//                        Log.i("Error Add", "Error Adding products from web");
//
//                    }


//            }
           String tex= doc.text();

            Jumiaproduct.add(new Products(tex,tex));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Jumiaproduct;
    }

    //building the url
    public static String buildUrl(){
        String url="https://www.jumia.co.ke/oppo/?q=a7";


        return url;
    }

}
