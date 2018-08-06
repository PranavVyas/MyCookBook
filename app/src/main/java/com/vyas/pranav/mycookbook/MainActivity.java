package com.vyas.pranav.mycookbook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;
import com.vyas.pranav.mycookbook.ui.MainListFragment;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    FrameLayout frameMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameMain = findViewById(R.id.frame_main);
        FragmentManager fragManager = getSupportFragmentManager();
        MainListFragment recepieFragment = new MainListFragment();
        fragManager.beginTransaction()
                .replace(R.id.frame_main, recepieFragment)
                .commit();
    }
}
