package com.example.pricecompare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pricecompare.AdaptersHelper.RecycleGridAdapter;
import com.example.pricecompare.AdaptersHelper.RecycleListAdapter;
import com.example.pricecompare.WebScraper.QueryUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProductList extends AppCompatActivity  implements  LoaderManager.LoaderCallbacks<ArrayList<Products>> {
    private ViewStub list_stub;
    private ViewStub grid_stub;

    private int currentViewMode=0;

    TextView emptyState;
    static final int VIEW_MODE_LISTVIEW=0;
    static final int VIEW_MODE_GRIDVIEW=1;
    ArrayList<Products> product;

    ProgressBar progressBar1;
    Button tryAgain;
    int alreadySearched=0;

    static String JumiaUrl ="";
    static String kilimallUrl="";
    static String MasokoUrl="";

    private RecyclerView recyclerView;
    private RecycleListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    RecyclerView gridRecyclerView;
    private RecycleGridAdapter gridAdapter;
    private RecyclerView.LayoutManager gridlayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        list_stub=findViewById(R.id.stub_list);
        grid_stub=findViewById(R.id.stub_grid);


        progressBar1=findViewById(R.id.progress_circular);
        emptyState=findViewById(R.id.empty_state);
        tryAgain=findViewById(R.id.try_again);

        //getting urls links for websites
        Bundle bundle=getIntent().getExtras();
        JumiaUrl =bundle.getString("JumiaUrl");
        kilimallUrl=bundle.getString("kilimallUrl");
        MasokoUrl=bundle.getString("MasokoUrl");

        list_stub.inflate();
        grid_stub.inflate();


        recyclerView=findViewById(R.id.recycle_list);
        layoutManager=new LinearLayoutManager(this);

        gridRecyclerView=findViewById(R.id.recycle_grid);
        gridlayoutManager=new GridLayoutManager(this,2);



        //retry when the network fails
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checking network before reloading
                ConnectivityManager conManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=conManager.getActiveNetworkInfo();
                if (networkInfo !=null && networkInfo.isConnected()) {
                    getSupportLoaderManager().restartLoader(100, null,  ProductList.this);
                    emptyState.setVisibility(View.GONE);
                    progressBar1.setVisibility(View.VISIBLE);
                    tryAgain.setVisibility(View.GONE);
                }else{
                    emptyState.setText(getString(R.string.no_network));
                    emptyState.setVisibility(View.VISIBLE);
                    progressBar1.setVisibility(View.GONE);
                }
            }
        });

        //check the default view saved in the shared preference
        SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
        currentViewMode=sharedPreferences.getInt("currentViewMode",VIEW_MODE_LISTVIEW);

        //used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //checking network connectivity
        ConnectivityManager conManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=conManager.getActiveNetworkInfo();
        if (networkInfo !=null && networkInfo.isConnected()){

            if(alreadySearched==0) {
                getSupportLoaderManager().initLoader(100, null, this);
                alreadySearched=1;
            }
        }else{
            progressBar1.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            emptyState.setText(getString(R.string.no_network));
            tryAgain.setVisibility(View.VISIBLE);
        }



    }

    //saving instances for before rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("alreadySearch",alreadySearched);
        super.onSaveInstanceState(outState);
    }

    //restoring instaces after rotation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        alreadySearched=savedInstanceState.getInt("alreadySearch");
        super.onRestoreInstanceState(savedInstanceState);
    }

    //used to switch the view
    private void switchView() {
        if (VIEW_MODE_LISTVIEW==currentViewMode){
            list_stub.setVisibility(View.VISIBLE);
            grid_stub.setVisibility(View.GONE);
        }else {
            list_stub.setVisibility(View.GONE);
            grid_stub.setVisibility(View.VISIBLE);
        }

        setAdapter();
    }

    //used to set the Adapter
    private void setAdapter() {
        if (VIEW_MODE_LISTVIEW==currentViewMode){
            adapter=new RecycleListAdapter(product);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new RecycleListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Products pro=product.get(position);
                    String url=pro.getUrlLink();

                    Intent i=new Intent(ProductList.this,webView.class);
                    i.putExtra("UrlWebLink",url);
                    startActivity(i);
                }

                @Override
                public void onShareClick(int position) {
                    Products pro=product.get(position);
                    String url=pro.getUrlLink();

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "I found this item in price Compare App \n"+url);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    Toast.makeText(ProductList.this,"the share postion "+position,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSaveClick(int position) {
                     product.get(position);
                    Toast.makeText(ProductList.this,"the postion "+position,Toast.LENGTH_SHORT).show();
                }
            });

//            listAdapter=new ListProductAdapter(this,product);
//            rootList.setAdapter(listAdapter);
        }else{

            gridAdapter=new RecycleGridAdapter(product);
            gridRecyclerView.setLayoutManager(gridlayoutManager);
            gridRecyclerView.setAdapter(gridAdapter);


            gridAdapter.setOnItemClickListener(new RecycleGridAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Products pro=product.get(position);
                    String url=pro.getUrlLink();

                    Intent i=new Intent(ProductList.this,webView.class);
                    i.putExtra("UrlWebLink",url);
                    startActivity(i);
                }

                @Override
                public void onShareClick(int position) {
                    Products pro=product.get(position);
                    String url=pro.getUrlLink();

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "I found this item in price Compare App \n"+url);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    Toast.makeText(ProductList.this,"the share postion "+position,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSaveClick(int position) {
                    product.get(position);
                    Toast.makeText(ProductList.this,"the postion "+position,Toast.LENGTH_SHORT).show();
                }
            });


//            gridAdapter=new GridProductAdapter(this,product);
//            rootGrid.setAdapter(gridAdapter);
        }
    }



    //setting the menu with the switch mode option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //listener when an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                if (VIEW_MODE_LISTVIEW==currentViewMode){
                    currentViewMode=VIEW_MODE_GRIDVIEW;
                }else{
                    currentViewMode=VIEW_MODE_LISTVIEW;
                }

                //switch the view
                switchView();

                //save the current view mode
                SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("currentViewMode",currentViewMode);
                editor.commit();
                break;

                //used to close this activity
            case R.id.search:
                finish();
                break;

                //back button used to close this activity
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
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
        }else {
            emptyState.setText(getString(R.string.no_data));
            emptyState.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
        }

        //when there is an error loading data

        if ( data.size() <= 0) {
            emptyState.setText(getString(R.string.load_error));
            emptyState.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
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
            // We’ll save the data for later retrieval
            produ = data;

            super.deliverResult(data);
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


                        //removing unwanted KSH , KES  and , before sorting
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
        switchView();
    }

}