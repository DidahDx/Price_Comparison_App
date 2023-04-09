package com.example.pricecompare.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.pricecompare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    EditText email;
    EditText password;
    EditText confirmPassword;
    Button signup;
    TextView errorMessage;
    ProgressBar progressBar;
    Toolbar toolbar;
    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String TAG=SignUp.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Obtain the Firebase Analytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        email=findViewById(R.id.signup_email);
        password=findViewById(R.id.signup_password);
        confirmPassword=findViewById(R.id.signp_confirm_password);
        signup=findViewById(R.id.signup_btn);
        errorMessage=findViewById(R.id.error_signup);
        progressBar=findViewById(R.id.signup_progress);
        toolbar=findViewById(R.id.sign_up_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Sign Up");

        //used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessage.setText(null);
                progressBar.setVisibility(View.VISIBLE);
                if (!email.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()
                        && !confirmPassword.getText().toString().trim().isEmpty()){

                    if (password.getText().toString().matches(confirmPassword.getText().toString())){

                        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            Toast.makeText(SignUp.this, "Account created successful",
                                                    Toast.LENGTH_LONG).show();
                                            finish();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUp.this, "Authentication failed.",
                                                    Toast.LENGTH_LONG).show();

                                        }

                                    }
                                });

                    }else {
                        progressBar.setVisibility(View.GONE);
                        errorMessage.setText(getString(R.string.password_dont_match));
                    }

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUp.this, "Fill both Email and Passwords Fields",
                            Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    //listener when an item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            //back button used to close this activity
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
