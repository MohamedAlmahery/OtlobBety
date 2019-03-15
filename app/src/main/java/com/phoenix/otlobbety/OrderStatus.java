package com.phoenix.otlobbety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.phoenix.otlobbety.Common.Common;
import com.phoenix.otlobbety.Model.Request;
import com.phoenix.otlobbety.ViewHolder.OrderViewHolder;

import static com.phoenix.otlobbety.Common.Common.convertCodeToStatus;
import static com.phoenix.otlobbety.Common.Common.currentRequest;
import static com.phoenix.otlobbety.Common.Common.currentUser;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    CardView cardView;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView) findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        loadOrders(Common.currentUser.getPhone());

    }
    private void loadOrders(String phone) {
            adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                    Request.class, R.layout.order_layout, OrderViewHolder.class,

                    requests.orderByChild("phone").equalTo(phone)


            ) {
                @Override
                protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int position) {


                             orderViewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                             orderViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                             orderViewHolder.txtOrderAddress.setText(request.getAddress());
                             orderViewHolder.txtOrderPhone.setText(request.getPhone());



                }
            };
            recyclerView.setAdapter(adapter);
        }
}
