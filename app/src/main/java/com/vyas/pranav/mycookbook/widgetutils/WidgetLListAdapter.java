package com.vyas.pranav.mycookbook.widgetutils;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.blankj.ALog;
import com.google.gson.Gson;
import com.vyas.pranav.mycookbook.BuildConfig;
import com.vyas.pranav.mycookbook.R;
import com.vyas.pranav.mycookbook.extrautils.SharedPrefs;
import com.vyas.pranav.mycookbook.modelsutils.MainIngrediantsModel;
import com.vyas.pranav.mycookbook.modelsutils.MainRecepieModel;

import java.util.List;

public class WidgetLListAdapter implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "WidgetLListAdapter";
    List<MainIngrediantsModel> ingrediantsModels;
    Context context;
    Intent intent;
    private int appWidgetId;

    public WidgetLListAdapter(Context context,Intent intent) {
        this.context = context;
        initALog();
    }

    public void setListResource(){
        Gson gson = new Gson();
        String recepieJson = SharedPrefs.getCurrentRecepieFromPrefs(context);
        MainRecepieModel recepie = gson.fromJson(recepieJson, MainRecepieModel.class);
        ingrediantsModels = recepie.getIngredients();
        //ALog.json(recepieJson);
    }

    // init it in ur application
    public void initALog() {
        ALog.Config config = ALog.init(context)
                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"alog"，即写入文件为"alog-MM-dd.txt"
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(ALog.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(ALog.V)// log文件过滤器，和logcat过滤器同理，默认Verbose
                .setStackDeep(1);// log栈深度，默认为1
        //ALog.d(config.toString());
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        ALog.d("DataSet Changed Now");
        setListResource();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(ingrediantsModels == null) return 0;
        else return ingrediantsModels.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_widget_single);
        MainIngrediantsModel ingrediantsModel = ingrediantsModels.get(position);
        //ALog.json(new Gson().toJson(ingrediantsModel));
        String iName = "<b>"+ingrediantsModel.getIngredient()+"</b>";
        remoteView.setTextViewText(R.id.text_list_widget_name, Html.fromHtml(iName));
        String q = ingrediantsModel.getQuantity() + " " +ingrediantsModel.getMeasure();
        remoteView.setTextViewText(R.id.text_list_quantity_widget,Html.fromHtml(q));
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
