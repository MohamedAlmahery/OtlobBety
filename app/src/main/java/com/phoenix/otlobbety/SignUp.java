package com.phoenix.otlobbety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import info.hoang8f.widget.FButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {
    MaterialEditText edtName,edtPhone,edtPassword,edtSecureCode;
    FButton btnSignUp;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPassword = (MaterialEditText)findViewById(R.id.password);
        edtPhone = (MaterialEditText)findViewById(R.id.phoneNumber);
        edtName = (MaterialEditText)findViewById(R.id.Name);
        edtSecureCode = (MaterialEditText)findViewById(R.id.edtSecureCode);
        btnSignUp = (FButton)findViewById(R.id.signUp);

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedToInternet(getBaseContext())){

                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please Wait...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Check if User not exist database
                        if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            //Get User information
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "This User is Used !", Toast.LENGTH_SHORT).show();
                            } else {
                            mDialog.dismiss();
                            User user = new User(edtName.getText().toString(),
                                    edtPassword.getText().toString(),
                                    edtSecureCode.getText().toString());
                            table_user.child(edtPhone.getText().toString()).setValue(user);

                            Toast.makeText(SignUp.this, "SignIn with this new Account ^^", Toast.LENGTH_LONG).show();

                           Intent homeIntentsignup = new Intent(SignUp.this,SignIn.class);
                           homeIntentsignup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(homeIntentsignup);
                            finish();

                        }
                        }


                    @Override
                    public void onCancelled (@NonNull DatabaseError databaseError){

                    }
                });
            }
                else {
                    Toast.makeText(SignUp.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return ;
                }
            }
        });
    }
}
