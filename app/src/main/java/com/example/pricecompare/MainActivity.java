package com.example.pricecompare;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editSearch=  findViewById(R.id.product_name);
        editSearch.setOnEditorActionListener(onEditorActionListener);

        Button button=findViewById(R.id.search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ProductList.class);
                startActivity(intent);
            }
        });

    }

    //method used to handle enter key event for search
    private TextView.OnEditorActionListener onEditorActionListener=new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            switch(actionId){
                case EditorInfo.IME_ACTION_SEARCH:
                    Intent i =new Intent(MainActivity.this,ProductList.class);
                    startActivity(i);
                    break;
            }
            return false;
        }
    };

    //used to build the jumia url link
    public void buildJumiaUrl(){

    }

    //used to build the Kilimal url link
    public void buildKilimallUrl(){

    }

    //used to build the masoko url link
    public void buildMasokoUrl(){

    }

}
