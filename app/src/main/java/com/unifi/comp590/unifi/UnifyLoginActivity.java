package com.unifi.comp590.unifi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class UnifyLoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mNewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unify_login);
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

    }

}
