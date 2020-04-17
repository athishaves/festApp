package com.athish_works.festdynamicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Objects;

public class TeamActivity extends AppCompatActivity {

    ArrayList<GuestsClass> teamList;

    FirebaseDatabase database;
    DatabaseReference reference;
    private static String REFERENCE_STRING = MainActivity.DATABASE_REF + "team";

    ImageView loadingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        loadingScreen = findViewById(R.id.loading_screen);

        animate();

        teamList = new ArrayList<>();
        getDetails();

        Toolbar mToolBar = findViewById(R.id.not_main_toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("The Team");
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        Log.i("TAGGEREST", "HEre I m");
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAGGEREST", "HEre not I m");
                onBackPressed();
            }
        });

    }

    private void getDetails() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(REFERENCE_STRING);
        Log.i("TAGGER", "Reference " + reference.toString());
        Log.i("TAGGER", "Key " + reference.getKey());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("TAGGER", "DS ChildCount " + dataSnapshot.getChildrenCount());

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    switch (Objects.requireNonNull(ds.getKey())) {
                        case "description":
                            TextView mainDescription = findViewById(R.id.team_desc_textview);
                            mainDescription.setText(ds.getValue().toString());
                            break;

                        case "imageURL":
                            ImageView mainImageView = findViewById(R.id.team_image);
                            Glide.with(getApplicationContext())
                                    .load(ds.getValue().toString())
                                    .centerCrop()
                                    .into(mainImageView);
                            break;
                        case "team":
                            getTeamDetails();
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

    private void getTeamDetails () {
        reference = database.getReference(REFERENCE_STRING + "/team");
        Log.i("TAGGER", "Reference " + reference.toString());
        Log.i("TAGGER", "Key " + reference.getKey());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("TAGGER", "Value " + dataSnapshot.getValue());

                if (teamList.size()!=0) {
                    teamList.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAGGER", "Here");
                    Log.i("TAGGER", "ds " + ds.getValue());
                    GuestsClass guest = ds.getValue(GuestsClass.class);
                    teamList.add(guest);
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
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        RecyclerView guestsRecyclerView = findViewById(R.id.team_recycler_view);
        guestsRecyclerView.setLayoutManager(layoutManager);
        GuestsRecyclerView adapter = new GuestsRecyclerView(this, teamList);
        guestsRecyclerView.setAdapter(adapter);
        Log.i("TAG2", "This also done");
    }

    private void animate() {
        Drawable d = loadingScreen.getDrawable();
        Log.i("TAGGERS", "Here 2");
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
        avd.start();
    }

}
