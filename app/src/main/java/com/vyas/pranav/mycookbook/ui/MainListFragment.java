package com.vyas.pranav.mycookbook.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;
import com.vyas.pranav.mycookbook.recyclerutils.MainListAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainListFragment extends Fragment{
    @BindView(R.id.rv_main_frag_main) RecyclerView rvMainList;
    private MainListAdapter mAdapter;
    private List<MainRecepieModel> mainRecepieModels;

    public MainListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list,container,false);
        ButterKnife.bind(this,view);
        mainRecepieModels = retriveJsonasList();
        boolean isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        mAdapter = new MainListAdapter(getContext());
        mAdapter.setRecepiesList(mainRecepieModels);
        rvMainList.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),isLandscape ? 2 : 1);
        rvMainList.setLayoutManager(layoutManager);
        return view;
    }

    public List<MainRecepieModel> retriveJsonasList(){
        String json;
        try {
            InputStream is = Objects.requireNonNull(getActivity()).getAssets().open("baking.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        List<MainRecepieModel> mainList = convertJsonToList(json);
        return mainList;
    }

    public List<MainRecepieModel> convertJsonToList(String jsonString){
        Gson gson = new Gson();
        Type typeOfMenu = new TypeToken<ArrayList<MainRecepieModel>>(){}.getType();
        List<MainRecepieModel> list = gson.fromJson(jsonString, typeOfMenu);
        return list;
    }

}
