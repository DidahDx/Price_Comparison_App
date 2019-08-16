package com.example.pricecompare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pricecompare.AdaptersHelper.SavedAdapter;
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

    String TAG=SavedProducts.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_products);
        firebaseAuth=FirebaseAuth.getInstance();

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
                if (product!=null){

                    product.clear();
                }

                for (DataSnapshot productSnapshot:dataSnapshot.getChildren()){
                    Products pro=productSnapshot.getValue(Products.class);
                    product.add(pro);
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

                Intent i=new Intent(SavedProducts.this,webView.class);
                i.putExtra("UrlWebLink",url);
                startActivity(i);
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
