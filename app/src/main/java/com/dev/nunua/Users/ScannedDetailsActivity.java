package com.dev.nunua.Users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ScannedDetailsActivity extends AppCompatActivity {
    private ImageView productImage;
    private Button payButton;
    private TextView productPrice, productDescription, productName;
    private String productID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_details);
        productID = getIntent().getStringExtra("pid");


        payButton = findViewById(R.id.scanned_pay_button);
        productImage = findViewById(R.id.scanned_image_details);
        productPrice = findViewById(R.id.scanned_description_price);
        productDescription = findViewById(R.id.scanned_description_details);
        productName = findViewById(R.id.scanned_name_details);

        getProductDetails(productID);
    }

    private void getProductDetails(String productID) {

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference()
                .child("Products");
        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);


                    payButton.setOnClickListener(v -> {
                        Intent intent = new Intent(ScannedDetailsActivity.this, MpesaActivity.class);
                        intent.putExtra("Amount payable", products.getPrice());
                        startActivity(intent);
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
