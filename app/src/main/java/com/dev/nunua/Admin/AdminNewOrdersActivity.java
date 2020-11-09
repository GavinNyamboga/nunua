package com.dev.nunua.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.nunua.Model.AdminOrders;
import com.dev.nunua.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    public String productId;
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);


        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");


        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef, AdminOrders.class)
                        .build();
        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {

                        holder.userName.setText("Name: " + model.getName());
                        holder.userPhoneNumber.setText("Phone: " + model.getPhone());
                        holder.userTotalPrice.setText("Total Amount = ksh " + model.getTotalAmount());
                        holder.userShippingAddress.setText("Shipping Address : " + model.getAddress() + "," + model.getCity());
                        holder.userDateTime.setText("Orders at : " + model.getDate() + " " + model.getTime());
                        holder.payment.setText(model.getPayment());

                        productId = model.getPid();

                        holder.showOrdersButton.setOnClickListener(view -> {

                            String uID = getRef(position).getKey();

                            Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                            intent.putExtra("uid", uID);
                            intent.putExtra("pid", productId);
                            startActivity(intent);
                        });


                        holder.itemView.setOnClickListener(view -> {
                            CharSequence[] options1 = new CharSequence[]
                                    {
                                            "Yes",
                                            "No"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                            builder.setTitle("Have you shipped products for this order?");
                            builder.setItems(options1, (dialogInterface, i) -> {
                                if (i == 0) //admin presses yes
                                {
                                    String uID = getRef(position).getKey();

                                    Shipped(uID);

                                } else //admin presses no
                                {
                                    String uID = getRef(position).getKey();

                                    NotShipped(uID);

                                }

                            });
                            builder.show();
                        });


                    }

                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);

                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void Shipped(String uID) {
        ordersRef.child(uID).child("state").setValue("Order Shipped");
    }

    private void NotShipped(String uID) {
        ordersRef.child(uID).child("state").setValue("Not Shipped");
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress, payment;
        public Button showOrdersButton;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            payment = itemView.findViewById(R.id.order_payment);
            showOrdersButton = itemView.findViewById(R.id.show_order_products_btn);
        }
    }

}
