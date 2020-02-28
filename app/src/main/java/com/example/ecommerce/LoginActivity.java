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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.model.Users;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText phone, password;
    private Button login;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    RadioButton radioButton;
    private TextView admin,notadmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        admin=findViewById(R.id.admin_panel_link);
        notadmin=findViewById(R.id.not_admin_panel_link);
        phone = findViewById(R.id.login_phone_no_input);
        password = findViewById(R.id.login_password_input);
        login = findViewById(R.id.login_btn);
        radioButton=findViewById(R.id.remember_me);
        Paper.init(this);
        loadingBar = new ProgressDialog(this);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login Admin");
                admin.setVisibility(View.INVISIBLE);
                notadmin.setVisibility(View.VISIBLE);
                parentDbName="Admins";
            }
        });

        notadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText("Login");
                admin.setVisibility(View.VISIBLE);
                notadmin.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String phoneno = phone.getText().toString();
        String passworD = password.getText().toString();

        if (TextUtils.isEmpty(phoneno)) {
            phone.setError("Please enter Phone number");
        } else if (TextUtils.isEmpty(passworD)) {
            password.setError("Please enter Password");
        } else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Login(phoneno, passworD);
        }
    }

    private void Login(final String phoneno, final String passworD) {
        if (radioButton.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey,phoneno);
            Paper.book().write(Prevalent.userPasswordKey,passworD);

        }
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phoneno).exists()) {
                    Users usersData = dataSnapshot.child(parentDbName).child(phoneno).getValue(Users.class);
                    if (usersData.getPhone().equals(phoneno)) {
                        if (usersData.getPassword().equals(passworD)) {
                           if (parentDbName.equals("Admins")){
                               System.out.println("sdfd");
                               Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                               Intent intent = new Intent(LoginActivity.this, AdminAddNewProductActivity.class);
                               startActivity(intent);
                           }else if (parentDbName.equals("Users")){
                               Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                               loadingBar.dismiss();
                               Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                               startActivity(intent);
                           }
                        }else{
                            Toast.makeText(LoginActivity.this, "Password credentials is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Account with " + phoneno + " doesnt exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "You need to create a new account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
