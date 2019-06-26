package com.example.pricecompare;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListProductAdapter extends ArrayAdapter<Products> {
    Activity context;

    public ListProductAdapter(@NonNull Activity context, @NonNull ArrayList<Products> objects) {
        super(context, 0, objects);
        this.context=context;
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

        TextView price=listview.findViewById(R.id.product_price);
        price.setText(currentProduct.getPrice());

        ImageView img=(ImageView) listview.findViewById(R.id.product_image);

        String url =currentProduct.getImageProduct();
        Picasso.get().load(url).into(img);

        ImageView imgLogo=listview.findViewById(R.id.website_logo);
        String urlLogo=currentProduct.getImageLogo();
        Picasso.get().load(urlLogo).into(imgLogo);

        return listview;
    }
}
