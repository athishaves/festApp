package com.athish_works.festdynamicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignInSignUpActivity extends AppCompatActivity {

    TextView textView, textView2;
    TextInputLayout nameEditLayout, gmailEditLayout, passEditLayout;
    TextInputEditText nameEditText, gmailEditText, passEditText;
    Button button;

    int goneCount;

    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);

        mAuth = FirebaseAuth.getInstance();
        progressBar = new ProgressBar(this);

        goneCount = 0;

        nameEditLayout = findViewById(R.id.usernameInputLayout);
        gmailEditLayout = findViewById(R.id.emailInputLayout);
        passEditLayout = findViewById(R.id.passwordInputLayout);

        nameEditText = findViewById(R.id.usernameEditText);
        gmailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passwordEditText);

        textView = findViewById(R.id.textView);
        textView.setText(R.string.click_here_to_login);

        textView2 = findViewById(R.id.textView2);
        textView2.setText(R.string.skip_for_now);

        button = findViewById(R.id.button);
        button.setText(R.string.sign_up);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (goneCount==0) {
                    SignUp();

                } else if (goneCount==1) {
                    SignIn();
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fade_in = AnimationUtils.loadAnimation(SignInSignUpActivity.this, R.anim.fade_in);
                Animation small_fade_in = AnimationUtils.loadAnimation(SignInSignUpActivity.this, R.anim.small_fade_in);
                Animation slide_out = AnimationUtils.loadAnimation(SignInSignUpActivity.this, R.anim.slide_out);
                Animation slide_in = AnimationUtils.loadAnimation(SignInSignUpActivity.this, R.anim.slide_in);

                gmailEditText.setText("");
                nameEditText.setText("");
                passEditText.setText("");

                textView.setAnimation(fade_in);

                if (goneCount==0) {
                    gmailEditText.requestFocus();
                    slide_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            nameEditLayout.setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    nameEditLayout.setAnimation(slide_out);

                    button.setText(R.string.sign_in);
                    textView.setText(R.string.click_here_to_signin);
                    goneCount=1;

                } else if (goneCount==1) {
                    nameEditText.requestFocus();
                    slide_in.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            nameEditLayout.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    nameEditLayout.setAnimation(slide_in);

                    button.setText(R.string.sign_up);
                    textView.setText(R.string.click_here_to_login);
                    goneCount=0;
                }
                button.setAnimation(small_fade_in);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInSignUpActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void SignIn() {
        final String gmail = gmailEditText.getText().toString();
        final String password = passEditText.getText().toString();
        if (gmail.equals("") || password.equals("")) {
            callAToast("Fill the details");

        } else {
            mAuth.signInWithEmailAndPassword(gmail, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                callAToast("User login successful");
                                callAToast("Welcome " + mAuth.getCurrentUser().getDisplayName());
                                startActivity(new Intent(SignInSignUpActivity.this, MainActivity.class));
                                finish();
                            } else {
                                callAToast(task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private void SignUp() {
        final String name = nameEditText.getText().toString();
        final String gmail = gmailEditText.getText().toString();
        final String password = passEditText.getText().toString();

        if (name.equals("") || gmail.equals("") || password.equals("")) {
            callAToast("Fill the details");

        } else {
            mAuth.createUserWithEmailAndPassword(gmail, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {


                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                user.updateProfile(profile)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                callAToast("User registered successfully");
                                                callAToast("Welcome " + name);
                                                startActivity(new Intent(SignInSignUpActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        });

                                /*
                                User user = new User(name, gmail);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if (task.isSuccessful()) {
                                                    callAToast("User registered successfully");
                                                    callAToast("Welcome " + name);
                                                    startActivity(new Intent(SignInSignUpActivity.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    callAToast("failed at setting database " + task.getException().getMessage());
                                                }
                                            }
                                        });

                                 */

                            } else {
                                progressBar.setVisibility(View.GONE);
                                callAToast(task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private void setProfile() {
    }

    private void callAToast(String a) {
        Toast.makeText(this, a, Toast.LENGTH_SHORT).show();
    }
}
