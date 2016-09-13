package com.bhaskar.snapreminder.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.bhaskar.snapreminder.controller.RemindersContract;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.d("bhaskar", "inside WidgetService");
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        final String[] projection = {RemindersContract.RemindersEntry._ID,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_ID,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE, RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME};
        Cursor cursor = this.getContentResolver().query(RemindersContract.RemindersEntry.CONTENT_URI, projection, null, null, null);
        Log.d("bhaskar", "inside on create loader");

        cursor.moveToFirst();


        //Log.d("",cursor.getString(cursor.getColumnIndex("symbol")));
        return (new RecyclerProvider(this.getApplicationContext(), intent, cursor, cursor.getCount()));
    }

}
