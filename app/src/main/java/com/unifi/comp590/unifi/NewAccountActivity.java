package com.unifi.comp590.unifi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class NewAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private EditText RegisterName;
    private EditText RegisterEmail;
    private EditText RegisterPassword;
    private Button ButtonSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        mToolbar = (Toolbar) findViewById(R.id.activity_new_account_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RegisterName = (EditText) findViewById(R.id.name_signup);
        RegisterEmail = (EditText) findViewById(R.id.email_signup);
        RegisterPassword = (EditText) findViewById(R.id.password_signup);
        ButtonSignUp = (Button) findViewById(R.id.sign_up);
        ButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = RegisterName.getText().toString();
                String email = RegisterEmail.getText().toString();
                String password = RegisterPassword.getText().toString();

                RegisterAccount(name, email, password);
            }
        });

    }

    private void RegisterAccount(String name, String email, String password) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(NewAccountActivity.this, "Please Enter your Name", Toast.LENGTH_LONG).show();

        } if (TextUtils.isEmpty(email)) {
            Toast.makeText(NewAccountActivity.this, "Please Enter your Email", Toast.LENGTH_LONG).show();

        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(NewAccountActivity.this, "Please Enter your Password", Toast.LENGTH_LONG).show();

        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(NewAccountActivity.this, UnifiActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(NewAccountActivity.this, "Error Occured, Try Again!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }
}
