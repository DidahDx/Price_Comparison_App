package com.example.pricecompare.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.pricecompare.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private static final int GOOGLE_SIGN =40 ;
    Button loginButton;
    Button signUpButton;
    EditText emailAddress;
    EditText password;
    ProgressBar progressBar;

    String TAG= Login.class.getSimpleName();

    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextView errorMessage;
    Button googleSign;
    GoogleSignInClient mGoogleSignInClient;

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
        googleSign=findViewById(R.id.google_button);
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
                                    Toast.makeText(Login.this, "Login Successful.",
                                            Toast.LENGTH_LONG).show();
                                    firebaseUser = mAuth.getCurrentUser();
                                    finish();
                                } else {
                                    errorMessage.setText(getString(R.string.login_error_message));
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

           }else{
                   Toast.makeText(Login.this, "Fill both Email and Password Fields",
                           Toast.LENGTH_LONG).show();
               }

           }
       });


       signUpButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Login.this,SignUp.class));
           }
       });


       ///USed to sign in with google

        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions);

        googleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent=mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signIntent,GOOGLE_SIGN);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GOOGLE_SIGN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);


            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);

                if (account!=null){
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG ,"FIREBASE authentication with google "+account.getId());

        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
           if (task.isSuccessful()){
               Log.d(TAG,"signin success");
               firebaseUser=mAuth.getCurrentUser();
               Toast.makeText(Login.this, "sign In success", Toast.LENGTH_LONG).show();
               finish();
           }else {
               Log.d(TAG,"signin failed "+task.getException());
               Toast.makeText(Login.this, "sign in failed", Toast.LENGTH_LONG).show();
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



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            finish();
        }
//
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
