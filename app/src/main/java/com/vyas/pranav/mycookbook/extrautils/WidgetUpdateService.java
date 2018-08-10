package com.vyas.pranav.mycookbook.extrautils;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetUpdateService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetLListAdapter(this.getApplicationContext(), intent));
    }
}
