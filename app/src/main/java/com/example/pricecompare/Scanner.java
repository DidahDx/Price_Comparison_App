package com.example.pricecompare;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler, LoaderManager.LoaderCallbacks<String>{

    private static final int REQUEST_CAMERA=100;
    private ZXingScannerView scannerView;
    static String jumiaUrl;
    static String kilimallUrl;
    static String MasokoUrl;
    String scanResult;
    String barcodeUrl="https://barcode-list.com/barcode/EN/Search.htm?barcode=";
    String googleUrl="https://www.google.com/search?q=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

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
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }




    @Override
    public void handleResult(Result result) {
        scanResult=result.getText();

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");

        builder.setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(Scanner.this);
            }
        });

        builder.setNeutralButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (scanResult.matches("[0-9]")){

                    ProgressBar progressBar=new ProgressBar(Scanner.this);
                    progressBar.setIndeterminate(true);
                    progressBar.setVisibility(View.VISIBLE);

                    BarcodeUrl();
                    finish();
                    buildJumiaUrl();
                    buildKilimallUrl();
                    buildMasokoUrl();
                    progressBar.setVisibility(View.GONE);
                    Intent i =new Intent(Scanner.this,ProductList.class);
                    i.putExtra("JumiaUrl",jumiaUrl);
                    i.putExtra("kilimallUrl",kilimallUrl);
                    i.putExtra("MasokoUrl",MasokoUrl);
                    startActivity(i);
                }else{
                    finish();
                    buildJumiaUrl();
                    buildKilimallUrl();
                    buildMasokoUrl();

                    Intent i =new Intent(Scanner.this,ProductList.class);
                    i.putExtra("JumiaUrl",jumiaUrl);
                    i.putExtra("kilimallUrl",kilimallUrl);
                    i.putExtra("MasokoUrl",MasokoUrl);
                    startActivity(i);
                }
            }
        });


        builder.setMessage(scanResult);
        AlertDialog alert=builder.create();
        alert.show();
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
        String s=scanResult;
        s=s.replace(" ","+");
        return s;
    }

    //build barcode url
    public void BarcodeUrl(){
        String s=scanResult;
        googleUrl+=s;
        barcodeUrl+=s;
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
