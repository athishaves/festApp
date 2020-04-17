package com.athish_works.festdynamicapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventsRecyclerView extends RecyclerView.Adapter<EventsRecyclerView.ViewHolder> {

    ArrayList<EventsClass> products;
    private Context context;

    public EventsRecyclerView(@NonNull Context context, @NonNull ArrayList<EventsClass> products) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_items_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        EventsClass product = products.get(position);

        Glide.with(context)
                .load(product.getImageURL())
                .centerCrop()
                .into(holder.events_image);
        holder.events_name.setText(product.getName());
        holder.events_description.setText(product.getDesc());

        String extended_description = "<b>Team Size : </b>" + product.getTeamSize() + "<br>" +
                "<b>Entry Fee : </b>" + product.getFee() + "<br>" +
                "<b>Scheduled On : </b>" + product.getSchDate() + ",  " + product.getSchTime() + "<br><br>" +
                "<b>Contacts : </b>" + "<br>" +
                product.getCont1Name() + " - " + product.getCont1No() + "<br>" +
                product.getCont2Name() + " - " + product.getCont2No() + "<br>" +
                product.getCont3Name() + " - " + product.getCont3No();
        holder.events_extended_descp.setText(Html.fromHtml(extended_description));

    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        PhotoView events_image;
        TextView events_name, events_description, events_extended_descp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            events_image = itemView.findViewById(R.id.events_image);
            events_name = itemView.findViewById(R.id.events_name);
            events_description = itemView.findViewById(R.id.events_description);
            events_extended_descp = itemView.findViewById(R.id.events_extended_descp);

        }

    }

}
