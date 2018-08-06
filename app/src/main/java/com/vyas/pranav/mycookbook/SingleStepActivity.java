package com.vyas.pranav.mycookbook;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;
import com.vyas.pranav.mycookbook.modelsutils.MainStepsModel;
import com.vyas.pranav.mycookbook.ui.SingleStepFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter.KEY_ALL_STEPS;
import static com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter.KEY_CURRENT_STEP;
import static com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter.KEY_STEP_SINGLE;

public class SingleStepActivity extends AppCompatActivity {

    int currentStep = -1;
    FragmentManager fragManager;
    Gson gson;
    List<MainStepsModel> listSteps;
    SingleStepFragment stepFragment;
    Button btnNext,btnPrevious;
    Boolean isLandScape;
    public static final String KEY_SAVED_CURRENT_POS = "LastSavedCurrentStep";
    public static final String KEY_FIRST_RUN_RECEPIE = "FirstTimeRunRecepie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isLandScape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if(savedInstanceState != null){
            currentStep = savedInstanceState.getInt(KEY_SAVED_CURRENT_POS);
            //Toast.makeText(this, "Current Pos is changed and is "+currentStep, Toast.LENGTH_SHORT).show();
        }else{
            currentStep = getIntent().getIntExtra(KEY_CURRENT_STEP,-1);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_step);
        btnNext = findViewById(R.id.btn_next_step);
        btnPrevious = findViewById(R.id.btn_prev_step);
        gson = new Gson();
        fragManager = getSupportFragmentManager();
        if(getIntent().hasExtra(KEY_ALL_STEPS)) {
            String allStepsJson = getIntent().getStringExtra(KEY_ALL_STEPS);
            Type typeOfMenu = new TypeToken<ArrayList<MainStepsModel>>(){}.getType();
            listSteps = gson.fromJson(allStepsJson, typeOfMenu);
        }else{
            Toast.makeText(this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
        sendStepsDataToFragment(currentStep,listSteps);
//        if(isLandScape){
//            btnPrevious.setVisibility(View.GONE);
//            btnNext.setVisibility(View.GONE);
//        }else{
//            btnPrevious.setVisibility(View.VISIBLE);
//            btnNext.setVisibility(View.VISIBLE);
//        }
    }

    public void sendStepsDataToFragment(int stepNo, List<MainStepsModel> steps){
        Bundle bundle = new Bundle();
        stepFragment = new SingleStepFragment();
        String singleStepJson = gson.toJson(steps.get(stepNo));
        bundle.putString(KEY_STEP_SINGLE, singleStepJson);
        stepFragment.setArguments(bundle);
        fragManager.beginTransaction()
                .replace(R.id.frame_main_step,stepFragment)
                .commit();
        checkAndSetButtons();
    }

    public void nextStepButton(View view) {
        if(availableNextFragment()){
            currentStep++;
            sendStepsDataToFragment(currentStep,listSteps);
        }else{
            Toast.makeText(this, "Exceeds no of Steps", Toast.LENGTH_SHORT).show();
        }
    }

    public void previousStepButton(View view) {
        if(availablePreviousFragment()){
            currentStep--;
            sendStepsDataToFragment(currentStep,listSteps);
        }else{
            Toast.makeText(this, "Less than no of Steps", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean availableNextFragment(){
        if(currentStep >= 0 && currentStep < listSteps.size() - 1) return true;
        else return false;
    }

    public boolean availablePreviousFragment(){
        if(currentStep > 0) return true;
        else return false;
    }

    public void checkAndSetButtons(){
        if(availableNextFragment()) btnNext.setEnabled(true);
        else btnNext.setEnabled(false);

        if(availablePreviousFragment()) btnPrevious.setEnabled(true);
        else btnPrevious.setEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SAVED_CURRENT_POS,currentStep);
        outState.putBoolean(KEY_FIRST_RUN_RECEPIE,true);
    }
}