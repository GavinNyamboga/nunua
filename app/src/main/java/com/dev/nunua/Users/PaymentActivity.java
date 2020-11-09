package com.dev.nunua.Users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.R;

public class PaymentActivity extends AppCompatActivity {
    private String totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Button mpesaBtn = findViewById(R.id.mpesa_pay);
        Button cardBtn = findViewById(R.id.card_pay);

        totalAmount = getIntent().getStringExtra("Total Price");


        mpesaBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, MpesaActivity.class);
            intent.putExtra("Amount payable", totalAmount);
            startActivity(intent);
        });
        cardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, StripeCheckoutActivity.class);
            intent.putExtra("Amount payable", totalAmount);
            startActivity(intent);
        });


    }
}