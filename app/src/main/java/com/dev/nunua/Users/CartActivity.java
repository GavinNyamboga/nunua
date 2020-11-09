package com.dev.nunua.Users;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.nunua.Model.Cart;
import com.dev.nunua.Prevalent.Prevalent;
import com.dev.nunua.R;
import com.dev.nunua.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextBtn;
    private TextView txtTotalAmount, txtMsg1;

    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextBtn = findViewById(R.id.next_btn);
        txtTotalAmount = findViewById(R.id.total_price);
        txtMsg1 = findViewById(R.id.msg1);


    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUsers.getPhone())
                                .child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder holder, int position, @NonNull final Cart model) {


                holder.txtProductQuantity.setText("Quantity = " + model.getQuantity());
                holder.txtProductName.setText("Price = ksh." + model.getPrice());
                holder.txtProductPrice.setText(model.getPname());
                Picasso.get().load(model.getImage()).into(holder.productImage);


                int oneTypeProductTPrice = ((Integer.parseInt(model.getPrice()))) * Integer.parseInt(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTypeProductTPrice;


                NumberFormat format = NumberFormat.getInstance();
                format.setMaximumFractionDigits(1);
                txtTotalAmount.setText("Total price = ksh " + format.format(Integer.valueOf(overTotalPrice)));


                //Edit and delete items in cart


                holder.deleteItem.setOnClickListener(view -> {
                    CharSequence[] options1 = new CharSequence[]
                            {
                                    "Yes",
                                    "No"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setTitle("Are you sure you want to delete this product?");

                    builder.setItems(options1, (dialogInterface, i) -> {
                        if (i == 0) //admin presses yes
                        {

                            cartListRef.child("User View")
                                    .child(Prevalent.currentOnlineUsers.getPhone())
                                    .child("Products")
                                    .child(model.getPid())
                                    .removeValue()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();

                                            finish();
                                            startActivity(getIntent());
                                        }

                                    });

                        } else //admin presses no
                        {
                            Intent intent = new Intent(CartActivity.this, CartActivity.class);
                            startActivity(intent);

                        }

                    });
                    builder.show();
                });


                holder.editItem.setOnClickListener(v -> {
                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("pid", model.getPid());
                    startActivity(intent);
                });

                NextBtn.setOnClickListener(view -> {


                    Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                    intent.putExtra("pid", model.getPid());
                    startActivity(intent);
                    finish();
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
