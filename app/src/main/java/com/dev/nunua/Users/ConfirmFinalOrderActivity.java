package com.dev.nunua.Users;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.Prevalent.Prevalent;
import com.dev.nunua.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;

    private String totalAmount = "", productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        productId = getIntent().getStringExtra("pid");


        confirmOrderBtn = findViewById(R.id.confrim_final_order_btn);

        nameEditText = findViewById(R.id.shipment_name);
        phoneEditText = findViewById(R.id.shipment_phone_number);
        addressEditText = findViewById(R.id.shipment_adress);
        cityEditText = findViewById(R.id.shipment_city);

        userInfoDisplay(nameEditText, phoneEditText, addressEditText, cityEditText);

        confirmOrderBtn.setOnClickListener(view -> check());
    }

    private void userInfoDisplay(final EditText nameEditText, final EditText phoneEditText, final EditText addressEditText, final EditText cityEditText) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUsers.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("name").exists()) {
                        String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                        String phone = Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString();
                        String address = Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString();
                        String city = Objects.requireNonNull(dataSnapshot.child("city").getValue()).toString();


                        nameEditText.setText(name);
                        phoneEditText.setText(phone);
                        addressEditText.setText(address);
                        cityEditText.setText(city);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void check() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            nameEditText.setError("Please provide your full name...");
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            phoneEditText.setError("Please provide your phone number..");
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            addressEditText.setError("Please provide the address to deliver to..");
        } else if (TextUtils.isEmpty(cityEditText.getText().toString())) {
            cityEditText.setError("Please provide your city/town name..");
        } else {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        final String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "Not shipped");
        ordersMap.put("pid", productId);
        ordersMap.put("payment", "not paid");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseDatabase.getInstance().getReference().child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentOnlineUsers.getPhone())
                        .removeValue()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {


                                Toast.makeText(ConfirmFinalOrderActivity.this, "your delivery details have been received", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, PaymentActivity.class);
                                intent.putExtra("Total Price", String.valueOf(totalAmount));
                                startActivity(intent);

                            }
                        });
            }

        });

    }
}
