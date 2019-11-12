package com.example.pricecompare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.example.pricecompare.DataModel.Recent;
import com.example.pricecompare.WebScraper.QueryUtil;
import com.example.pricecompare.WebScraper.ScannerQuery;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.example.pricecompare.Search.editSearch;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler, LoaderManager.LoaderCallbacks<String>{

    private static final int REQUEST_CAMERA=100;
    private ZXingScannerView scannerView;
    static String jumiaUrl;
    static String kilimallUrl;
    static String MasokoUrl;
    String scanResult;
    String scanName;
    static String barcodeUrl;
    static String  googleUrl;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

//        TextView textView=new TextView(this);
//        textView.setText("TEXT");
//        scannerView.addView(textView);
        //checking camera permission
        if (ContextCompat.checkSelfPermission(Scanner.this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Scanner.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
        }

    }

    //requesting camera permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CAMERA){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Camera permission granted",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Camera permission denied",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        scanResult=result.getText();
        scanName="";

        BarcodeUrl();

        //checking network before reloading
        ConnectivityManager conManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=conManager.getActiveNetworkInfo();
        progressDialog =new ProgressDialog(Scanner.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait.");
        progressDialog.setCancelable(false);

        if (scanResult.matches("[0-9]+")) {
            if (networkInfo != null && networkInfo.isConnected()) {
                progressDialog.show();
                getSupportLoaderManager().restartLoader(20, null, this);
            } else {
                progressDialog.dismiss();
                Toast.makeText(Scanner.this,"Check network and try again",Toast.LENGTH_LONG).show();
                scannerView.resumeCameraPreview(Scanner.this);
            }
        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Scan Result");

            builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(Scanner.this);
                    scanName=" ";
                }
            });


            builder.setNeutralButton("Search", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                    buildJumiaUrl();
                    buildKilimallUrl();
                    buildMasokoUrl();

                    Intent i =new Intent(Scanner.this,ProductList.class);

                    Search.recentSearch.add(new Recent(scanResult,R.drawable.ic_barcode,R.drawable.ic_close));

                    editSearch.setText(scanResult);
                    i.putExtra("JumiaUrl",jumiaUrl);
                    i.putExtra("kilimallUrl",kilimallUrl);
                    i.putExtra("MasokoUrl",MasokoUrl);
                    i.putExtra("ProductName",scanResult);
                    startActivity(i);
                    scanName=" ";

                }
            });


            builder.setMessage(scanResult);
            AlertDialog alert=builder.create();
            alert.show();
        }

    }

    //used to build the JumiaUrl link
    public void buildJumiaUrl(){
        jumiaUrl="https://www.jumia.co.ke/catalog/?q=";
        jumiaUrl+=buildUrlEnd();
    }

    //used to build the Kilimal Url link
    public void buildKilimallUrl(){
        kilimallUrl="https://www.kilimall.co.ke/?act=search&keyword=";
        kilimallUrl+=buildUrlEnd();
    }

    //used to build the masoko Url link
    public void buildMasokoUrl(){
        MasokoUrl="https://www.masoko.com/catalogsearch/result/index/?product_list_dir=asc&product_list_order=price&q=";
        MasokoUrl+=buildUrlEnd();
    }

    //build last part of url
    public String buildUrlEnd(){
        String s;
        if (!scanName.trim().isEmpty() && scanName !=null){
            s=scanName;
        }else{
            s=scanResult;
        }
        s=s.replace(" ","+");
        return s;
    }

    //build barcode url
    public void BarcodeUrl(){
        barcodeUrl="https://barcode-list.com/barcode/EN/Search.htm?barcode=";
        googleUrl="https://www.google.com/search?q=";
        String s=scanResult;
        googleUrl+=s;
        barcodeUrl+=s;
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new ScannerAsyncLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
           if (data!=null) {
               scanName = data;
               if (progressDialog!=null) {
                   progressDialog.dismiss();
               }
               AlertDialog.Builder builder=new AlertDialog.Builder(this);
               builder.setTitle("Scan Result");

               builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       scannerView.resumeCameraPreview(Scanner.this);
                       scanName=" ";
                   }
               });

               builder.setNeutralButton("Search", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       finish();
                       buildJumiaUrl();
                       buildKilimallUrl();
                       buildMasokoUrl();

                       Intent i =new Intent(Scanner.this,ProductList.class);
                       Search.recentSearch.add(new Recent(scanName,R.drawable.ic_barcode,R.drawable.ic_close));
                       editSearch.setText(scanName);
                       i.putExtra("JumiaUrl",jumiaUrl);
                       i.putExtra("kilimallUrl",kilimallUrl);
                       i.putExtra("MasokoUrl",MasokoUrl);
                       i.putExtra("ProductName",scanName);
                       startActivity(i);
                       scanName=" ";
                   }
               });


               builder.setMessage("Product Name:"+scanName+"\n\n Barcode:"+scanResult);
               AlertDialog alert=builder.create();
               alert.show();
           }else {
               scanName="Something went wrong Try again";
               AlertDialog.Builder builder=new AlertDialog.Builder(this);
               builder.setTitle("Scan Result");

               builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       scannerView.resumeCameraPreview(Scanner.this);
                       scanName=" ";
                   }
               });

               builder.setNeutralButton("Search", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       finish();
                       buildJumiaUrl();
                       buildKilimallUrl();
                       buildMasokoUrl();

                       Intent i =new Intent(Scanner.this,ProductList.class);

                       Search.recentSearch.add(new Recent(scanName,R.drawable.ic_barcode,R.drawable.ic_close));

                       editSearch.setText(scanName);
                       i.putExtra("JumiaUrl",jumiaUrl);
                       i.putExtra("kilimallUrl",kilimallUrl);
                       i.putExtra("MasokoUrl",MasokoUrl);
                       i.putExtra("ProductName",scanName);
                       startActivity(i);
                       scanName=" ";

                   }
               });


               builder.setMessage(scanName+"\n"+scanResult);
               AlertDialog alert=builder.create();
               alert.show();
           }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private static class ScannerAsyncLoader extends AsyncTaskLoader<String> {

        private String produ;

        ScannerAsyncLoader(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            if (produ != null) {
                // Use cached data
                deliverResult(produ);
            }else{
                forceLoad();
            }
        }

        @Override
        public void deliverResult(String data) {
            super.deliverResult(data);

            // We’ll save the data for later retrieval
            produ = data;
        }


        @Nullable
        @Override
        public String loadInBackground() {

            return ScannerQuery.getBarcodeName(googleUrl,barcodeUrl);
        }
    }

}
