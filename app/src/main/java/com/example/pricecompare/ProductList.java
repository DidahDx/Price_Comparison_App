package com.example.pricecompare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pricecompare.AdaptersHelper.RecycleGridAdapter;
import com.example.pricecompare.WebScraper.QueryUtil;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static com.example.pricecompare.AdaptersHelper.RecycleGridAdapter.SPAN_COUNT_ONE;
import static com.example.pricecompare.AdaptersHelper.RecycleGridAdapter.SPAN_COUNT_TWO;

public class ProductList extends AppCompatActivity  implements  LoaderManager.LoaderCallbacks<ArrayList<Products>> {

    private int currentViewMode=1;
    ArrayList<Products> product;

    ProgressBar progressBar1;
    Button tryAgain;
    ImageButton switchLayout;
    int alreadySearched=0;
    RelativeLayout relativeLayout;

    //url links set to blank
    static String JumiaUrl ="";
    static String kilimallUrl="";
    static String MasokoUrl="";
    static String[] filter_words;

    TextView emptyState;
    RecyclerView gridRecyclerView;
    private RecycleGridAdapter gridAdapter;
    private GridLayoutManager gridlayoutManager;

    private FirebaseAnalytics mFirebaseAnalytics;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private final int PRODUCT_LOADER_ID=100;
    private static Bundle mBundleRecyclerViewState;
    Parcelable listState;
    Toolbar toolbar;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String TAG=ProductList.class.getSimpleName();
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
        currentViewMode=sharedPreferences.getInt("currentViewMode",currentViewMode);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //firebase database reference

        databaseReference= FirebaseDatabase.getInstance().getReference("SavedProducts");
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        toolbar =findViewById(R.id.product_toolbar);

        setSupportActionBar(toolbar);


        progressBar1=findViewById(R.id.progress_circular);
        emptyState=findViewById(R.id.empty_state);
        tryAgain=findViewById(R.id.try_again);
        switchLayout=findViewById(R.id.layout_switcher);
        relativeLayout=findViewById(R.id.container_switcher);
        relativeLayout.setVisibility(View.GONE);
        Spinner mySpinner=findViewById(R.id.sort_product);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String selectedText = null;
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedText = parentView.getItemAtPosition(position).toString();
                switch (selectedText){
                    case "Low to High Price":
                        sortPriceOrder(0);
                        gridAdapter.notifyDataSetChanged();
                        break;

                    case "High to Low Price":
                        sortPriceOrder(1);
                        gridAdapter.notifyDataSetChanged();
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //setting spinner input
        ArrayAdapter<String> sortAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.sort));
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mySpinner.setAdapter(sortAdapter);


        //used to switch the layout
        switchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SPAN_COUNT_ONE==currentViewMode){
                    currentViewMode=SPAN_COUNT_TWO;
                    gridlayoutManager.setSpanCount(currentViewMode);
                }else{
                    currentViewMode=SPAN_COUNT_ONE;
                    gridlayoutManager.setSpanCount(currentViewMode);
                }


                switchIcon(switchLayout);
                //switch the view
                SwitchLayout();

                //save the current view mode
                SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("currentViewMode",currentViewMode);
                editor.apply();
            }
        });

        //getting urls links for websites
        Bundle bundle=getIntent().getExtras();
        JumiaUrl =bundle.getString("JumiaUrl");
        kilimallUrl=bundle.getString("kilimallUrl");
        MasokoUrl=bundle.getString("MasokoUrl");
        toolbar.setTitle(bundle.getString("ProductName"));



        gridRecyclerView=findViewById(R.id.rv);
        gridlayoutManager=new GridLayoutManager(this,currentViewMode);

        //retry when the network fails
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checking network before reloading
                ConnectivityManager conManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=conManager.getActiveNetworkInfo();
                if (networkInfo !=null && networkInfo.isConnected()) {
                    progressBar1.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                    tryAgain.setVisibility(View.GONE);
                    getSupportLoaderManager().restartLoader(PRODUCT_LOADER_ID, null,  ProductList.this);
                }else{
                    emptyState.setText(getString(R.string.no_network));
                    emptyState.setVisibility(View.VISIBLE);
                    progressBar1.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        });



        //used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //checking network connectivity
        ConnectivityManager conManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=conManager.getActiveNetworkInfo();
        if (networkInfo !=null && networkInfo.isConnected()){

            if(alreadySearched==0) {
                progressBar1.setVisibility(View.VISIBLE);
                getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
                alreadySearched=1;
            }
        }else{
            progressBar1.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyState.setText(getString(R.string.no_network));
            tryAgain.setVisibility(View.VISIBLE);
        }

        //used to switch the display icon
        switchIcon(switchLayout);

    }



    //saving instances for before rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("alreadySearch",alreadySearched);
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();


         listState = gridlayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_STATE, listState);

    }

    //restoring instances after rotation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        alreadySearched=savedInstanceState.getInt("alreadySearch");
        // restore RecyclerView state
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        listState=null;
    }

    @Override
    protected void onResume() {
        super.onResume();
      if (listState!=null) {
          gridlayoutManager.onRestoreInstanceState(listState);
      }
    }


    //used to set the Adapter
    private void setAdapter() {

            gridAdapter=new RecycleGridAdapter(product, gridlayoutManager);
            gridRecyclerView.setLayoutManager(gridlayoutManager);
            gridRecyclerView.setAdapter(gridAdapter);

            gridAdapter.setOnItemClickListener(new RecycleGridAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Products pro=product.get(position);
                    String url=pro.getUrlLink();

                    try {
                        Intent webBrowser = new Intent(Intent.ACTION_VIEW);
                        webBrowser.setData(Uri.parse(url));
                        startActivity(webBrowser);
                    }catch (Exception e){
                        Toast.makeText(ProductList.this, "Browser not Found ", Toast.LENGTH_LONG).show();
                        //internal web browser
                        Intent i=new Intent(ProductList.this,webView.class);
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
                    Toast.makeText(ProductList.this,"Sharing "+position,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSaveClick(final int position) {
                    Products pro=product.get(position);
                    firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser!=null){


                        if (!pro.isImageChanged()){
                            //used to save a product
                            String userid =firebaseUser.getUid();
                            String id=databaseReference.push().getKey();

                            Products products=new Products(pro.getProductDescription(),pro.getPriceOld(),pro.getImageProduct()
                                    ,pro.getUrlLink(),pro.getImageLogo(),pro.getPriceNew(),pro.getDiscountPercentage());

                            assert id != null;
                            databaseReference.child(userid).child(id).setValue(products);
                            Toast.makeText(ProductList.this,"Saved "+position,Toast.LENGTH_SHORT).show();
                            gridAdapter.changeImage(position);
                        }else {
                            //used to delete a saved product
                            DatabaseReference ref =FirebaseDatabase.getInstance().getReference("SavedProducts").child(firebaseUser.getUid());
                            Query deleteQuery = ref.orderByChild("urlLink").equalTo(pro.getUrlLink());

                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot deleteSnapShot : dataSnapshot.getChildren()){
                                        deleteSnapShot.getRef().removeValue();
                                        Toast.makeText(ProductList.this, "Item removed "+position, Toast.LENGTH_SHORT).show();
                                        gridAdapter.removeImage(position);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                }
                            });
                        }

                    }else {
                        startActivity(new Intent(ProductList.this,LoginPage.class));
                    }

                }
            });

    }

    //setting the menu fr toolbar option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    //listener when an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

                //back button used to close this activity
            case android.R.id.home:
                mBundleRecyclerViewState=null;
                finish();
                break;

            case R.id.saved_items:
                firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser!=null){
                    startActivity(new Intent(ProductList.this,SavedProducts.class));
                }else {
                    startActivity(new Intent(ProductList.this,LoginPage.class));

                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    //used to switch the layout
    private void SwitchLayout() {
        gridlayoutManager.setSpanCount(currentViewMode);
        gridAdapter.notifyItemRangeChanged(0, gridAdapter.getItemCount());
    }


    //used to switch the icons
    private void switchIcon(ImageButton item) {
        if (gridlayoutManager.getSpanCount() == SPAN_COUNT_TWO) {
            item.setBackground(getResources().getDrawable(R.drawable.ic_grid_icon));
        } else {
            item.setBackground(getResources().getDrawable(R.drawable.ic_list_icon));
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<Products>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ProductAsyncLoader(ProductList.this);
    }

    //called when the background thread has finished loading
    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Products>> loader, ArrayList<Products> data) {
        progressBar1.setVisibility(View.GONE);

        if (data!=null){
            UpdateUi(data);
            relativeLayout.setVisibility(View.VISIBLE);

            //when there is an error loading data
            if ( data.size() <= 0) {
                emptyState.setText(getString(R.string.no_data));
                emptyState.setVisibility(View.VISIBLE);
                tryAgain.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.GONE);
            }
        }else {
            emptyState.setText(getString(R.string.load_error));
            emptyState.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Products>> loader) {
        UpdateUi(null);
    }

    private static class ProductAsyncLoader extends AsyncTaskLoader<ArrayList<Products>> {

        private ArrayList<Products> produ;

        ProductAsyncLoader(@NonNull Context context) {
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
        public void deliverResult(ArrayList<Products> data) {
            super.deliverResult(data);

            // We’ll save the data for later retrieval
            produ = data;
        }


        @Nullable
        @Override
        public ArrayList<Products> loadInBackground() {
            ArrayList<Products>  prod= (ArrayList<Products>) QueryUtil.fetchWebsiteData(JumiaUrl,kilimallUrl,MasokoUrl);


            //do not sort when there is no data
            if(prod!=null) {

                //    used in sorting
                Collections.sort(prod, new Comparator<Products>() {
                    @Override
                    public int compare(Products o1, Products o2) {
                        String p1 = o1.getPriceNew().trim();
                        String p2 = o2.getPriceNew().trim();

                        //removing unwanted KSH , KES  and  ranges between prices, before sorting
                        p1 = p1.replace("KSh", "");
                        p1 = p1.replace("KES", "");
                        p1 = p1.replace(",", "");
                        if (p1.indexOf('-') != -1) {
                            p1 = p1.replace(p1.substring(p1.indexOf('-') + 1), "");
                            p1 = p1.replace("-", "");
                        }
                        p1 = p1.trim();

                        p2 = p2.replace("KSh", "");
                        p2 = p2.replace(",", "");
                        p2 = p2.replace("KES", "");
                        if (p2.indexOf('-') != -1) {
                            p2 = p2.replace(p2.substring(p2.indexOf('-') + 1), "");
                            p2 = p2.replace("-", "");
                        }
                        p2 = p2.trim();

                        return Integer.valueOf(p1) - (Integer.valueOf(p2));
                    }
                });

            }
            produ=prod;

            return prod;
        }
    }

    //used to update the xml layouts
    private void UpdateUi(ArrayList<Products> data) {
        product=data;
        setAdapter();
    }



    public void sortPriceOrder(final int order){

        progressBar1.setVisibility(View.VISIBLE);

        if(product!=null) {

            //    used in sorting
            Collections.sort(product, new Comparator<Products>() {
                @Override
                public int compare(Products o1, Products o2) {
                    String p1 = o1.getPriceNew().trim();
                    String p2 = o2.getPriceNew().trim();

                    //removing unwanted KSH , KES  and  ranges between prices, before sorting
                    p1 = p1.replace("KSh", "");
                    p1 = p1.replace("KES", "");
                    p1 = p1.replace(",", "");
                    if (p1.indexOf('-') != -1) {
                        p1 = p1.replace(p1.substring(p1.indexOf('-') + 1), "");
                        p1 = p1.replace("-", "");
                    }
                    p1 = p1.trim();

                    p2 = p2.replace("KSh", "");
                    p2 = p2.replace(",", "");
                    p2 = p2.replace("KES", "");
                    if (p2.indexOf('-') != -1) {
                        p2 = p2.replace(p2.substring(p2.indexOf('-') + 1), "");
                        p2 = p2.replace("-", "");
                    }
                    p2 = p2.trim();

                    switch (order){
                        case 0:
                            return Integer.valueOf(p1) - (Integer.valueOf(p2));
                        case 1:
                             return Integer.valueOf(p2) - (Integer.valueOf(p1));
                        default:
                                return Integer.valueOf(p1) - (Integer.valueOf(p2));

                    }
                }
            });

    }
        progressBar1.setVisibility(View.GONE);
    }


}