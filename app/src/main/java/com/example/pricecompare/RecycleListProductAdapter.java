package com.example.pricecompare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecycleListProductAdapter extends RecyclerView.Adapter<RecycleListProductAdapter.ListViewHolder>{

    ArrayList<Products> products;


    public static class ListViewHolder extends RecyclerView.ViewHolder{
       public TextView productDescrption;
        public TextView NewPrice;
        public TextView OldPrice;
        public ImageView img;
        public ProgressBar progressBar;
        public ImageView imgLogo;
       public ImageView share;
        public ImageView save;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            productDescrption=itemView.findViewById(R.id.product_description);
            NewPrice=itemView.findViewById(R.id.new_price);
            OldPrice=itemView.findViewById(R.id.old_price);
            progressBar=itemView.findViewById(R.id.image_progress);
            img=itemView.findViewById(R.id.product_image);
            imgLogo=itemView.findViewById(R.id.website_logo);
            share=itemView.findViewById(R.id.share);
            save=itemView.findViewById(R.id.save);

        }
    }


    public RecycleListProductAdapter(ArrayList<Products> prod){

        products=prod;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
      ListViewHolder listViewHolder=new ListViewHolder(v);

      return listViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        Products currentProduct=products.get(position);
        holder.OldPrice.setText(currentProduct.getPriceOld());
        holder.NewPrice.setText(currentProduct.getPriceNew());
        holder.productDescrption.setText(currentProduct.getProductDescription());

        Picasso.get().load(currentProduct.getImageProduct()).into(holder.img);
        Picasso.get().load(currentProduct.getImageLogo()).into(holder.imgLogo);

    }



    @Override
    public int getItemCount() {
        return products.size();
    }
}
