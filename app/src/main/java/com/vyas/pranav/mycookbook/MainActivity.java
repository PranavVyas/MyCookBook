package com.vyas.pranav.mycookbook;

import android.Manifest;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.vyas.pranav.mycookbook.ui.MainListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.toolbar_main) Toolbar toolbarMain;
    @BindView(R.id.text_toolbar_main) TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvTitle.setText("My Cook Book");
        new Thread(new Runnable() {
            @Override public void run() {
                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.INTERNET)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Toast.makeText(MainActivity.this, "Granted", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        })
                        .withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {

                            }
                        })
                        .onSameThread()
                        .check();
            }
        }).start();
        FragmentManager fragManager = getSupportFragmentManager();
        MainListFragment recepieFragment = new MainListFragment();
        fragManager.beginTransaction()
                .replace(R.id.frame_main, recepieFragment)
                .commit();
    }
}
