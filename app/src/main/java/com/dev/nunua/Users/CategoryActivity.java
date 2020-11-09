package com.dev.nunua.Users;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.dev.nunua.R;


public class CategoryActivity extends AppCompatActivity {
    private CardView phones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        phones = findViewById(R.id.phones_card);

        phones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(CategoryActivity.this, PhonesActivity.class);
                startActivity(intent);*/
            }
        });
    }
}
