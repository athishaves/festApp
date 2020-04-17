package com.athish_works.festdynamicapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GuestsRecyclerView extends RecyclerView.Adapter<GuestsRecyclerView.ViewHolder> {

    ArrayList<GuestsClass> products;
    private Context context;

    public GuestsRecyclerView(@NonNull Context context, @NonNull ArrayList<GuestsClass> products) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guests_items_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GuestsClass product = products.get(position);

        Glide.with(context)
                .load(product.getImageURL())
                .centerCrop()
                .into(holder.guestsImage);
        holder.guestsName.setText(product.getName());
        holder.guestsOccupation.setText(product.getOccupation());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView guestsImage;
        TextView guestsName, guestsOccupation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            guestsImage = itemView.findViewById(R.id.guest_image);
            guestsName = itemView.findViewById(R.id.guest_name);
            guestsOccupation = itemView.findViewById(R.id.guest_occup);
        }

    }

}
