package com.vyas.pranav.mycookbook.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.extrautils.JsonDownloadAsyncTask;
import com.vyas.pranav.mycookbook.extrautils.NetworkUtils;
import com.vyas.pranav.mycookbook.extrautils.SharedPrefs;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;
import com.vyas.pranav.mycookbook.recyclerutils.MainListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainListFragment extends Fragment implements JsonDownloadAsyncTask.dataReceived{
    private static final String TAG = "MainListFragment";
    public static final String URL_DOWNLOAD_JSON = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    @BindView(R.id.rv_main_frag_main) RecyclerView rvMainList;
    @BindView(R.id.image_error_main) ImageView imageError;
    private MainListAdapter mAdapter;
    private BroadcastReceiver myReceiver;

    public MainListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list,container,false);
        ButterKnife.bind(this,view);
        boolean isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mAdapter = new MainListAdapter(getContext());
        rvMainList.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),isLandscape ? 2 : 1);
        rvMainList.setLayoutManager(layoutManager);
        checkConnectivity();
        if(SharedPrefs.isFirstTimeRun(getContext())){
            final Display display = getActivity().getWindowManager().getDefaultDisplay();
            final Drawable info = ContextCompat.getDrawable(getContext(), R.drawable.ic_info);
            final Rect infoTarget = new Rect(0, 0, info.getIntrinsicWidth() * 2, info.getIntrinsicHeight() * 2);
            infoTarget.offset(display.getWidth() / 2, display.getHeight() / 2);
            TapTargetView.showFor(getActivity(),
                    TapTarget.forBounds(infoTarget, "Setting Ingrediants in Widget", "This Process is automatic !\nJust Select the Recepie from Recepie List to set it on Homescreen Widget, It will automatically capture current selected recepie and Display it in widget")
                            .outerCircleColor(R.color.RED)
                            .outerCircleAlpha(0.96f)
                            .targetCircleColor(R.color.WHITE)
                            .titleTextSize(20)
                            .titleTextColor(R.color.WHITE)
                            .descriptionTextSize(16)
                            .descriptionTextColor(R.color.BLACK)
                            .textTypeface(Typeface.SANS_SERIF)
                            .dimColor(R.color.BLACK)
                            .drawShadow(true)
                            .icon(info)
                            .tintTarget(false)
                            .cancelable(false)
                            .targetRadius(50),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            Toast.makeText(getContext(), "Volia ! You Completed Tutorial !", Toast.LENGTH_SHORT).show();
                        }
                    });
            SharedPrefs.setFirstTimeRun(getContext(),false);
        }
        return view;
    }

    @Override
    public void OnJsonListReceived(List<MainRecepieModel> RecepieJsonList) {
        mAdapter.setRecepiesList(RecepieJsonList);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkConnectivity();
            }
        };
        getContext().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(myReceiver);
    }

    private void checkConnectivity() {
            if (!NetworkUtils.hasInternetConnection(getContext())) {
                Toast.makeText(getContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
                rvMainList.setVisibility(View.GONE);
                imageError.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "Connection Sucessfull", Toast.LENGTH_SHORT).show();
                rvMainList.setVisibility(View.VISIBLE);
                imageError.setVisibility(View.GONE);
                JsonDownloadAsyncTask downloadTask = new JsonDownloadAsyncTask(getContext(), this);
                downloadTask.execute(URL_DOWNLOAD_JSON);
            }
    }

    public class networkChangeReceiverMain extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkConnectivity();
            //Toast.makeText(context, "Network Changed", Toast.LENGTH_SHORT).show();
        }
    }
}
