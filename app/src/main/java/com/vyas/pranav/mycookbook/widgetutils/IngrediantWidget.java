package com.vyas.pranav.mycookbook.widgetutils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.blankj.ALog;
import com.google.gson.Gson;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.vyas.pranav.mycookbook.ui.MainActivity;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.ui.RecepieDescriptionActivity;
import com.vyas.pranav.mycookbook.extrautils.SharedPrefs;
import com.vyas.pranav.mycookbook.modelsutils.MainIngrediantsModel;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

import java.util.List;

import static com.vyas.pranav.mycookbook.recyclerutils.MainListAdapter.KEY_SINGLE_RECEPIE_JSON;
public class IngrediantWidget extends AppWidgetProvider {
    private static final String TAG = "IngrediantWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        MainRecepieModel recepie;
        List<MainIngrediantsModel> ingrediantsList;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingrediant_widget);
        String ingrediants = "";
        String recepieName;
        String recepieJson;

        if(!SharedPrefs.isRecepieAddedAlready(context)){
            ingrediants = "No Recepie Selected\nPlease select Recepie From App Now";
            views.setTextViewText(R.id.text_ingrediants_title_widget,ingrediants);
        }else{
            //Gson gson = new Gson();
            //recepieJson = SharedPrefs.getCurrentRecepieFromPrefs(context);
            //recepie = gson.fromJson(recepieJson, MainRecepieModel.class);
            //views.setTextViewText(R.id.text_recepie_title_widget,recepie.getName());
            Intent intent = new Intent(context, WidgetUpdateService.class);
            views.setRemoteAdapter(R.id.listViewWidget, intent);
            Log.d(TAG, "updateAppWidget: Updating widget Now...");
            //To Open Activity Directly from Widget
        }
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntentToOpenActivity = PendingIntent.getActivity(context,0,intent,0);
        views.setOnClickPendingIntent(R.id.btn_widget,pendingIntentToOpenActivity);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            Log.d(TAG, "onReceive: Received Update Now");
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, IngrediantWidget.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listViewWidget);
        }
        super.onReceive(context, intent);
    }

    public static void UpdateWidget(Context context){
        Log.d(TAG, "UpdateWidget: Update Broadcast Sending");
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, IngrediantWidget.class));
        context.sendBroadcast(intent);
    }
}

