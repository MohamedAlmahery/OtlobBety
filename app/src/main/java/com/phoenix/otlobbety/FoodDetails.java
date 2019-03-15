package com.phoenix.otlobbety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Database.Database;
import com.phoenix.otlobbety.Model.Food;
import com.phoenix.otlobbety.Model.Order;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {

    TextView foodname,foodprice,fooddescription,fooddiscount;
    ImageView foodimage;
    CollapsingToolbarLayout cooCollapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");

        //InitView
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
            ));
                Toast.makeText(FoodDetails.this, "Added to Cart", Toast.LENGTH_SHORT).show();

            }
        });

        fooddescription = (TextView)findViewById(R.id.food_description);
        foodname = (TextView)findViewById(R.id.food_name);
        foodprice= (TextView)findViewById(R.id.food_price);
        fooddiscount = (TextView) findViewById(R.id.food_discount);
        foodimage = (ImageView) findViewById(R.id.img_food);

        cooCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        cooCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsAppbar);
        cooCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsAppbar);

        //Get Food Id Intent
        if(getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if(! foodId.isEmpty())
        {
            if(Common.isConnectedToInternet(getBaseContext()))
                getDetailFood(foodId);
            else{
                Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void getDetailFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 currentFood = dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage())
                        .into(foodimage);

                cooCollapsingToolbarLayout.setTitle(currentFood.getName());

                foodprice.setText(currentFood.getPrice());
                foodname.setText(currentFood.getName());
                fooddescription.setText(" الوصف : "+currentFood.getDescription());
                fooddiscount.setPaintFlags(fooddiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                fooddiscount.setText(currentFood.getDiscount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
