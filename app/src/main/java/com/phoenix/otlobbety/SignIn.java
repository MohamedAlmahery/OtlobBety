package com.phoenix.otlobbety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.drawable.CheckBoxDrawable;
import com.rey.material.widget.CheckBox;

public class SignIn extends AppCompatActivity {
    MaterialEditText  edtPhone,edtPassword;
    FButton btnSignIn;
    ProgressDialog mDialog;
    TextView txtforgetpasword;
    CheckBox ckbRemember;

    FirebaseDatabase database;
    DatabaseReference table_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (MaterialEditText)findViewById(R.id.password);
        edtPhone = (MaterialEditText)findViewById(R.id.phoneNumber);
        btnSignIn = (FButton)findViewById(R.id.signIn);
        txtforgetpasword = (TextView)findViewById(R.id.forgot_password);
        ckbRemember = (CheckBox)findViewById(R.id.ckbRemember);
        //Init Paper
        Paper.init(this);

        txtforgetpasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgetShow();
            }
        });

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Common.isConnectedToInternet(getBaseContext())) {

                    //Save user & password
                    if (ckbRemember.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
                    }
                   // if (edtPhone.getText().toString().isEmpty() && edtPassword.getText().toString().isEmpty()) // هنا
                     //   Toast.makeText(SignIn.this, "Text's is Empty !!!", Toast.LENGTH_SHORT).show();

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please Wait...");
                    mDialog.show();


                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Check if User not exist database
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                //Get User information
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString()); //set phone
                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(homeIntent);
                                    finish();
                                } else {
                                    Toast.makeText(SignIn.this, "Wrong Password!!", Toast.LENGTH_SHORT).show();
                                    txtforgetpasword.setVisibility(View.VISIBLE);



                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Wrong Phone Number!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }); }
                else {
                    Toast.makeText(SignIn.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return ;
                }
            }
        });
    }

    private void showForgetShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forget Password");
        builder.setMessage("Enter Your Secure Code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgetView = inflater.inflate(R.layout.forgetpassword_layout,null);

        builder.setView(forgetView);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText)forgetView.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCide = (MaterialEditText)forgetView.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText().toString())
                                .getValue(User.class);
                        if(user.getSecureCode().equals(edtSecureCide.getText().toString()))
                            Toast.makeText(SignIn.this, "Your Password : "+user.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SignIn.this, "Wrong Secure Code !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();


    }
}
