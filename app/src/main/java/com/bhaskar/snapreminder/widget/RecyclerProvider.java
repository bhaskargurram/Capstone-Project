package com.bhaskar.snapreminder.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bhaskar.snapreminder.R;
import com.bhaskar.snapreminder.controller.RemindersContract;

/**
 * Created by bhaskar on 3/4/16.
 */
public class RecyclerProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context context = null;
    private int appWidgetId;
    private static final int CURSOR_LOADER_ID = 0;
    Cursor cursor;
    int size;


    public RecyclerProvider(Context context, Intent intent, Cursor cursor, int size) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        this.cursor = cursor;
        this.size = size;
        Log.d("", "inside Recycler Provider");
        cursor.moveToFirst();
        //Toast.makeText(context,"inside",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        Log.d("bhaskar", "inside onDataSetChanged");
        final String[] projection = {RemindersContract.RemindersEntry._ID,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_ID,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE, RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME};
        Cursor cursor = context.getContentResolver().query(RemindersContract.RemindersEntry.CONTENT_URI, projection, null, null, null);
        cursor.moveToFirst();
        size = cursor.getCount();
        Log.d("bhaskar", "cursor size=" + cursor.getCount());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public RemoteViews getViewAt(int position) {
        //Log.d("", "inside " + cursor.getString(cursor.getColumnIndex("symbol")) + " " + cursor.getCount());

        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_row);

        cursor.moveToPosition(position);
        Log.i("", "inside " + position);
        if (!cursor.isAfterLast()) {
            String description = cursor.getString(cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION));
            if (description.length() > 10)
                description = description.substring(0, 10) + "...";
            remoteView.setTextViewText(R.id.widget_desc, description);
            String date = cursor.getString(cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE));
            if (!date.equals("NA")) {
                String[] date_array = date.split(",");
                String year = date_array[0];
                int month = Integer.parseInt(date_array[1]);
                String d = date_array[2];
                String month_string = "";
                switch (month) {
                    case 1:
                        month_string = "JAN";
                        break;
                    case 2:
                        month_string = "FEB";
                        break;
                    case 3:
                        month_string = "MAR";
                        break;
                    case 4:
                        month_string = "APR";
                        break;
                    case 5:
                        month_string = "MAY";
                        break;
                    case 6:
                        month_string = "JUN";
                        break;
                    case 7:
                        month_string = "JULY";
                        break;
                    case 8:
                        month_string = "AUG";
                        break;
                    case 9:
                        month_string = "SEPT";
                        break;
                    case 10:
                        month_string = "OCT";
                        break;
                    case 11:
                        month_string = "NOV";
                        break;
                    case 12:
                        month_string = "DEC";
                        break;
                }
                remoteView.setTextViewText(R.id.widget_date, d + " " + month_string + " " + year);

            } else {
                remoteView.setTextViewText(R.id.widget_date, date);
            }


            String time = cursor.getString(cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME));
            String[] hrs_array = time.split(",");
            if (hrs_array.length == 2)
                remoteView.setTextViewText(R.id.widget_time, hrs_array[0] + ":" + hrs_array[1]);

            String image = cursor.getString(cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE));

            Intent fillIntent = new Intent();
            fillIntent.putExtra("reminder_id", cursor.getLong(cursor.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_ID)));
            if (!image.equals("NA")) {
                fillIntent.putExtra("image_clickable", true);
            }
            remoteView.setOnClickFillInIntent(R.id.widget_row, fillIntent);
            return remoteView;
        }
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

}
