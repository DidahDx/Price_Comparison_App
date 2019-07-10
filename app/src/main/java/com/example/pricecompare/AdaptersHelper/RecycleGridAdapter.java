package com.example.pricecompare.AdaptersHelper;

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

public class RecycleGridAdapter extends RecyclerView.Adapter<RecycleGridAdapter.GridViewHolder>{

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_TWO = 2;

    private static final int LIST_VIEW = 1;
    private static final int GRID_VIEW = 2;

    private GridLayoutManager mLayoutManager;
    ArrayList<Products> products;

    private RecycleGridAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onShareClick(int position);
        void onSaveClick(int position);
    }

    public void setOnItemClickListener(RecycleGridAdapter.OnItemClickListener listener){
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
        public ImageView save;

        public GridViewHolder(@NonNull View itemView, final RecycleGridAdapter.OnItemClickListener listener, int viewType) {
            super(itemView);

            productDescrption=itemView.findViewById(R.id.product_description);
            NewPrice=itemView.findViewById(R.id.new_price);
            OldPrice=itemView.findViewById(R.id.old_price);
            progressBar=itemView.findViewById(R.id.image_progress);
            img=itemView.findViewById(R.id.product_image);
            imgLogo=itemView.findViewById(R.id.website_logo);
            share=itemView.findViewById(R.id.share);
            save=itemView.findViewById(R.id.save);


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

            //click listener for save
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listener.onSaveClick(position);
                        }
                    }
                }
            });

        }
    }


    public RecycleGridAdapter(ArrayList<Products> prod,GridLayoutManager gridLayoutManager){
        products=prod;
        mLayoutManager=gridLayoutManager;
    }


    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType==LIST_VIEW){
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        }else {
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item,parent,false);
        }

        return new RecycleGridAdapter.GridViewHolder(v,mListener,viewType);
    }

    @Override
    public void onBindViewHolder(final GridViewHolder holder, int position) {

        Products currentProduct=products.get(position);
        holder.OldPrice.setText(currentProduct.getPriceOld());
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
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return LIST_VIEW;
        } else {
            return GRID_VIEW;
        }
    }


    @Override
    public int getItemCount() {
        return products.size();
    }
}
