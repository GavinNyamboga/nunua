package com.dev.nunua.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.dev.nunua.Prevalent.Prevalent;
import com.dev.nunua.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecycleActivity extends AppCompatActivity {
    private Button recycle, redeemPoints;
    private TextView points;


    DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);



        recycle = findViewById(R.id.scan_recycle_btn);
        redeemPoints = findViewById(R.id.redeem_points_btn);
        points = findViewById(R.id.points_recycle);

        recycle.setOnClickListener(v -> {
            Intent intent = new Intent(RecycleActivity.this, ScanRecycleActivity.class);
            startActivity(intent);


        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference reference = UsersRef.child(Prevalent.currentOnlineUsers.getPhone());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("points").exists()){

                    String points2 = dataSnapshot.child("points").getValue().toString();

                    points.setText(points2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
}
}
