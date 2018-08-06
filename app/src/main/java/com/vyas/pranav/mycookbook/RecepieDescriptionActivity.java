package com.vyas.pranav.mycookbook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.vyas.pranav.mycookbook.ui.RecepieDescriptionFragment;

import static com.vyas.pranav.mycookbook.recyclerutils.MainListAdapter.KEY_SINGLE_RECEPIE_JSON;

public class RecepieDescriptionActivity extends AppCompatActivity {
    FrameLayout frame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepie_description);
        RecepieDescriptionFragment recepieFragment = new RecepieDescriptionFragment();
        FragmentManager fragManager = getSupportFragmentManager();
        frame = findViewById(R.id.frame_recepie_recipie_description);
        if (getIntent().hasExtra(KEY_SINGLE_RECEPIE_JSON)){
            Bundle bundle = new Bundle();
            bundle.putString(KEY_SINGLE_RECEPIE_JSON,getIntent().getStringExtra(KEY_SINGLE_RECEPIE_JSON));
            recepieFragment.setArguments(bundle);
            fragManager.beginTransaction()
                    .replace(R.id.frame_recepie_recipie_description,recepieFragment)
                    .commit();
        }else{
            Toast.makeText(this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
    }
}
