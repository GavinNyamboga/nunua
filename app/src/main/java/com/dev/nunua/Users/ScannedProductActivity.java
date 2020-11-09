package com.dev.nunua.Users;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.nunua.R;
import com.dev.nunua.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ScannedProductActivity extends AppCompatActivity {
    Boolean isOpen = false;
    private EditText productID;
    private Button searchBtn;
    private RecyclerView scannedList;
    private String ScanInput;
    private FloatingActionButton fab_main, fab2_home, fab1_scan;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_product);

        productID = findViewById(R.id.scanned_product_name);
        scannedList = findViewById(R.id.scanned_list);
        searchBtn = findViewById(R.id.scanned_product_btn);
        scannedList.setLayoutManager(new GridLayoutManager(ScannedProductActivity.this, 1));


        ScanInput = getIntent().getStringExtra("product_id");
        productID.setText(ScanInput);

        searchBtn.setOnClickListener(view -> {
            ScanInput = productID.getText().toString();

            onStart();

        });
        productID.setVisibility(View.GONE);
        searchBtn.setVisibility(View.GONE);


        fab_main = findViewById(R.id.fab_main);
        fab1_scan = findViewById(R.id.fab1);
        fab2_home = findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);


        fab_main.setOnClickListener(view -> {
            if (isOpen) {
                fab2_home.startAnimation(fab_close);
                fab1_scan.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab1_scan.setClickable(false);
                fab2_home.setClickable(false);
                isOpen = false;
            } else {
                fab2_home.startAnimation(fab_open);
                fab1_scan.startAnimation(fab_open);
                fab_main.startAnimation(fab_clock);
                fab1_scan.setClickable(true);
                fab2_home.setClickable(true);

                isOpen = true;
            }
        });

        fab1_scan.setOnClickListener(view -> {
            Intent intent = new Intent(ScannedProductActivity.this, ScanBarcodeActivity.class);
            startActivity(intent);
        });
        fab2_home.setOnClickListener(view -> {
            Intent intent = new Intent(ScannedProductActivity.this, HomeActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");


        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference.orderByChild("pid").equalTo(ScanInput), Products.class)
                        .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price = Ksh " + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(view -> {
                            Intent intent = new Intent(ScannedProductActivity.this, ScannedDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        });


                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;


                    }
                };
        scannedList.setAdapter(adapter);
        adapter.startListening();
    }
}
