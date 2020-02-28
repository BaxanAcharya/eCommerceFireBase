package com.example.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName;
    private Button add_new_product;
    private EditText input_product_name,input_product_desc,input_product_price;
    private ImageView input_product_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        add_new_product=findViewById(R.id.add_new_product);
        input_product_image=findViewById(R.id.select_product_image);
        input_product_name=findViewById(R.id.product_name);
        input_product_desc=findViewById(R.id.product_description);
        input_product_price=findViewById(R.id.product_price);


        CategoryName = getIntent().getExtras().getString("category").toString();
        Toast.makeText(this, ""+CategoryName, Toast.LENGTH_SHORT).show();


    }
}
