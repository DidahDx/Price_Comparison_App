package com.example.pricecompare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    Button loginButton;
    Button signUpButton;
    EditText emailAddress;
    EditText password;
    ProgressBar progressBar;

    String TAG=LoginPage.class.getSimpleName();

    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Obtain the Firebase Analytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        loginButton=findViewById(R.id.login_button);
        signUpButton=findViewById(R.id.sign_up_button);
        emailAddress=findViewById(R.id.emailAddress);
        password=findViewById(R.id.password);
        progressBar=findViewById(R.id.progress_login_bar);
        errorMessage=findViewById(R.id.error_message);
        Toolbar toolbar=findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
       toolbar.setTitle("Login");

        //used to display the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
       mAuth = FirebaseAuth.getInstance();

       loginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
           if (!emailAddress.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){
               errorMessage.setText(null);
               progressBar.setVisibility(View.VISIBLE);
               mAuth.signInWithEmailAndPassword(emailAddress.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(LoginPage.this, "Login Successful.",
                                            Toast.LENGTH_LONG).show();
                                    firebaseUser = mAuth.getCurrentUser();
                                    finish();
                                } else {
                                    errorMessage.setText(getString(R.string.login_error_message));
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginPage.this, "Authentication failed.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

           }else{
                   Toast.makeText(LoginPage.this, "Fill both Email and Password Fields",
                           Toast.LENGTH_LONG).show();
               }

           }
       });


       signUpButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(LoginPage.this,SignUp.class));
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



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            finish();
        }
//        updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            finish();
        }
    }

}
