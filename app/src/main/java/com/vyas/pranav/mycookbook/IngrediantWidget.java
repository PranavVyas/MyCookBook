package com.vyas.pranav.mycookbook;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vyas.pranav.mycookbook.extrautils.SharedPrefs;
import com.vyas.pranav.mycookbook.modelsutils.MainIngrediantsModel;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

import java.util.List;

import static com.vyas.pranav.mycookbook.recyclerutils.MainListAdapter.KEY_SINGLE_RECEPIE_JSON;
public class IngrediantWidget extends AppWidgetProvider {


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
            views.setTextViewText(R.id.text_container_widget,ingrediants);
            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntentToOpenActivity = PendingIntent.getActivity(context,0,intent,0);
            views.setOnClickPendingIntent(R.id.btn_widget,pendingIntentToOpenActivity);
        }else{
            Gson gson = new Gson();
            recepieJson = SharedPrefs.getCurrentRecepieFromPrefs(context);
            recepie = gson.fromJson(recepieJson, MainRecepieModel.class);
            ingrediantsList = recepie.getIngredients();
            recepieName = recepie.getName();
            //Setting widget Text View
            int index = 1;
            String ingrediantsData = "";
            for (MainIngrediantsModel ingrediant_x : ingrediantsList){
                String ingrediantDetail = index+": <b>"+ingrediant_x.getIngredient()+"</b> : "+ingrediant_x.getQuantity()+" "+ingrediant_x.getMeasure();
                ingrediantsData = ingrediantsData+ingrediantDetail+"<br>";
                index++;
            }

            //Toast.makeText(context, "Updated Widgets Now..................", Toast.LENGTH_SHORT).show();
            views.setTextViewText(R.id.text_container_widget,Html.fromHtml(ingrediantsData));
            views.setTextViewText(R.id.text_recepie_title_widget,recepieName);

            //To Open Activity Directly from Widget
            Intent intent = new Intent(context,RecepieDescriptionActivity.class);
            intent.putExtra(KEY_SINGLE_RECEPIE_JSON,recepieJson);
            PendingIntent pendingIntentToOpenActivity = PendingIntent.getActivity(context,0,intent,0);
            views.setOnClickPendingIntent(R.id.btn_widget,pendingIntentToOpenActivity);
        }
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
}

