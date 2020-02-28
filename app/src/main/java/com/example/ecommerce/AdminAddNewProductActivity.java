package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, desceription, price, pname, saveCurrentDate, saveCurrentTime;
    private Button add_new_product;
    private EditText input_product_name, input_product_desc, input_product_price;
    private ImageView input_product_image;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private ProgressDialog loadingBar;
    private String productRamdomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(this);
        add_new_product = findViewById(R.id.add_new_product);
        input_product_image = findViewById(R.id.select_product_image);
        input_product_name = findViewById(R.id.product_name);
        input_product_desc = findViewById(R.id.product_description);
        input_product_price = findViewById(R.id.product_price);

        add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

        input_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


        CategoryName = getIntent().getExtras().getString("category").toString();
        Toast.makeText(this, "" + CategoryName, Toast.LENGTH_SHORT).show();


    }

    private void ValidateProductData() {
        desceription = input_product_desc.getText().toString();
        price = input_product_price.getText().toString();
        pname = input_product_name.getText().toString();
        if (imageUri == null) {
            Toast.makeText(this, "Product Image is mandetaory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(desceription)) {
            input_product_desc.setError("Please enter Description");
        } else if (TextUtils.isEmpty(price)) {
            input_product_price.setError("Please enter Price");
        } else if (TextUtils.isEmpty(pname)) {
            input_product_name.setError("Please enter Name");
        } else {
            storeProductInfo();
        }
    }

    private void storeProductInfo() {
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Dear Admin,Please wait while we are adding the new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = timeFormat.format(calendar.getTime());

        productRamdomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImagesRef.child(imageUri.getLastPathSegment() + productRamdomKey+".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product image uploaded successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "getting product image Url successfully", Toast.LENGTH_SHORT).show();
                            SaveProductInfoInFirebase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoInFirebase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRamdomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", desceription);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", price);
        productMap.put("pname", pname);
        ProductRef.child(productRamdomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent=new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                    startActivity(intent);
                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            input_product_image.setImageURI(imageUri);
        }
    }
}
