package com.vyas.pranav.mycookbook.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.modelsutils.MainIngrediantsModel;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;
import com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vyas.pranav.mycookbook.recyclerutils.MainListAdapter.KEY_SINGLE_RECEPIE_JSON;

public class RecepieDescriptionFragment extends Fragment {

    @BindView(R.id.rv_details_frag_recepie_desc) RecyclerView rvDesc;
    private RecepieDescAdapter mAdapter;
    private LinearLayout ingrediantsContainer;
    MainRecepieModel recepie;
    List<MainIngrediantsModel> ingrediants;

    public RecepieDescriptionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recepie_description, container, false);
        ButterKnife.bind(this,view);
        ingrediantsContainer = view.findViewById(R.id.linear_container_ingrediants_recepie_desc);
        String recepieJson = getArguments().getString(KEY_SINGLE_RECEPIE_JSON);
        Gson gson = new Gson();
        recepie = gson.fromJson(recepieJson, MainRecepieModel.class);
        ingrediants = recepie.getIngredients();
        for (MainIngrediantsModel ingrediant_x : ingrediants){
            TextView tv = new TextView(getActivity());
            String ingrediantDetail = ""+ingrediant_x.getIngredient()+" : "+ingrediant_x.getQuantity()+" "+ingrediant_x.getMeasure();
            tv.setText(ingrediantDetail);
            tv.setGravity(Gravity.CENTER);
            ingrediantsContainer.addView(tv);
        }
        mAdapter = new RecepieDescAdapter(getActivity());
        mAdapter.setRecepie(recepie);
        rvDesc.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvDesc.setLayoutManager(layoutManager);
        return view;
    }
}
