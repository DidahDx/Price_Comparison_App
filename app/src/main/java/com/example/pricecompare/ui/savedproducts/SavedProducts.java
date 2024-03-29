package com.example.pricecompare.ui.savedproducts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pricecompare.R;
import com.example.pricecompare.data.model.Products;
import com.example.pricecompare.ui.WebView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedProducts extends AppCompatActivity {

    RecyclerView recyclerView;
    private SavedAdapter gridAdapter;
    private GridLayoutManager gridlayoutManager;
    private int currentViewMode=1;
    ArrayList<Products> product=new ArrayList<Products>();
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Toolbar toolbar;

    TextView noData;

    private FirebaseAnalytics mFirebaseAnalytics;
    String TAG=SavedProducts.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_products);
        firebaseAuth=FirebaseAuth.getInstance();

        // Obtain the Firebase Analytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        firebaseUser=firebaseAuth.getCurrentUser();
        if (firebaseUser !=null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("SavedProducts").child(firebaseUser.getUid());
        }else{
            finish();
            Toast.makeText(this, "Login to view Saved", Toast.LENGTH_SHORT).show();
        }

        toolbar=findViewById(R.id.saved_toolbar);
        setSupportActionBar(toolbar);
        //used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noData=findViewById(R.id.Hint);
        noData.setVisibility(View.GONE);
        recyclerView=findViewById(R.id.rv_saved);
        gridlayoutManager=new GridLayoutManager(this,currentViewMode);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (product != null) {
                    product.clear();

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Products pro = productSnapshot.getValue(Products.class);
                    product.add(pro);
                }

                if (product.size() == 0) {
                    noData.setVisibility(View.VISIBLE);
                }else {
                    noData.setVisibility(View.GONE);
                }
            }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //used to set the Adapter
    private void setAdapter() {

        gridAdapter=new SavedAdapter(product, gridlayoutManager);
        recyclerView.setLayoutManager(gridlayoutManager);

        recyclerView.setAdapter(gridAdapter);

        gridAdapter.setOnItemClickListener(new SavedAdapter.OnItemClickListener() {


            @Override
            public void onItemClick(int position) {
                Products pro=product.get(position);
                String url=pro.getUrlLink();

                try {
                    Intent webBrowser = new Intent(Intent.ACTION_VIEW);
                    webBrowser.setData(Uri.parse(url));
                    startActivity(webBrowser);
                }catch (Exception e){
                    Toast.makeText(SavedProducts.this, "Browser not Found ", Toast.LENGTH_LONG).show();
                    //internal web browser
                    Intent i=new Intent(SavedProducts.this, WebView.class);
                    i.putExtra("UrlWebLink",url);
                    startActivity(i);
                }

            }

            @Override
            public void onShareClick(int position) {
                Products pro=product.get(position);
                String url=pro.getUrlLink();

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I found this item in price Compare App \n"+url);
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
                Toast.makeText(SavedProducts.this,"Sharing "+position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                Products pro=product.get(position);

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference("SavedProducts").child(firebaseUser.getUid());
                Query deleteQuery = ref.orderByChild("urlLink").equalTo(pro.getUrlLink());

                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot deleteSnapShot : dataSnapshot.getChildren()){
                            deleteSnapShot.getRef().removeValue();
                            Toast.makeText(SavedProducts.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                        }

                        gridAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });

            }
        });


    }


}
