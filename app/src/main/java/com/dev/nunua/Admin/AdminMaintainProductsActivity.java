package com.dev.nunua.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {
    private Button applyChangesBtn, deleteBtn;
    private EditText name, price, description;
    private ImageView imageView;

    private String productID = "";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);


        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        applyChangesBtn = findViewById(R.id.apply_changes_btn);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);
        deleteBtn = findViewById(R.id.delete_product_btn);

        imageView = findViewById(R.id.product_image_maintain);


        displayProductInfo();


        applyChangesBtn.setOnClickListener(view -> applyChanges());

        deleteBtn.setOnClickListener(view -> {

            CharSequence[] options = new CharSequence[]
                    {
                            "Yes",
                            "No"
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProductsActivity.this);
            builder.setTitle("Are you sure you want to delete this product?");
            builder.setItems(options, (dialogInterface, i) -> {
                if (i == 0) {

                    deleteThisProduct();

                } else {
                    //Intent intent = new Intent(AdminMaintainProductsActivity.this,AdminMaintainProductsActivity.class);
                    // startActivity(intent);
                    finish();

                }

            });
            builder.show();
        });

    }

    private void deleteThisProduct() {
        productsRef.removeValue().addOnCompleteListener(task -> {

            Intent intent = new Intent(AdminMaintainProductsActivity.this, Admin_add_productsActivity.class);
            startActivity(intent);
            finish();

            Toast.makeText(AdminMaintainProductsActivity.this, "The Product has been deleted successfully", Toast.LENGTH_SHORT).show();

        });
    }

    private void applyChanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();

        if (pName.equals("")) {
            name.setError("type product name...");

        } else if (pPrice.equals("")) {
            price.setError("type product price...");
        } else if (pDescription.equals("")) {
            description.setError("type product description...");
        } else {
            //update information to database on product info
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);

            productsRef.updateChildren(productMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminMaintainProductsActivity.this, "Changes Applied successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AdminMaintainProductsActivity.this, AdminHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void displayProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();

                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
