package com.athish_works.festdynamicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BookTicketsActivity extends AppCompatActivity {

    ImageView loadingScreen, transactionScreen;

    ArrayList<String> eventsList;
    ArrayList<String> membsList;
    ArrayList<String> feeList;

    FirebaseDatabase database;
    DatabaseReference reference;
    private static String REFERENCE_STRING = MainActivity.DATABASE_REF;

    Spinner eventSpinner;
    TextView membsTextView, paymentTextView;
    Button payButton, payAnotherButton;
    int index;

    String acName, acNumber;
    String playerName, playerNumber, txnNo, college;

    TextInputEditText memb1EditText, memb1NumberEditText, collegeEditText;

    final int UPI_PAYMENT = 7271;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_tickets);

        loadingScreen = findViewById(R.id.loading_screen);
        transactionScreen = findViewById(R.id.transaction_success_screen);

        eventsList = new ArrayList<>();
        membsList = new ArrayList<>();
        feeList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();

        index = -1;
        animate();

        Log.i("TAGGERZZ", "Got here");

        Log.i("TAGGERZZ", "Got here man");

        membsTextView = findViewById(R.id.membersTextView);
        paymentTextView = findViewById(R.id.textViewPayment);

        Log.i("TAGGERZZ", "Got here girl");

        memb1EditText = findViewById(R.id.memb1EditText);
        memb1NumberEditText = findViewById(R.id.memb1ContactEditText);
        collegeEditText = findViewById(R.id.collegeEditText);

        getDetails();
        getAcDetails();

        Log.i("TAGGERZZ", "Got here also beach");

        Toolbar mToolBar = findViewById(R.id.other_toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("Book Tickets");
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        payButton = findViewById(R.id.tickets_button);
        payAnotherButton = findViewById(R.id.tickets_another_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index!=-1) {
                    playerName = memb1EditText.getText().toString();
                    playerNumber = memb1NumberEditText.getText().toString();
                    college = collegeEditText.getText().toString();

                    playerNumber = playerNumber.trim();
                    playerName = playerName.trim();
                    college = college.trim();

                    if (!playerName.equals("") &&
                            !playerNumber.equals("") &&
                            (playerNumber.length()==10 || playerNumber.length()==13) &&
                            !college.equals("")) {

                        if (playerNumber.startsWith("+91")) {
                            playerNumber = playerNumber.substring(3);
                            memb1NumberEditText.setText(playerNumber);
                            Log.i("TAGGG", "Number " + playerNumber);
                        }

                        memb1EditText.setFocusable(false);
                        memb1NumberEditText.setFocusable(false);
                        collegeEditText.setFocusable(false);

                        if (feeList.get(index).equals("Free")) {
                            if (isConnectionAvailable(BookTicketsActivity.this)) {
                                gotTickets();
                            } else {
                                callAToast("Please connect to the internet");
                                memb1EditText.setFocusableInTouchMode(true);
                                collegeEditText.setFocusableInTouchMode(true);
                                memb1NumberEditText.setFocusableInTouchMode(true);
                            }

                        } else {
                            paymentProcess(feeList.get(index), acNumber, acName);
                            eventSpinner.setEnabled(false);
                        }

                    } else {
                        callAToast("Enter valid details");
                    }
                }
            }
        });

        payAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventSpinner.setEnabled(true);
                spinnerClicked(index);
                paymentTextView.setText("");
                memb1EditText.setFocusableInTouchMode(true);
                memb1EditText.setText("");
                collegeEditText.setFocusableInTouchMode(true);
                collegeEditText.setText("");
                memb1NumberEditText.setFocusableInTouchMode(true);
                memb1NumberEditText.setText("");
                payAnotherButton.setVisibility(View.GONE);
                payButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getAcDetails() {
        DatabaseReference ref = database.getReference(REFERENCE_STRING + "events");
        Log.i("TAGGERZZ", "Reference " + ref.toString());
        Log.i("TAGGERZZ", "Key " + ref.getKey());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                acName = dataSnapshot.child("bookAcName").getValue().toString();
                acNumber = dataSnapshot.child("bookAc").getValue().toString();
                Log.i("TAGGERZZ", "Ac Name " + acName);
                Log.i("TAGGERZZ", "Ac Number " + acNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gotTickets() {
        eventSpinner.setEnabled(false);
        payButton.setVisibility(View.GONE);
        payAnotherButton.setVisibility(View.VISIBLE);
        Log.i("TAGGG", "The tickets have been bought");
        DatabaseReference r = database.getReference(REFERENCE_STRING + "tickets");

        if (feeList.get(index).equals("Free")) {
            String[] eventsSplits = eventsList.get(index).split(" ");
            txnNo = eventsSplits[0] + playerNumber.substring(5);
            String a = "Your tickets have been booked" + "\n" + "Ref no. : " + txnNo;
            paymentTextView.setText(a);
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Tickets tickets = new Tickets(playerName,
                playerNumber,
                college,
                auth.getCurrentUser().getEmail(),
                eventsList.get(index),
                txnNo);

        String id = r.push().getKey();
        r.child(id).setValue(tickets);

        transactionScreen.setVisibility(View.VISIBLE);
        animateTransaction();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                transactionScreen.setVisibility(View.GONE);
            }
        }, 1400);

        callAToast("Your tickets have been booked");
    }

    private void spinnerClicked(int position) {
        Log.i("TAGGERZZ", "Completed");
        String mem = "Team Size : " + membsList.get(position);
        membsTextView.setText(mem);

        if (!feeList.get(position).equals("Free")) {
            String mmem = "Total amount to pay : Rs. " + feeList.get(position);
            paymentTextView.setText(mmem);
        } else {
            String mmem = "The event is free";
            paymentTextView.setText(mmem);
        }
        index = position;
    }

    private void paymentProcess(String amount, String upiId, String name) {
        Log.i("TAGGG", "In payment process");
        Uri uri = Uri.parse("upi://pay")
                .buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        /*
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        Log.i("TAGGG", upiPayIntent.toString());
        upiPayIntent.setData(uri);

         */

        List<Intent> targets = new ArrayList<Intent>();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        List<ResolveInfo> candidates = this.getPackageManager().queryIntentActivities(upiPayIntent, 0);
        Log.i("TAGGG", "Packages\n" + candidates);

        // filter package here
        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            Log.i("TAGGG", "Package = " + packageName);
            if (!packageName.equals("com.phonepe.app")) {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setData(uri);
                target.setPackage(packageName);
                targets.add(target);

            } else {
                Log.i("TAGGG", "Package Name = " + packageName + " found!!!");
            }
        }

        if (!targets.isEmpty()) {
            Intent chooser = Intent.createChooser(targets.get(0), "Pay with");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[]{}));
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            callAToast("No UPI app found, please install and retry");
        }


        /*
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        Log.i("TAGGG", "Package name " + chooser.resolveActivity(getPackageManager()));
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            callAToast("No UPI app found, please install and retry");
        }

         */


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("TAGGG", "Here");
        Log.i("TAGGG", "Data " + data);
        Log.i("TAGGG", "ResultCode " + resultCode);
        Log.i("TAGGG", "requestCode " + requestCode);
        switch (requestCode) {
            case UPI_PAYMENT:
                if (resultCode==RESULT_OK || resultCode==11) {
                    if (data!=null) {
                        Log.i("TAGGG", "Response " + data.getStringExtra("response"));
                        String txt = data.getStringExtra("response");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(txt);
                        upiPaymentDataOperation(dataList);

                    } else {
                        Log.i("TAGGG", "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }

                } else {
                    Log.i("TAGGG", "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(BookTicketsActivity.this)) {
            String str = data.get(0);
            Log.i("TAGGG", "payment operation : " + str);
            String paymentCancel = "";
            if (str==null) {
                str = "discard";
            }
            String status = "";
            String approvalRefNo = "";
            String[] response = str.split("&");
            for (int i=0; i<response.length; i++) {
                String[] equalStr = response[i].split("=");
                if (equalStr.length>=2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) ||
                            equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                                approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user";
                }
            }

            if (status.equals("success")) {
                callAToast("Transaction successful");
                Log.i("TAGGG", "responseStr : " + approvalRefNo);
                txnNo = approvalRefNo;
                gotTickets();
                String a = "Your tickets have been booked" + "\n" + "Ref no. : " + txnNo;
                paymentTextView.setText(a);
                payButton.setVisibility(View.GONE);
                payAnotherButton.setVisibility(View.VISIBLE);

            } else if (paymentCancel.equals("Payment cancelled by user")) {
                callAToast("Payment cancelled by user");
                memb1NumberEditText.setFocusableInTouchMode(true);
                memb1EditText.setFocusableInTouchMode(true);
                collegeEditText.setFocusableInTouchMode(true);
                eventSpinner.setEnabled(true);

            } else {
                callAToast("Transaction failed. Please try again later");
                memb1NumberEditText.setFocusableInTouchMode(true);
                memb1EditText.setFocusableInTouchMode(true);
                collegeEditText.setFocusableInTouchMode(true);
                eventSpinner.setEnabled(true);
            }

        } else {
            callAToast("Internet connection not available");
            memb1EditText.setFocusableInTouchMode(true);
            collegeEditText.setFocusableInTouchMode(true);
            memb1NumberEditText.setFocusableInTouchMode(true);
            eventSpinner.setEnabled(true);
        }
    }

    private boolean isConnectionAvailable(Context context) {
        Log.i("TAGGG", "I'm here");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo!=null && networkInfo.isConnected()
                    && networkInfo.isConnectedOrConnecting()
                    && networkInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private void callAToast(String a) {
        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
    }

    private void animate() {
        Drawable d = loadingScreen.getDrawable();
        Log.i("TAGGERS", "Here 2");
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
        avd.start();
    }

    private void animateTransaction() {
        Drawable d = transactionScreen.getDrawable();
        Log.i("TAGGERS", "Here yes");
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
        avd.start();
    }

    private void getDetails () {
        reference = database.getReference(REFERENCE_STRING + "events/events");
        Log.i("TAGGERZZ", "Reference " + reference.toString());
        Log.i("TAGGERZZ", "Key " + reference.getKey());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("TAGGERZZ", "ChildrenCount " + dataSnapshot.getChildrenCount());
                Log.i("TAGGERZZ", "Value " + dataSnapshot.getValue());

                eventsList.clear();
                membsList.clear();
                feeList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAGGERZZ", "Here");
                    Log.i("TAGGERZZ", "ds " + ds.getValue());

                    for (DataSnapshot event : ds.getChildren()) {
                        switch (event.getKey()) {
                            case "name":
                                String name = event.getValue().toString();
                                Log.i("TAGGERZZ", "event " + name);
                                eventsList.add(name);
                                break;
                            case "teamSize":
                                String member = event.getValue().toString();
                                Log.i("TAGGERZZ", "member " + member);
                                membsList.add(member);
                                break;
                            case "fee":
                                String fee = event.getValue().toString();
                                Log.i("TAGGERZZ", "fee " + fee);
                                feeList.add(fee);
                                break;
                        }
                    }
                }
                loadingScreen.setVisibility(View.GONE);


                eventSpinner = findViewById(R.id.select_event_spinner);
                ArrayAdapter<String> eventsAdapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, eventsList);
                eventsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Log.i("TAGGERZZ", "Got here mannn");

                eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.i("TAGGG", "I'm selected");
                        spinnerClicked(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.i("TAGGG", "I'm not selected");
                    }
                });

                eventSpinner.setAdapter(eventsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("TAGGERZZ", "Error " + databaseError);
            }
        });

    }

}
