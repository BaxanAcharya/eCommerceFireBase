package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
//        Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();

        CategoryName = getIntent().getExtras().getString("category").toString();
        Toast.makeText(this, ""+CategoryName, Toast.LENGTH_SHORT).show();


    }
}
