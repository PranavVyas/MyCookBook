package com.vyas.pranav.mycookbook.extrautils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

import java.util.ArrayList;
import java.util.List;

public class JsonDownloadAsyncTask extends AsyncTask<String,Void,List<MainRecepieModel>>{
    private static final String TAG = "JsonDownloadAsyncTask";
    Context context;
    dataReceived mCallback;
    String url;

    public JsonDownloadAsyncTask(Context context,dataReceived mCallback) {
        this.mCallback = mCallback;
        this.context = context;
    }

    @Override
    protected List<MainRecepieModel> doInBackground(String... strings) {
        url = strings[0];
        AndroidNetworking.initialize(context);
        ANRequest request = AndroidNetworking.get(url)
                .build();

        ANResponse<List<MainRecepieModel>> response = request.executeForObjectList(MainRecepieModel.class);
        List<MainRecepieModel> users = new ArrayList<>();
        if (response.isSuccess()) {
            users = response.getResult();
        } else {
            ANError error = response.getError();
            Log.d(TAG, "downloadJsonAsList: Error Occured : "+error.getErrorDetail());
        }
        return users;
    }

    @Override
    protected void onPostExecute(List<MainRecepieModel> mainRecepieModelList) {
        super.onPostExecute(mainRecepieModelList);
        mCallback.OnJsonListReceived(mainRecepieModelList);
    }

    public interface dataReceived{
        void OnJsonListReceived(List<MainRecepieModel> RecepieJsonList);
    }
}
