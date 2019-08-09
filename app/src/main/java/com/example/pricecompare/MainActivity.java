package com.example.pricecompare;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static String jumiaUrl;
    static String kilimallUrl;
    static String MasokoUrl;
   static EditText editSearch;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the Firebase Analytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        editSearch=  findViewById(R.id.product_name);
        editSearch.setOnEditorActionListener(onEditorActionListener);

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);



        Button button=findViewById(R.id.search);
        Button barcode=findViewById(R.id.barcode);
        Button customSearch=findViewById(R.id.custom_search);


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
                intent.putExtra("ProductName",editSearch.getText().toString());
                startActivity(intent);
            }else {
                    Toast.makeText(MainActivity.this,"the width of the screen is"+size.x,Toast.LENGTH_LONG).show();
                 Toast.makeText(MainActivity.this,"Search can not be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });


        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, Scanner.class);
                    startActivity(intent);

            }
        });

        customSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Search.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

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
                        i.putExtra("ProductName",editSearch.getText().toString());
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()){

            case R.id.nav_login:
                break;

            case R.id.nav_settings:
                break;

            case R.id.nav_share:
                break;

            case R.id.nav_saved:
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}