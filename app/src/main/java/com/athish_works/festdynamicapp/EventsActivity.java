package com.athish_works.festdynamicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Objects;

public class EventsActivity extends AppCompatActivity {

    ArrayList<EventsClass> eventsList;

    ImageView loadingScreen;

    FirebaseDatabase database;
    DatabaseReference reference;
    private static String REFERENCE_STRING = MainActivity.DATABASE_REF + "events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        loadingScreen = findViewById(R.id.loading_screen);

        eventsList = new ArrayList<>();

        animate();

        Log.i("TAGGER", "Got here");
        getDetails();

        Toolbar mToolBar = findViewById(R.id.not_main_toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("Events");
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getDetails() {
        database = FirebaseDatabase.getInstance();
        Log.i("TAGGER", "Here I am");
        reference = database.getReference(REFERENCE_STRING);
        Log.i("TAGGER", "Here also");
        Log.i("TAGGER", "Reference " + reference.toString());
        Log.i("TAGGER", "Key " + reference.getKey());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("TAGGER", "DS ChildCount " + dataSnapshot.getChildrenCount());

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    switch (Objects.requireNonNull(ds.getKey())) {
                        case "descp":
                            TextView mainDescription = findViewById(R.id.events_desc_textview);
                            mainDescription.setText(ds.getValue().toString());
                            break;

                        case "images":
                            ImageView mainImageView = findViewById(R.id.events_main_image);
                            Glide.with(getApplicationContext())
                                    .load(ds.getValue().toString())
                                    .centerCrop()
                                    .into(mainImageView);
                            break;

                        case "events":
                            getEventsDetails();
                            break;
                    }
                }

                loadingScreen.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("TAGGER", "Error " + databaseError);
            }
        });
    }

    private void getEventsDetails () {
        reference = database.getReference(REFERENCE_STRING + "/events");
        Log.i("TAGGER", "Reference " + reference.toString());
        Log.i("TAGGER", "Key " + reference.getKey());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("TAGGER", "Value " + dataSnapshot.getValue());

                if (eventsList.size()!=0) {
                    eventsList.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAGGER", "Here");
                    Log.i("TAGGER", "ds " + ds.getValue());
                    EventsClass guest = ds.getValue(EventsClass.class);
                    eventsList.add(guest);
                    Log.i("TAGGER", "Done");
                }
                initGuestsRecyclerView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("TAGGER", "Error " + databaseError);
            }
        });
    }

    private void initGuestsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView guestsRecyclerView = findViewById(R.id.events_recycler_view);
        guestsRecyclerView.setLayoutManager(layoutManager);
        EventsRecyclerView adapter = new EventsRecyclerView(this, eventsList);
        guestsRecyclerView.setAdapter(adapter);
        Log.i("TAG2", "This also done");
    }

    private void animate() {
        Drawable d = loadingScreen.getDrawable();
        Log.i("TAGGERS", "Here 2");
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
        avd.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
