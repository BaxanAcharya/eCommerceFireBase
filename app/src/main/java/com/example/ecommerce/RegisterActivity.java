package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button create_account;
    private EditText name, number, password;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        create_account = findViewById(R.id.register_btn);
        name = findViewById(R.id.register_username_input);
        number = findViewById(R.id.register_phone_no_input);
        password = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String username = name.getText().toString();
        String phoneno = number.getText().toString();
        String passworD = password.getText().toString();

        if (TextUtils.isEmpty(username)) {
            name.setError("Please enter Name");
        } else if (TextUtils.isEmpty(phoneno)) {
            number.setError("Please enter Phone number");
        } else if (TextUtils.isEmpty(passworD)) {
            password.setError("Please enter Password");
        } else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait while we are checking credentails");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(username, phoneno, passworD);
        }
    }

    private void ValidatePhoneNumber(final String username, final String phoneno, final String passworD) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phoneno).exists())) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phoneno);
                    userDataMap.put("password", passworD);
                    userDataMap.put("name", username);
                    rootRef.child("Users").child(phoneno).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Your account has been created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Nwetwork error try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "This " + phoneno + " already exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone no", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
