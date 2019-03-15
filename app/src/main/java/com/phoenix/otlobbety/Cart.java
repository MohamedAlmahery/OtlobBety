package com.phoenix.otlobbety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Model.MyResponse;
import com.phoenix.otlobbety.Model.Notification;
import com.phoenix.otlobbety.Model.Order;
import com.phoenix.otlobbety.Model.Request;
import com.phoenix.otlobbety.Model.Sender;
import com.phoenix.otlobbety.Model.Token;
import com.phoenix.otlobbety.Remote.APIService;
import com.phoenix.otlobbety.ViewHolder.CartAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice , noThingtext;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Service
        mApiService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        noThingtext = (TextView)findViewById(R.id.Nothingtext);
        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace = (FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size() > 0) {
                    showAlertDialog();
                }
                else {
                    Toast.makeText(Cart.this, "Your Cart is Empty !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadListFood();
        
    }

    private void showAlertDialog() {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
            alertDialog.setTitle("One more step!");
            alertDialog.setMessage("Enter your address");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);

        final MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

            alertDialog.setView(order_address_comment);
            alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Create new Request
                    Request request = new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            edtAddress.getText().toString(),
                            txtTotalPrice.getText().toString(),
                            "0", //Status
                            edtComment.getText().toString(),
                            cart
                    );
                    //Submit to Firebase
                    //We will using System.CurrentMill to key
                    String order_number = String.valueOf(System.currentTimeMillis());
                    requests.child(order_number)
                            .setValue(request);
                    //Delete Cart
                    new Database(getBaseContext()).cleanCart();

                    sendNotificationOrder(order_number);
                    Toast.makeText(Cart.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                   finish();
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog.show();

    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");
        Query data = tokens.orderByChild("serverToken").equalTo(true); //get all node with "isServerToken"
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Token serverToken = postSnapshot.getValue(Token.class);

                    Notification notification = new Notification("OtlobBety","You have new Order"+order_number);
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mApiService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    Toast.makeText(Cart.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                                    new Database(getBaseContext()).cleanCart();
                                    finish();

                                   /* if (response.body().success == 1)
                                    {
                                        Toast.makeText(Cart.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                                        new Database(getBaseContext()).cleanCart();
                                        finish();
                                    }else
                                    {
                                        Toast.makeText(Cart.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                                    }*/
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("Error",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calcuate total price
        float total = 0;
        int shippingPrice = 12;
        for (Order order : cart)
            total+=(Float.parseFloat(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

        Locale local = new Locale("ar","rEG"); // غيرها للعربي عشان متنساش
        NumberFormat fmt  = NumberFormat.getCurrencyInstance(local);

        txtTotalPrice.setVisibility(View.GONE);
        noThingtext.setVisibility(View.GONE);
        if(cart.size() > 0) {
            txtTotalPrice.setVisibility(View.VISIBLE);
            noThingtext.setVisibility(View.VISIBLE);
        }
        txtTotalPrice.setText(fmt.format(total + shippingPrice));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for (Order item:cart)
            new Database(this).addToCart(item);
        loadListFood();
    }
}