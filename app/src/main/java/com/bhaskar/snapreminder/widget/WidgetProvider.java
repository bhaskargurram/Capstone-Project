package com.bhaskar.snapreminder.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bhaskar.snapreminder.MainActivity;


public class WidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, WidgetIntentService.class));
        Log.d("bhaskar", "on update widget provider");

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, WidgetIntentService.class));
        Log.d("bhaskar", "on app widget options changed");
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(MainActivity.WIDGET_UPDATE_ACTION)) {
            Log.d("bhaskar", "broadcast received");
            context.startService(new Intent(context, WidgetIntentService.class));
        }
    }
}
