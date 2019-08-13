package com.example.pricecompare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.Result;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Search extends AppCompatActivity {

    static EditText editSearch;
    ImageButton voiceSearch;
    ImageButton barcodeSearch;
    static String jumiaUrl;
    static String kilimallUrl;
    static String MasokoUrl;
    private static final int RECOGNIZER_RESULT=1;


    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Obtain the Firebase Analytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Toolbar toolbar=findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Material search");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        //used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editSearch=findViewById(R.id.search_product);
        editSearch.setOnEditorActionListener(onEditorActionListener);
        voiceSearch=findViewById(R.id.voice_search);
        barcodeSearch=findViewById(R.id.barcode_search);

        barcodeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, Scanner.class);
                startActivity(intent);
            }
        });

        //used for voice
        voiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //connecting to google api
                Intent speechIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                //which speech model to prefer/use
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                //displayed to user
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice Search");
                try {
                    startActivityForResult(speechIntent, RECOGNIZER_RESULT);
                }catch (Exception e){
                    Toast.makeText(Search.this, "Something went wrong starting voice search try again", Toast.LENGTH_LONG).show();
                }
            }
        });



    }


    //results for voice search
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==RECOGNIZER_RESULT && resultCode==RESULT_OK && data!=null){
            ArrayList<String> matches=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editSearch.setText(matches.get(0).toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
//         searchView.setMenuItem(item);
         return true;
    }



    //listener when an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            //used to search for products
            case R.id.action_search:
                if(!editSearch.getText().toString().trim().isEmpty()){
                    buildJumiaUrl();
                    buildKilimallUrl();
                    buildMasokoUrl();

                    Intent intent=new Intent(Search.this,ProductList.class);
                    intent.putExtra("JumiaUrl",jumiaUrl);
                    intent.putExtra("kilimallUrl",kilimallUrl);
                    intent.putExtra("MasokoUrl",MasokoUrl);
                    intent.putExtra("ProductName",editSearch.getText().toString());
                    startActivity(intent);
                }else {
                    Toast.makeText(Search.this,"Search can not be empty",Toast.LENGTH_SHORT).show();
                }
                break;

            //back button used to close this activity
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //method used to handle enter key event for search
    private TextView.OnEditorActionListener onEditorActionListener=new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            switch(actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    if(!editSearch.getText().toString().trim().isEmpty()){
                        buildJumiaUrl();
                        buildKilimallUrl();
                        buildMasokoUrl();
                        Intent i =new Intent(Search.this,ProductList.class);
                        i.putExtra("JumiaUrl",jumiaUrl);
                        i.putExtra("kilimallUrl",kilimallUrl);
                        i.putExtra("MasokoUrl",MasokoUrl);
                        i.putExtra("ProductName",editSearch.getText().toString());
                        startActivity(i);
                    }else {
                        Toast.makeText(Search.this,"Search can not be empty",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return false;
        }
    };

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
        String s=editSearch.getText().toString().trim();
        s=s.replace(" ","+");
        return s;
    }

}
