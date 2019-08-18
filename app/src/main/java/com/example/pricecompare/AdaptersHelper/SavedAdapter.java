package com.example.pricecompare.AdaptersHelper;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pricecompare.Products;
import com.example.pricecompare.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.GridViewHolder>{


    private GridLayoutManager mLayoutManager;
    ArrayList<Products> products;

    private SavedAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onShareClick(int position);
        void onDeleteClick(int position);

    }

    public void setOnItemClickListener(SavedAdapter.OnItemClickListener listener){
        mListener=listener;
    }


    class GridViewHolder extends RecyclerView.ViewHolder{
        public TextView productDescrption;
        public TextView NewPrice;
        public TextView OldPrice;
        public ImageView img;
        public ProgressBar progressBar;
        public ImageView imgLogo;
        public ImageView share;
        public ImageView delete;

        TextView discount;

        public GridViewHolder(@NonNull View itemView, final SavedAdapter.OnItemClickListener listener, int viewType) {
            super(itemView);

            productDescrption=itemView.findViewById(R.id.save_product_description);
            NewPrice=itemView.findViewById(R.id.save_new_price);
            OldPrice=itemView.findViewById(R.id.save_old_price);
            progressBar=itemView.findViewById(R.id.save_image_progress);
            img=itemView.findViewById(R.id.save_product_image);
            imgLogo=itemView.findViewById(R.id.save_website_logo);
            share=itemView.findViewById(R.id.save_share);
            discount=itemView.findViewById(R.id.save_discount);
            delete=itemView.findViewById(R.id.save_delete);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }

                }
            });

            //click listener for share
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onShareClick(position);
                        }
                    }

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

        }
    }


    public SavedAdapter(ArrayList<Products> prod,GridLayoutManager gridLayoutManager){
        products=prod;
        mLayoutManager=gridLayoutManager;

    }


    @NonNull
    @Override
    public SavedAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_list,parent,false);

        return new SavedAdapter.GridViewHolder(v,mListener,viewType);
    }

    @Override
    public void onBindViewHolder(final SavedAdapter.GridViewHolder holder, int position) {

        Products currentProduct=products.get(position);
        holder.OldPrice.setText(currentProduct.getPriceOld());
        holder.OldPrice.setPaintFlags(holder.OldPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        holder.discount.setText(currentProduct.getDiscountPercentage());
        holder.NewPrice.setText(currentProduct.getPriceNew());
        holder.productDescrption.setText(currentProduct.getProductDescription());

        Picasso.get().load(currentProduct.getImageProduct()).into(holder.img, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        Picasso.get().load(currentProduct.getImageLogo()).into(holder.imgLogo);

    }

    @Override
    public int getItemViewType(int position) {
       return 1;
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

}
