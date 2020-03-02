package com.dev.nunua.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dev.nunua.R;
import com.dev.nunua.Users.HomeActivity;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Button addProducts = findViewById(R.id.add_products_btn);
        Button checkOrders = findViewById(R.id.new_orders_btn);
        Button maintainProducts = findViewById(R.id.maintain_products_btn);

        addProducts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this,Admin_add_productsActivity.class);
            startActivity(intent);
        });
        maintainProducts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
            intent.putExtra("Admin","Admin");
            startActivity(intent);
        });
        checkOrders.setOnClickListener(v -> {
            Intent intent2 = new Intent(AdminHomeActivity.this,AdminNewOrdersActivity.class);
            startActivity(intent2);
        });


    }
}
