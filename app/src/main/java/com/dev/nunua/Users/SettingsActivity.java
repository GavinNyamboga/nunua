package com.dev.nunua.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.Prevalent.Prevalent;
import com.dev.nunua.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullnameEditText, userPhoneEditText, addressEditText, cityEditText;
    private ImageView profileChangeTextBtn;

    private Uri imageUri;
    private String myurl = "";
    private StorageTask uploadTask;

    private StorageReference storageProfilePictureRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference("Profile Pictures");

        profileImageView = findViewById(R.id.settings_profile_image);
        fullnameEditText = findViewById(R.id.settings_full_name);
        userPhoneEditText = findViewById(R.id.settings_phone_number);
        addressEditText = findViewById(R.id.settings_address);
        cityEditText = findViewById(R.id.city_settings);
        profileChangeTextBtn = findViewById(R.id.change_profile_image_btn);
        Button closeTxtBtn = findViewById(R.id.close_settings);
        Button saveTextBtn = findViewById(R.id.update_account_settings);

        userInfoDisplay(profileImageView, fullnameEditText, userPhoneEditText, addressEditText, cityEditText);

        closeTxtBtn.setOnClickListener(view -> finish());

        saveTextBtn.setOnClickListener(view -> {
            if (checker.equals("clicked")) {
                userInfoSaved();
            } else {
                updateOnlyUserInfo();
            }
        });

        profileChangeTextBtn.setOnClickListener(view -> {
            checker = "clicked";


            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);


        });

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullnameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("phone", userPhoneEditText.getText().toString());
        userMap.put("city", cityEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Error, please try again...", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }


    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullnameEditText.getText().toString())) {
            fullnameEditText.setError("Name is mandatory...");
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            addressEditText.setError("enter address..");

        } else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())) {
            userPhoneEditText.setError("please enter phone number...");
        } else if (TextUtils.isEmpty(cityEditText.getText().toString())) {
            cityEditText.setError("please enter phone number...");
        } else if (checker.equals("clicked")) {
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog ProgressDialog = new ProgressDialog(this);
        ProgressDialog.setTitle("update profile");
        ProgressDialog.setMessage("please wait we are updating your profile...");
        ProgressDialog.setCanceledOnTouchOutside(false);
        ProgressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentOnlineUsers.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask((Continuation) task -> {

                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            })
                    .addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myurl = downloadUrl.toString();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", fullnameEditText.getText().toString());
                            userMap.put("address", addressEditText.getText().toString());
                            userMap.put("phone", userPhoneEditText.getText().toString());
                            userMap.put("city", cityEditText.getText().toString());
                            userMap.put("image", myurl);
                            ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap);

                            ProgressDialog.dismiss();

                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                            Toast.makeText(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            ProgressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "an Error occured...", Toast.LENGTH_SHORT).show();
                        }

                    });
        } else {
            Toast.makeText(this, "image is not selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullnameEditText, final EditText userPhoneEditText, final EditText addressEditText, final EditText cityEditText) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUsers.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String city = dataSnapshot.child("city").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnameEditText.setText(name);
                        userPhoneEditText.setText(phone);
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

}
