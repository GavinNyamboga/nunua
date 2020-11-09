package com.dev.nunua.Users;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.Admin.AdminHomeActivity;
import com.dev.nunua.Model.Users;
import com.dev.nunua.Prevalent.Prevalent;
import com.dev.nunua.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText loginPhone, loginPassword;
    private Button LoginButton;
    private TextView AdminLink, NotAdminLink, registerLink;

    private SweetAlertDialog pDialog;
    private String parentDBName = "Users";
    private CheckBox RememberMeChkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginButton = findViewById(R.id.login_button);
        loginPhone = findViewById(R.id.login_phone);
        loginPassword = findViewById(R.id.login_password);
        AdminLink = findViewById(R.id.admin_link);
        NotAdminLink = findViewById(R.id.not_admin_link);
        registerLink = findViewById(R.id.register_link);
        RememberMeChkBox = findViewById(R.id.remember_me);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        Paper.init(this);

        LoginButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        AdminLink.setOnClickListener(view -> {
            LoginButton.setText("Login Admin");
            AdminLink.setVisibility(View.INVISIBLE);
            NotAdminLink.setVisibility(View.VISIBLE);
            RememberMeChkBox.setVisibility(View.INVISIBLE);
            parentDBName = "Admins";
        });

        NotAdminLink.setOnClickListener(view -> {
            LoginButton.setText("Login");
            AdminLink.setVisibility(View.VISIBLE);
            NotAdminLink.setVisibility(View.INVISIBLE);
            parentDBName = "Users";

        });


    }

    private void loginUser() {
        String phone = loginPhone.getText().toString();
        String password = loginPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            loginPhone.setError("please enter your phone number");

        } else if (TextUtils.isEmpty(password)) {
            loginPassword.setError("please enter your password");

        } else {
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Logging you in...");
            pDialog.setCancelable(false);
            pDialog.show();


            AllowAccess(phone, password);
        }
    }

    private void AllowAccess(String phone, String password) {
        if (RememberMeChkBox.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(phone).exists()) {

                    Users usersData = dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            if (parentDBName.equals("Admins")) {

                                Toast.makeText(LoginActivity.this, "logged in successfully as an admin", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                            } else if (parentDBName.equals("Users")) {

                                Toast.makeText(LoginActivity.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUsers = usersData;
                                startActivity(intent);
                            }


                        } else {
                            pDialog.dismiss();
                            loginPassword.setError("password is incorrect");

                        }
                    }
                } else {
                    loginPhone.setError("Account does not exist");
                    pDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
