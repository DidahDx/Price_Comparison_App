package com.example.pricecompare;

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

public class GridProductAdapter extends ArrayAdapter<Products> {


    public GridProductAdapter(@NonNull Context context,  @NonNull ArrayList<Products> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listview=convertView;
        if (listview==null){
            listview= LayoutInflater.from(getContext()).inflate(R.layout.grid_item,parent,false);
        }

        Products currentProduct=getItem(position);

        TextView productDescrption=listview.findViewById(R.id.product_description);
        productDescrption.setText(currentProduct.getProductDescription());



        TextView NewPrice=listview.findViewById(R.id.new_price);
        NewPrice.setText(currentProduct.getPriceNew());

        TextView OldPrice=listview.findViewById(R.id.old_price);
        OldPrice.setText(currentProduct.getPriceOld());

        ImageView img=listview.findViewById(R.id.product_image);

        String url =currentProduct.getImageProduct();
        Picasso.get().load(url).into(img);

        ImageView imgLogo=listview.findViewById(R.id.website_logo);
        String urlLogo=currentProduct.getImageLogo();
        Picasso.get().load(urlLogo).into(imgLogo);

        return listview;
    }

}
