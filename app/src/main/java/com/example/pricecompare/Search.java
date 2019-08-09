package com.example.pricecompare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.Result;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Search extends AppCompatActivity {

    String [] listSource={"Samsung","Tecno"};
    MaterialSearchView searchView;


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

     searchView=findViewById(R.id.searchView);
     searchView.closeSearch();
     searchView.setSuggestions(listSource);
     searchView.setVoiceSearch(true);

     searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
         @Override
         public void onSearchViewShown() {

             searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                 @Override
                 public boolean onQueryTextSubmit(String query) {

                     Toast.makeText(getApplicationContext(),"Submitted query",Toast.LENGTH_SHORT).show();
                     return true;
                 }

                 @Override
                 public boolean onQueryTextChange(String newText) {
                     return false;
                 }
             });
         }

         @Override
         public void onSearchViewClosed() {

         }
     });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
         searchView.setMenuItem(item);
         return true;
    }

}
