package com.example.pricecompare.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pricecompare.data.model.Recent;
import com.example.pricecompare.R;

import java.util.ArrayList;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.RecentViewHolder>{


    private GridLayoutManager mLayoutManager;
  private ArrayList<Recent> product_name;

    private RecentSearchAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(RecentSearchAdapter.OnItemClickListener listener){
        mListener=listener;
    }

    class RecentViewHolder extends RecyclerView.ViewHolder{
        TextView recent_name;
        ImageView recent_image;
        ImageView recent_close;

        public RecentViewHolder(@NonNull View itemView, final RecentSearchAdapter.OnItemClickListener listener, int viewType) {
            super(itemView);

            recent_image=itemView.findViewById(R.id.recent_image);
            recent_name=itemView.findViewById(R.id.recent_name);
            recent_close=itemView.findViewById(R.id.recent_close);

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

            recent_close.setOnClickListener(new View.OnClickListener() {
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

    public RecentSearchAdapter(ArrayList<Recent> prod, GridLayoutManager gridLayoutManager){
        product_name=prod;
        mLayoutManager=gridLayoutManager;

    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_list,parent,false);
        return new RecentSearchAdapter.RecentViewHolder(v,mListener,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSearchAdapter.RecentViewHolder holder, int position) {
        Recent current=product_name.get(position);

        holder.recent_name.setText(current.getName());
        holder.recent_image.setImageResource(current.getRecentImage());
        holder.recent_close.setImageResource(current.getLeftImage());

    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return product_name.size();
    }


}
