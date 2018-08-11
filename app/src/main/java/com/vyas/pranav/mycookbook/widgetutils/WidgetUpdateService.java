package com.vyas.pranav.mycookbook.widgetutils;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.vyas.pranav.mycookbook.widgetutils.WidgetLListAdapter;

public class WidgetUpdateService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetLListAdapter(this.getApplicationContext(), intent));
    }
}
