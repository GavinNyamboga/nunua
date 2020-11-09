package com.dev.nunua.Users;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {

    long points = 0;
    private Button RegisterButton;
    private EditText InputName, InputPhone, InputPassword, confirmPass;
    private TextView loginLink;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterButton = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_name);
        InputPhone = findViewById(R.id.register_phone);
        InputPassword = findViewById(R.id.register_password);
        confirmPass = findViewById(R.id.confirm_pass);
        loginLink = findViewById(R.id.login_link);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        RegisterButton.setOnClickListener(view -> CreateAccount());

    }

    private void CreateAccount() {
        String name = InputName.getText().toString();
        String phone = InputPhone.getText().toString();
        String password = InputPassword.getText().toString();
        String confirmPassword = confirmPass.getText().toString();

        if (TextUtils.isEmpty(name)) {
            InputName.setError("please enter your name!");
        } else if (TextUtils.isEmpty(phone)) {
            InputPhone.setError("please enter your phone number");
        } else if (TextUtils.isEmpty(password)) {
            InputPassword.setError("please enter your password");

        } else if (TextUtils.isEmpty(confirmPassword)) {
            confirmPass.setError("Please confirm your password!");
        } else if (!confirmPassword.equals(password)) {
            confirmPass.setError("passwords do not match!");
        } else {
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Creating your account...");
            pDialog.setCancelable(false);
            pDialog.show();

            validatePhone(name, phone, password);
        }
    }

    private void validatePhone(String name, String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Users").child(phone).exists())) {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("name", name);
                    userdataMap.put("password", password);
                    userdataMap.put("points", points);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {


                                    Toast.makeText(RegisterActivity.this, "your account has been created", Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();

                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    pDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "This " + phone + " already exists", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    InputPhone.setError("please try again using a different phone number");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
