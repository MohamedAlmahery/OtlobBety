package com.phoenix.otlobbety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class Splash_Screen extends AppCompatActivity {

    FButton loginshow,signupshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        FirebaseApp.initializeApp(this);


        loginshow = (FButton)findViewById(R.id.signIn);
        signupshow = (FButton)findViewById(R.id.signUp);

        Paper.init(this);


        loginshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SuperIntentSignIn = new Intent(Splash_Screen.this,SignIn.class);
                startActivity(SuperIntentSignIn);
            }
        });
        signupshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SuperIntentSignUp = new Intent(Splash_Screen.this,SignUp.class);
                startActivity(SuperIntentSignUp);
            }
        });
        //Check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd !=null)
        {
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user,pwd);
        }

    }

    private void login(final String phone, final String pwd) {
        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();


            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Check if User not exist database
                    if (dataSnapshot.child(phone).exists()) {
                        //Get User information
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone); //set phone
                        if (user.getPassword().equals(pwd)) {
                            Intent homeIntent = new Intent(Splash_Screen.this, Home.class);
                            Common.currentUser = user;
                            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Toast.makeText(Splash_Screen.this, "Wrong Password!!", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(Splash_Screen.this, "Wrong Phone Number!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            }); }
        else {
            Toast.makeText(Splash_Screen.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return ;
        }
    }

    }


