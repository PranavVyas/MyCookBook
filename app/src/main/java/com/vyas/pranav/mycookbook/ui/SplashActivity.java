package com.vyas.pranav.mycookbook.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vyas.pranav.mycookbook.R;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    public static final int SPLASH_TIME_OUT = 500;
//    public static final String PATH =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String path = this.getAssets().toString();
        Log.d(TAG, "onCreate: Path is "+path);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
