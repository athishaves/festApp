package com.athish_works.festdynamicapp;

import android.content.Context;
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

public class SponsorsRecyclerView extends RecyclerView.Adapter<SponsorsRecyclerView.ViewHolder> {

    ArrayList<SponsorsClass> products;
    private Context context;

    public SponsorsRecyclerView(@NonNull Context context, @NonNull ArrayList<SponsorsClass> products) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sponsors_items_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SponsorsClass product = products.get(position);

        Glide.with(context)
                .load(product.getImageURL())
                .centerCrop()
                .into(holder.guestsImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView guestsImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            guestsImage = itemView.findViewById(R.id.sponsors_image);
        }

    }

}
