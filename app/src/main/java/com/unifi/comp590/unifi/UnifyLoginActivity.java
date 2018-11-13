package com.unifi.comp590.unifi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

public class UnifyLoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mNewAccount;
    private Button mLogin;
    private EditText mLoginEmail;
    private EditText mLoginPassword;
    private FirebaseAuth mAuth;
    //    private ProgressDialog mLoading;
//    private AlertDialog mLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unify_login);
        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.activity_login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        mNewAccount = (Button) findViewById(R.id.no_account);
        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_account = new Intent(UnifyLoginActivity.this, NewAccountActivity.class);
                startActivity(new_account);
            }
        });
//        mLoading = new ProgressDialog(this);
        mLoginEmail = (EditText) findViewById(R.id.email_login);
        mLoginPassword = (EditText) findViewById(R.id.password_login);
        mLogin = (Button) findViewById(R.id.sign_in);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getText().toString();
                String password = mLoginPassword.getText().toString();

                LoginUserAccount(email, password);
            }
        });

    }

    private void LoginUserAccount(String email, String password) {

        AlertDialog.Builder builder = new AlertDialog.Builder(UnifyLoginActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_bar);
        final AlertDialog mLoading = builder.create();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(UnifyLoginActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(UnifyLoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();

        } else {

//            mLoading.setTitle("Logging In");
//            mLoading.setMessage("Please Wait!");
            mLoading.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(UnifyLoginActivity.this, UnifiActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UnifyLoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();

                    }
                    mLoading.dismiss();
                }
            });

        }
    }


}
