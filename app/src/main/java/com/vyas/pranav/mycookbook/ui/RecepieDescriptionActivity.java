package com.vyas.pranav.mycookbook.ui;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;
import com.vyas.pranav.mycookbook.modelsutils.MainStepsModel;
import com.vyas.pranav.mycookbook.ui.RecepieDescriptionFragment;
import com.vyas.pranav.mycookbook.ui.SingleStepFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vyas.pranav.mycookbook.recyclerutils.MainListAdapter.KEY_SINGLE_RECEPIE_JSON;
import static com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter.KEY_STEP_SINGLE;

public class RecepieDescriptionActivity extends AppCompatActivity implements RecepieDescriptionFragment.getObject,RecepieDescriptionFragment.ItemClicked{
    private static final String TAG = "RecepieDescriptionActiv";
    @BindView(R.id.toolbar_desc) Toolbar toolbarDesc;
    @BindView(R.id.text_toolbar_dsec) TextView tvTitle;
    private Boolean twoPane;
    public static final String KEY_BOOLEAN_TWO_PANE = "TwoPane";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepie_description);
        ButterKnife.bind(this);
        //Check for two Pane Laytout
        if(findViewById(R.id.divider) != null){
            twoPane = true;
        }else{
            twoPane = false;
        }
        RecepieDescriptionFragment recepieFragment = new RecepieDescriptionFragment();
        FragmentManager fragManager = getSupportFragmentManager();
        if (getIntent().hasExtra(KEY_SINGLE_RECEPIE_JSON)){
            Bundle bundle = new Bundle();
            bundle.putString(KEY_SINGLE_RECEPIE_JSON,getIntent().getStringExtra(KEY_SINGLE_RECEPIE_JSON));
            bundle.putBoolean(KEY_BOOLEAN_TWO_PANE,twoPane);
            recepieFragment.setArguments(bundle);
            fragManager.beginTransaction()
                    .replace(R.id.frame_recepie_recipie_description,recepieFragment)
                    .commit();
        }else{
            Toast.makeText(this, "Error Occured No Intent Received", Toast.LENGTH_SHORT).show();
        }
        setSupportActionBar(toolbarDesc);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void ObjectReceived(MainRecepieModel recepieModel) {
        tvTitle.setText(recepieModel.getName());
    }

    @Override
    public void itemClicked(int stepNo, String StepJsonFull) {
        if(twoPane){
            //Toast.makeText(this, "Reached at End Bro", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "itemClicked: stepNo :"+stepNo);
            Gson gson = new Gson();
            Bundle bundle = new Bundle();
            Type typeOfMenu = new TypeToken<ArrayList<MainStepsModel>>(){}.getType();
            List<MainStepsModel> steps = gson.fromJson(StepJsonFull,typeOfMenu);
            SingleStepFragment stepFragment = new SingleStepFragment();
            String singleStepJson = gson.toJson(steps.get(stepNo));
            Log.d(TAG, "itemClicked: singleStepJson : "+singleStepJson);
            bundle.putBoolean(KEY_BOOLEAN_TWO_PANE,twoPane);
            bundle.putString(KEY_STEP_SINGLE, singleStepJson);
            stepFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_main_step,stepFragment)
                    .commit();
        }
    }
}
