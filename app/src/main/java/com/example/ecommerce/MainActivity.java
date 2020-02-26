package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.model.Users;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button btnlogin,join_now;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnlogin=findViewById(R.id.main_login_btn);
        join_now=findViewById(R.id.main_jowin_now);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);
        String userPhoneKey=Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey=Paper.book().read(Prevalent.userPasswordKey);
        if (userPhoneKey!="" && userPasswordKey!=""){
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)){
                AllowAccess(userPasswordKey,userPhoneKey);
                loadingBar.setTitle("Already Logged");
                loadingBar.setMessage("Please wait.......");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        join_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AllowAccess(final String passworD, final String phoneno) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phoneno).exists()) {
                    Users usersData = dataSnapshot.child("Users").child(phoneno).getValue(Users.class);
                    if (usersData.getPhone().equals(phoneno)) {
                        if (usersData.getPassword().equals(passworD)) {
                            Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity.this, "Password credentials is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Account with " + phoneno + " doesnt exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "You need to create a new account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
