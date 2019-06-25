package com.example.pricecompare;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QueryUtil {
//
//    public static final String LOG_TAG =QueryUtil.class.getSimpleName();
//
//    public QueryUtil(){
//    }
//
//
//    /**
//     * Query the online website and return an {@link List} object to represent a single earthquake.
//     */
//    public static List<Products> fetchWebsiteData(String requestUrl) {
//        // Create URL object
//        URL url = createUrl(requestUrl);
//
//        // Perform HTTP request to the URL and receive a JSON response back
//        String jsonResponse = null;
//        try {
//            jsonResponse = makeHttpRequest(url);
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error closing input stream", e);
//        }
//
//
//        // Return the {@link Event}
//        return extractEarthquakes(jsonResponse);
//    }
//
//    /**
//     * Make an HTTP request to the given URL and return a String as the response.
//     */
//    private static String makeHttpRequest(URL url) throws IOException {
//        String jsonResponse = "";
//
//        // If the URL is null, then return early.
//        if (url == null) {
//            return jsonResponse;
//        }
//
//        HttpURLConnection urlConnection = null;
//
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setReadTimeout(10000 /* milliseconds */);
//            urlConnection.setConnectTimeout(15000 /* milliseconds */);
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            // If the request was successful (response code 200),
//
//            if (urlConnection.getResponseCode() == 200) {
//
//
//            } else {
//                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
//            }
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//
//        }
//        return jsonResponse;
//    }
//
//
//
//
//    /**
//     * Returns new URL object from the given string URL.
//     */
//    public static URL createUrl(String stringUrl) {
//        URL url = null;
//        try {
//            url = new URL(stringUrl);
//        } catch (MalformedURLException e) {
//            Log.e(LOG_TAG, "Error with creating URL ", e);
//        }
//        return url;
//    }
//
//    public static ArrayList<EarthQuake> extractEarthquakes(String sampleJson) {
//
//        // Create an empty ArrayList that we can start adding earthquakes to
//        ArrayList<EarthQuake> earthquakes = new ArrayList<>();
//
//        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
//        // is formatted, a JSONException exception object will be thrown.
//        // Catch the exception so the app doesn't crash, and print the error message to the logs.
//        try {
//
//            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
//            // build up a list of Earthquake objects with the corresponding data.
//
//            JSONObject baseJSONresponse = new JSONObject(sampleJson);
//            JSONArray earthQuakeArray = baseJSONresponse.getJSONArray("features");
//
//            for (int i = 0; i < earthQuakeArray.length(); i++) {
//                JSONObject currentEarthQuake = earthQuakeArray.getJSONObject(i);
//                JSONObject properties = currentEarthQuake.getJSONObject("properties");
//                String magnitude = properties.getString("mag");
//                String time =   properties.getString("time");
//                String location = properties.getString("place");
//                String url=properties.getString("url");
//                String Tm=getDateString(Long.parseLong(time));
//                String [] Time=Tm.split("at");
//                String [] place=location.split(",");
//
//                EarthQuake earthQuake;
//                if (place.length==2){
//                    earthQuake = new EarthQuake(magnitude, place[1], Time[0],url,Time[1],place[0]);
//                }else{
//                    earthQuake = new EarthQuake(magnitude, location, Time[0],url,Time[1]);
//                }
//
//                earthquakes.add(earthQuake);
//
//            }
//
//        } catch (JSONException e) {
//            // If an error is thrown when executing any of the above statements in the "try" block,
//            // catch the exception here, so the app doesn't crash. Print a log message
//            // with the message from the exception.
//            Log.e("QueryUtil", "Problem parsing  results", e);
//        }
//
//        // Return the list of earthquakes
//        return earthquakes;
//    }
//
}
