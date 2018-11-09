package com.unifi.comp590.unifi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(1500);
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                }
                finally {
                    Intent UnifiIntent = new Intent(WelcomeActivity.this,UnifiActivity.class);
                    startActivity(UnifiIntent);
                    
                }
            }
        };
        thread.start();
    }
}
