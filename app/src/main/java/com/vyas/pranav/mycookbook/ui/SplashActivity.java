package com.vyas.pranav.mycookbook.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vyas.pranav.mycookbook.MainActivity;
import com.vyas.pranav.mycookbook.R;

public class SplashActivity extends AppCompatActivity {
    public static final int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                //bar.smoothToHide();
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
