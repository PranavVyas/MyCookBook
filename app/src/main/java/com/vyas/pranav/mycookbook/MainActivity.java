package com.vyas.pranav.mycookbook;

import android.graphics.Color;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.toolbar_main) Toolbar toolbarMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbarMain.setTitle("My Cook Book");
        toolbarMain.setTitleTextColor(Color.WHITE);
        FragmentManager fragManager = getSupportFragmentManager();
        MainListFragment recepieFragment = new MainListFragment();
        fragManager.beginTransaction()
                .replace(R.id.frame_main, recepieFragment)
                .commit();
    }
}
