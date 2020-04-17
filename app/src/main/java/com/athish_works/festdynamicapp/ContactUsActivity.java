package com.athish_works.festdynamicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class ContactUsActivity extends AppCompatActivity {

    String officeNoMainString, websiteMainString, mailIdMainString, addressMainString, locationString;

    FirebaseDatabase database;
    DatabaseReference reference;
    private static String REFERENCE_STRING = MainActivity.DATABASE_REF + "uvce";

    ImageView loadingScreen;

    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        mToolBar = findViewById(R.id.other_toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("Contact Us");
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadingScreen = findViewById(R.id.loading_screen);

        animate();

        getDetails();

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
                        case "office":
                            TextView officeNo = findViewById(R.id.office_no);
                            officeNoMainString = ds.getValue().toString();
                            String officeNoString = "<b>Office:\t</b>" + "<u>" + officeNoMainString + "</u>";
                            officeNo.setText(Html.fromHtml(officeNoString));
                            break;

                        case "website":
                            TextView website = findViewById(R.id.website);
                            websiteMainString = ds.getValue().toString();
                            String websiteString = "<b>Website:\t</b>" + "<u>" + websiteMainString + "</u>";
                            website.setText(Html.fromHtml(websiteString));
                            break;

                        case "mail":
                            TextView mailId = findViewById(R.id.mail_id);
                            mailIdMainString = ds.getValue().toString();
                            String mailIdString = "<b>Official Mail ID:\t</b>" + mailIdMainString;
                            mailId.setText(Html.fromHtml(mailIdString));
                            break;

                        case "address":
                            TextView address = findViewById(R.id.address);
                            addressMainString = ds.getValue().toString();
                            String addressString = "<b>Address:<br></b>" + addressMainString;
                            address.setText(Html.fromHtml(addressString));
                            break;

                        case "location":
                            ImageView location = findViewById(R.id.location);
                            Glide.with(getApplicationContext())
                                    .load(ds.getValue().toString())
                                    .centerCrop()
                                    .into(location);
                            break;

                        case "link":
                            locationString = ds.getValue().toString();
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

    public void contactUsClick(View view) {
        Uri uri;
        Intent likeIng;
        switch (view.getTag().toString()) {
            case "location":
                uri = Uri.parse(locationString+addressMainString);
                likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.google.android.apps.maps");
                startActivity(likeIng);
                break;

            case "mail":
                likeIng = new Intent(Intent.ACTION_SEND);
                likeIng.setType("text/plain");
                likeIng.putExtra(Intent.EXTRA_EMAIL, new String[] {mailIdMainString});
                likeIng.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(likeIng, "Send Mail"));
                break;

            case "website":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+websiteMainString)));
                break;

            case "office_no":
                likeIng = new Intent(Intent.ACTION_DIAL);
                likeIng.setData(Uri.parse("tel:"+officeNoMainString));
                startActivity(likeIng);
                break;
        }
    }

    private void animate() {
        Drawable d = loadingScreen.getDrawable();
        if (d instanceof AnimatedVectorDrawableCompat) {
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) d;
            avd.start();
        } else if (d instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
            avd.start();
        }
    }

}
