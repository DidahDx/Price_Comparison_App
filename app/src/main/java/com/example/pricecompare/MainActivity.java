package com.example.pricecompare;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,View.OnClickListener{

    static TextView editSearch;

    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView userEmail;
    String user_email;
    Boolean checkLogin;
    MenuItem logOut;
    TextView navUsername;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the Firebase Analytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        editSearch=  findViewById(R.id.product_name);
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);



        editSearch.setOnClickListener(this);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();

        View headerView = navigationView.getHeaderView(0);
       navUsername = (TextView) headerView.findViewById(R.id.user_label_email);
        if (firebaseUser != null) {
            navUsername.setText(firebaseUser.getEmail());
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        logOut=menu.findItem(R.id.action_logout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout:
                FirebaseUser User= firebaseAuth.getCurrentUser();
                if (User!=null){
                    firebaseAuth.signOut();
                    checkLogin=false;
                    user_email="user email ";
                    navUsername.setText(user_email);
                    LogOutGoogleSignIn();

                    Toast.makeText(this, "Logged out successful", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, "Swipe Right to Login", Toast.LENGTH_LONG).show();

                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void LogOutGoogleSignIn() {
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }


//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        if (checkLogin){
//            logOut.setVisible(true);
//        }else{
//            logOut.setVisible(false);
//        }
//
//        return true;
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()){

            case R.id.nav_login:
                Intent i=new Intent(MainActivity.this,LoginPage.class);
                startActivity(i);
                break;



            case R.id.nav_saved:
                startActivity(new Intent(MainActivity.this,SavedProducts.class));
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            user_email=currentUser.getEmail();
            Toast.makeText(MainActivity.this,user_email,Toast.LENGTH_LONG).show();
            checkLogin=true;
            navUsername.setText(user_email);
        }else{
            user_email="user email ";
            checkLogin=false;
            navUsername.setText(user_email);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            user_email=currentUser.getEmail();
//            Toast.makeText(MainActivity.this,user_email,Toast.LENGTH_LONG).show();
            checkLogin=true;
            navUsername.setText(user_email);

        }else{

            user_email="user email ";
            navUsername.setText(user_email);
            checkLogin=false;

        }
    }

    @Override
    public void onClick(View v) {

        if (v==editSearch){
            startActivity(new Intent(MainActivity.this,Search.class));
        }

    }
}