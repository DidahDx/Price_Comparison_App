package com.example.pricecompare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class Scanner extends AppCompatActivity implements ZBarScannerView.ResultHandler{

    private ZBarScannerView scannerView;
    static String jumiaUrl;
    static String kilimallUrl;
    static String MasokoUrl;
    String scanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView=new ZBarScannerView(this);
        scannerView.setAutoFocus(true);
        setContentView(scannerView);
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
//     scanResult=result.getText();
     scanResult=result.getContents();

    AlertDialog.Builder builder=new AlertDialog.Builder(this);
    builder.setTitle("Scan Result");

    builder.setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            scannerView.resumeCameraPreview(Scanner.this);
        }
    });

    builder.setNeutralButton("Search", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

//    Intent intent=new Intent(Intent.ACTION_VIEW , Uri.parse(scanResult));
//    startActivity(intent);


            buildJumiaUrl();
            buildKilimallUrl();
            buildMasokoUrl();
            Intent i =new Intent(Scanner.this,ProductList.class);
            i.putExtra("JumiaUrl",jumiaUrl);
            i.putExtra("kilimallUrl",kilimallUrl);
            i.putExtra("MasokoUrl",MasokoUrl);
            startActivity(i);
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


}
