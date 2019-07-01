package com.example.pricecompare;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static String jumiaUrl;
    static String kilimallUrl;
    static String MasokoUrl;
    EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSearch=  findViewById(R.id.product_name);
        editSearch.setOnEditorActionListener(onEditorActionListener);

        Button button=findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editSearch.getText().toString().trim().isEmpty()){
                buildJumiaUrl();
                buildKilimallUrl();
                buildMasokoUrl();
                Intent intent=new Intent(MainActivity.this,ProductList.class);
                intent.putExtra("JumiaUrl",jumiaUrl);
                intent.putExtra("kilimallUrl",kilimallUrl);
                intent.putExtra("MasokoUrl",MasokoUrl);
                startActivity(intent);
            }else {
                 Toast.makeText(MainActivity.this,"Search can not be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                    Intent i =new Intent(MainActivity.this,ProductList.class);
                    i.putExtra("JumiaUrl",jumiaUrl);
                    i.putExtra("kilimallUrl",kilimallUrl);
                    i.putExtra("MasokoUrl",MasokoUrl);
                    startActivity(i);
            }else {
                Toast.makeText(MainActivity.this,"Search can not be empty",Toast.LENGTH_SHORT).show();
            }
                    break;
            }
            return false;
        }
    };

    //used to build the JumiaUrl link
    public void buildJumiaUrl(){
      jumiaUrl="https://www.jumia.co.ke/catalog/?q=";
        String s=editSearch.getText().toString().trim();
        s=s.replace(" ","+");
        jumiaUrl+=s;
    }

    //used to build the Kilimal Url link
    public void buildKilimallUrl(){
        kilimallUrl="https://www.kilimall.co.ke/?act=search&keyword=";
       String s=editSearch.getText().toString().trim();
        s=s.replace(" ","+");
        kilimallUrl+=s;
    }

    //used to build the masoko Url link
    public void buildMasokoUrl(){
        MasokoUrl="https://www.masoko.com/catalogsearch/result/index/?product_list_dir=asc&product_list_order=price&q=";
        String s=editSearch.getText().toString().trim();
        s=s.replace(" ","+");
        MasokoUrl+=s;
    }

}
