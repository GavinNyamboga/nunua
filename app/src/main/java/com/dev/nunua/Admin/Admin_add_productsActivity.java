package com.dev.nunua.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.nunua.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Admin_add_productsActivity extends AppCompatActivity {

    private static final int GalleryPick = 1;
    private String categoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductBtn;
    private ImageView InputProductImage;
    private Spinner spinner;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String ProductRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_products);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        AddNewProductBtn = findViewById(R.id.add_new_product);
        InputProductName = findViewById(R.id.product_name);
        InputProductDescription = findViewById(R.id.product_description);
        InputProductPrice = findViewById(R.id.product_price);
        InputProductImage = findViewById(R.id.select_product_image);
        spinner = findViewById(R.id.category);

        String[] category = new String[]{
                "Phones", "laptops", "headphones", "food"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.arrayadapter, category);
        adapter.setDropDownViewResource(R.layout.arrayadapter);
        spinner.setAdapter(adapter);


        loadingBar = new ProgressDialog(this);
        

        InputProductImage.setOnClickListener(view -> OPenGallery());

        AddNewProductBtn.setOnClickListener(view -> ValidateProductData());


    }


    private void OPenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {

            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData() {
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();
        categoryName = spinner.getSelectedItem().toString();


        if (ImageUri == null) {
            Toast.makeText(this, "Product Image is Mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            InputProductDescription.setError("Please write the product description");
        } else if (TextUtils.isEmpty(Price)) {
            InputProductPrice.setError("Please enter the product price");
        } else if (TextUtils.isEmpty(Pname)) {
            InputProductName.setError("Please write the product name");

        } else {
            StoreProductInformation();
        }

    }

    private void StoreProductInformation() {
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("chill kiasi....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        ProductRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + ProductRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(e -> {
            String message = e.toString();
            Toast.makeText(Admin_add_productsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();

        }).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(Admin_add_productsActivity.this, "Product Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                downloadImageUrl = filePath.getDownloadUrl().toString();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    downloadImageUrl = task.getResult().toString();
                    Toast.makeText(Admin_add_productsActivity.this, "got the Product image url Successfully...", Toast.LENGTH_SHORT).show();

                    saveProductInfoToDatabase();
                }
            });
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", ProductRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);

        ProductsRef.child(ProductRandomKey).updateChildren(productMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(Admin_add_productsActivity.this, "Product is added successfully...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Admin_add_productsActivity.this, AdminHomeActivity.class);
                        startActivity(intent);
                    } else {
                        loadingBar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(Admin_add_productsActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


