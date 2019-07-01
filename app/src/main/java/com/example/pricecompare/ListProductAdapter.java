package com.example.pricecompare;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListProductAdapter extends ArrayAdapter<Products> {


    public ListProductAdapter(@NonNull Activity context, @NonNull ArrayList<Products> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listview=convertView;
        if (listview==null){
            listview= LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        Products currentProduct=getItem(position);

        TextView productDescrption=listview.findViewById(R.id.product_description);
        productDescrption.setText(currentProduct.getProductDescription());

        TextView NewPrice=listview.findViewById(R.id.new_price);
        NewPrice.setText(currentProduct.getPriceNew());

        TextView OldPrice=listview.findViewById(R.id.old_price);
        OldPrice.setText(currentProduct.getPriceOld());

        ImageView img= listview.findViewById(R.id.product_image);
        final ProgressBar progressBar=listview.findViewById(R.id.image_progress);

        String url =currentProduct.getImageProduct();
        Picasso.get().load(url).into(img, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });



        ImageView imgLogo=listview.findViewById(R.id.website_logo);
        String urlLogo=currentProduct.getImageLogo();
        Picasso.get().load(urlLogo).into(imgLogo);

        return listview;
    }
}
