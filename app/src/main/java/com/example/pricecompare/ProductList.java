package com.example.pricecompare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProductList extends AppCompatActivity {
    private ViewStub list_stub;
    private ViewStub grid_stub;
    GridView rootGrid;
    ListView rootList;
    GridProductAdapter gridAdapter;
    ListProductAdapter listAdapter;
    private int currentViewMode=0;

    static final int VIEW_MODE_LISTVIEW=0;
    static final int VIEW_MODE_GRIDVIEW=1;
    ArrayList<Products> product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        list_stub=findViewById(R.id.stub_list);
        grid_stub=findViewById(R.id.stub_grid);

        list_stub.inflate();
        grid_stub.inflate();

        rootList=findViewById(R.id.listView);
        rootGrid=findViewById(R.id.gridView);



        product=new ArrayList<Products>();

        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2134"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","21345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2135"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2145"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","21345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","21345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","21345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","1345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","21345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","21345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2135"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2145"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2135"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2134"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2135"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","21345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2145"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","2345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","1345"));
        product.add(new Products("Long placeholder for the  descrption of the product should not be too long","213f5"));


        SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
        currentViewMode=sharedPreferences.getInt("currentViewMode",VIEW_MODE_LISTVIEW);

        rootList.setOnItemClickListener(onItemClickListener);
        rootGrid.setOnItemClickListener(onItemClickListener);
        switchView();
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
            listAdapter=new ListProductAdapter(this,product);
            rootList.setAdapter(listAdapter);
        }else{
            gridAdapter=new GridProductAdapter(this,product);
            rootGrid.setAdapter(gridAdapter);
        }
    }

    //used to set item listener
    AdapterView.OnItemClickListener onItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(),product.get(position).getPrice(),Toast.LENGTH_SHORT).show();

        }
    };

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

                //save
                SharedPreferences sharedPreferences=getSharedPreferences("ViewMode",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("currentViewMode",currentViewMode);
                editor.commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
