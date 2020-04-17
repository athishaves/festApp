package com.athish_works.festdynamicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseDatabase database;
    DatabaseReference reference;
    public static String DATABASE_REF = "";
    private static String REFERENCE_STRING = DATABASE_REF + "homePage";

    FirebaseAuth mAuth;

    Toolbar mToolBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ArrayList<GuestsClass> guestsList;

    ArrayList<SponsorsClass> sponsorsList;

    String fbLink, instaLink;

    ImageView loadingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingScreen = findViewById(R.id.loading_screen);
        animate();

        Log.i("MAIN", "Got here");
        mAuth = FirebaseAuth.getInstance();

        getDetails();

        mToolBar = findViewById(R.id.main_toolbar);
        mToolBar.setTitle("Milagro");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        checkLoginStatus();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                mToolBar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        guestsList = new ArrayList<>();

        sponsorsList = new ArrayList<>();

    }

    private void checkLoginStatus() {
        View header_view = navigationView.getHeaderView(0);
        TextView signedInAs = header_view.findViewById(R.id.nav_textView);
        Menu nav_menu = navigationView.getMenu();
        if (mAuth.getCurrentUser()==null) {
            nav_menu.findItem(R.id.log_out_nav).setVisible(false);
            signedInAs.setText(R.string.guest);
        } else {
            nav_menu.findItem(R.id.sign_in_nav).setVisible(false);
            String a = mAuth.getCurrentUser().getDisplayName();
            signedInAs.setText(a);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_page_nav:
                break;
            case R.id.contact_page_nav:
                startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                break;
            case R.id.events_page_nav:
                startActivity(new Intent(MainActivity.this, EventsActivity.class));
                break;
            case R.id.team_page_nav:
                startActivity(new Intent(MainActivity.this, TeamActivity.class));
                break;
            case R.id.log_out_nav:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, SignInSignUpActivity.class));
                finish();
                break;
            case R.id.sign_in_nav:
                startActivity(new Intent(MainActivity.this, SignInSignUpActivity.class));
                finish();
                break;
            case R.id.book_tickets_nav:
                if (mAuth.getCurrentUser()!=null) {
                    startActivity(new Intent(MainActivity.this, BookTicketsActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Login to book a Ticket", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, SignInSignUpActivity.class));
                }
                break;
        }
        drawerLayout.closeDrawers();
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                        case "fbID":
                            fbLink = ds.getValue().toString();
                            break;

                        case "instaID":
                            instaLink = ds.getValue().toString();
                            break;

                        case "mainDesc":
                            TextView mainDescription = findViewById(R.id.main_desc_textview);
                            mainDescription.setText(ds.getValue().toString());
                            break;

                        case "mainImage":
                            ImageView mainImageView = findViewById(R.id.main_image);
                            Glide.with(getApplicationContext())
                                    .load(ds.getValue().toString())
                                    .centerCrop()
                                    .into(mainImageView);
                            break;
                        case "guests":
                            getGuestsDetails();
                            break;
                        case "sponsors":
                            getSponsorsDetails();
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

    private void getGuestsDetails () {
        reference = database.getReference(REFERENCE_STRING + "/guests");
        Log.i("TAGGER", "Reference " + reference.toString());
        Log.i("TAGGER", "Key " + reference.getKey());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("TAGGER", "Value " + dataSnapshot.getValue());

                if (guestsList.size()!=0) {
                    guestsList.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAGGER", "Here");
                    Log.i("TAGGER", "ds " + ds.getValue());
                    GuestsClass guest = ds.getValue(GuestsClass.class);
                    guestsList.add(guest);
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

    private void getSponsorsDetails () {
        reference = database.getReference(REFERENCE_STRING + "/sponsors");
        Log.i("TAGGER", "Reference " + reference.toString());
        Log.i("TAGGER", "Key " + reference.getKey());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sponsorsList.clear();
                Log.i("TAGGER", "Here 1");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SponsorsClass guest = ds.getValue(SponsorsClass.class);
                    sponsorsList.add(guest);
                    Log.i("TAGGER", "Here 2");
                }
                initSponsorsRecyclerView();
                Log.i("TAGGER", "Here 3");
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
        RecyclerView guestsRecyclerView = findViewById(R.id.guests_recycler_view);
        guestsRecyclerView.setLayoutManager(layoutManager);
        GuestsRecyclerView adapter = new GuestsRecyclerView(this, guestsList);
        guestsRecyclerView.setAdapter(adapter);
    }

    private void initSponsorsRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        RecyclerView guestsRecyclerView = findViewById(R.id.sponsors_recycler_view);
        guestsRecyclerView.setLayoutManager(layoutManager);
        SponsorsRecyclerView adapter = new SponsorsRecyclerView(this, sponsorsList);
        guestsRecyclerView.setAdapter(adapter);
    }

    public void iClicked(View view) {

        Log.i("TAG2", view.getTag() + " clicked me");

        if (view.getTag().equals("insta")) {
            Log.i("TAG2", " insta exception");
            Uri uri = Uri.parse(instaLink);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");
            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                Log.i("TAG2", e.getMessage() + " insta exception");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(instaLink)));
            }

        } else if (view.getTag().equals("fb")) {
            Log.i("TAG2", " fb exception");
            Uri uri = Uri.parse(fbLink);
            Log.i("TAG2", fbLink + " FBLink");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.Facebook.katana");
            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                Log.i("TAG2", e.getMessage() + " fb exception");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fbLink)));
            }
        }
    }

    private void animate() {
        Drawable d = loadingScreen.getDrawable();
        Log.i("TAGGERS", "Here 2");
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
        avd.start();
    }

}
