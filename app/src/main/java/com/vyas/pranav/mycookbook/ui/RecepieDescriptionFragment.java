package com.vyas.pranav.mycookbook.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.modelsutils.MainIngrediantsModel;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;
import com.vyas.pranav.mycookbook.recyclerutils.RecepieDescAdapter;

import java.util.List;
import java.util.Objects;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vyas.pranav.mycookbook.RecepieDescriptionActivity.KEY_BOOLEAN_TWO_PANE;
import static com.vyas.pranav.mycookbook.recyclerutils.MainListAdapter.KEY_SINGLE_RECEPIE_JSON;

public class RecepieDescriptionFragment extends Fragment {

    @BindView(R.id.rv_details_frag_recepie_desc) RecyclerView rvDesc;
    private RecepieDescAdapter mAdapter;
    @BindView(R.id.linear_container_ingrediants_recepie_desc) ExpandableTextView ingrediantsContainer;
    @BindView(R.id.button_toggle_ingrediants) Button btnToogle;
    private MainRecepieModel recepie;
    private List<MainIngrediantsModel> ingrediants;
    private getObject mCallback;
    private ItemClicked mItemClicked;
    boolean twoPane;

    public RecepieDescriptionFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (getObject) context;
        mItemClicked = (ItemClicked) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recepie_description, container, false);
        ButterKnife.bind(this,view);
        ingrediantsContainer.setInterpolator(new OvershootInterpolator());
        String recepieJson = Objects.requireNonNull(getArguments()).getString(KEY_SINGLE_RECEPIE_JSON);
        twoPane = getArguments().getBoolean(KEY_BOOLEAN_TWO_PANE);
        Gson gson = new Gson();
        recepie = gson.fromJson(recepieJson, MainRecepieModel.class);
        setIngrediants();
        setRecyclerView();
        mCallback.ObjectReceived(recepie);
        return view;
    }

    private void setRecyclerView(){
        mAdapter = new RecepieDescAdapter(getContext(),tapped);
        mAdapter.setRecepie(recepie);
        mAdapter.setTwoPaneLayout(twoPane);
        rvDesc.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvDesc.setLayoutManager(layoutManager);
    }

    private void setIngrediants(){
        ingrediants = recepie.getIngredients();
        int index = 1;
        String ingrediantsData = "";
        for (MainIngrediantsModel ingrediant_x : ingrediants){
            String ingrediantDetail = index+": <b>"+ingrediant_x.getIngredient()+"</b> : "+ingrediant_x.getQuantity()+" "+ingrediant_x.getMeasure();
            ingrediantsData = ingrediantsData+ingrediantDetail+"<br>";
            index++;
        }
        ingrediantsContainer.setText(Html.fromHtml(ingrediantsData));
        ingrediantsContainer.expand();
        btnToogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                btnToogle.setText(ingrediantsContainer.isExpanded() ? "Show All Ingrediants" : "Hide all Ingrediants");
                ingrediantsContainer.toggle();
            }
        });
    }

    public interface getObject{
        void ObjectReceived(MainRecepieModel recepieModel);
    }

    public interface ItemClicked{
        void itemClicked(int stepNo, String StepJsonFull);
    }

    RecepieDescAdapter.OnTapListener tapped = new RecepieDescAdapter.OnTapListener() {
        @Override
        public void OnItemClick(int stepNo, String StepJsonFull) {
            mItemClicked.itemClicked(stepNo,StepJsonFull);
        }
    };
}
