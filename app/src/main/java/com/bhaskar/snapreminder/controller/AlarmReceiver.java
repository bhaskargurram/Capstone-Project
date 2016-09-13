package com.bhaskar.snapreminder.controller;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.bhaskar.snapreminder.R;
import com.bhaskar.snapreminder.view.ReminderDetailActivity;

import java.io.File;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("bhaskar", "Received alarm");
        if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // Set the alarm here.

            long rem_id = intent.getLongExtra(context.getString(R.string.id), 0);
            String _path = Environment
                    .getExternalStorageDirectory() + "/.SnapReminder/" + rem_id + ".jpg";
            File file = new File(_path);


            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            String description = intent.getStringExtra(context.getString(R.string.desc_extra));
            Log.d("bhaskar", "description=" + description);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentText("Snap Reminder");
            mBuilder.setContentTitle(description);
            mBuilder.setSound(soundUri);
            if (file.exists()) {
                Bitmap image = BitmapFactory.decodeFile(_path);
                mBuilder.setLargeIcon(image);
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
            }
            Intent resultIntent = new Intent(context, ReminderDetailActivity.class);
            resultIntent.putExtra(context.getString(R.string.item_click_reminder_id), rem_id);
            resultIntent.putExtra(context.getString(R.string.image_clickable), true);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(ReminderDetailActivity.class);

  /* Adds the Intent that starts the Activity to the top of the stack */
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify((int) rem_id, mBuilder.build());


            String until = "NA";
            String days_array = "NA";
            int count = intent.getIntExtra(context.getString(R.string.count), -1);
            Log.d("bhaskar", "count on receive=" + count);
            int interval = intent.getIntExtra(context.getString(R.string.interval), -1);
            int until_year = -1, until_month = -1, until_day = -1;
            if (intent.getStringExtra(context.getString(R.string.until)) != null) {
                if (!intent.getStringExtra(context.getString(R.string.until)).equals("NA")) {
                    until = intent.getStringExtra(context.getString(R.string.until));
                    until_year = Integer.parseInt(until.substring(0, 4));
                    until_month = Integer.parseInt(until.substring(4, 6)) - 1;
                    until_day = Integer.parseInt(until.substring(6, 8));
                    Log.d("bhaskar", "until_year=" + until_year + "until_month=" + until_month + "until_day=" + until_day);
                }
            }

            if (!intent.getStringExtra(context.getString(R.string.days_array)).equals("NA")) {
                days_array = intent.getStringExtra(context.getString(R.string.days_array));
            }

            int type = intent.getIntExtra(context.getString(R.string.type), -1);
            Log.d("bhaskar", "until=" + until + ", type=" + type);
            cancelAlarm(context, (int) rem_id, intent);
            switch (type) {
                case 3:

                    if (!until.equals("NA")) {//check if there is an until
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(until_year, until_month, until_day);
                        if (Calendar.getInstance().before(calendar))//time not passed, so create another reminder
                        {
                            Log.d("bhaskar", "until is not NA and Valid time");
                            Calendar next_r = Calendar.getInstance();
                            next_r.add(Calendar.HOUR_OF_DAY, interval);

                            createAlarm(next_r, context, (int) rem_id, -1, until, 3, interval, days_array, description);

                        }//else leave it dont create another reminder
                        else {
                            Log.d("bhaskar", "until is not NA and inValid time");
                        }
                    } else if (count > 1) {//should repeat atleast once more
                        Log.d("bhaskar", "count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.add(Calendar.HOUR_OF_DAY, interval);


                        createAlarm(next_r, context, (int) rem_id, count - 1, null, 3, interval, days_array, description);
                    } else if (until.equals("NA") && count == -1) {//repeat forever
                        Log.d("bhaskar", "forever count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.add(Calendar.HOUR_OF_DAY, interval);

                        createAlarm(next_r, context, (int) rem_id, -1, null, 3, interval, days_array, description);
                    } else {
                        //  cancelAlarm(context, (int) rem_id, intent);
                    }


                    break;
                case 4://daily
                    if (!until.equals("NA")) {//check if there is an until
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(until_year, until_month, until_day, 0, 0);
                        Log.d("bhaskar", "date=" + calendar.get(Calendar.DATE));
                        if (Calendar.getInstance().compareTo(calendar) < 0)//time not passed, so create another reminder
                        {
                            Log.d("bhaskar", "until is not NA and Valid time");
                            Calendar next_r = Calendar.getInstance();
                            next_r.add(Calendar.DATE, interval);
                            //cancelAlarm(context, (int) rem_id, intent);
                            createAlarm(next_r, context, (int) rem_id, -1, until, 4, interval, days_array, description);

                        }//else leave it dont create another reminder
                        else {
                            Log.d("bhaskar", "until is not NA and inValid time");
                        }
                    } else if (count > 1) {//should repeat atleast once more
                        Log.d("bhaskar", "count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.add(Calendar.DATE, interval);

                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, count - 1, null, 4, interval, days_array, description);
                    } else if (until.equals("NA") && count == -1) {//repeat forever
                        Log.d("bhaskar", "forever count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.add(Calendar.DATE, interval);
                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, -1, null, 4, interval, days_array, description);
                    }


                case 5://weekly


                    if (!until.equals("NA")) {//check if there is an until
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(until_year, until_month, until_day, 0, 0);
                        Log.d("bhaskar", "date=" + calendar.get(Calendar.DATE));
                        if (Calendar.getInstance().compareTo(calendar) < 0)//time not passed, so create another reminder
                        {
                            Calendar next_r = Calendar.getInstance();
                            next_r.setFirstDayOfWeek(Calendar.SUNDAY);
                            int position = next_r.get(Calendar.DAY_OF_WEEK) - 1;
                            int k = 0;
                            for (int i = (position + 1) % 7; i != position; i = (i + 1) % 7) {
                                k = k + 1;
                                int c = Integer.parseInt(days_array.substring(i, i + 1));
                                if (c == 1) {
                                    if (i > position) {
                                        next_r.add(Calendar.DATE, k);
                                    } else {
                                        next_r.add(Calendar.DATE, k + (interval - 1) * 7);
                                    }
                                    break;
                                }
                            }


                            Log.d("bhaskar", "until is not NA and Valid time");


                            //cancelAlarm(context, (int) rem_id, intent);
                            createAlarm(next_r, context, (int) rem_id, -1, until, 5, interval, days_array, description);

                        }//else leave it dont create another reminder
                        else {
                            Log.d("bhaskar", "until is not NA and inValid time");
                        }
                    } else if (count > 1) {//should repeat atleast once more
                        Log.d("bhaskar", "count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.setFirstDayOfWeek(Calendar.SUNDAY);
                        int position = next_r.get(Calendar.DAY_OF_WEEK) - 1;
                        int k = 0;
                        for (int i = (position + 1) % 7; i != position; i = (i + 1) % 7) {
                            k = k + 1;
                            int c = Integer.parseInt(days_array.substring(i, i + 1));
                            if (c == 1) {
                                if (i > position) {
                                    next_r.add(Calendar.DATE, k);
                                } else {
                                    next_r.add(Calendar.DATE, k + (interval - 1) * 7);
                                }
                                break;
                            }
                        }
                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, count - 1, null, 5, interval, days_array, description);
                    } else if (until.equals("NA") && count == -1) {//repeat forever
                        Log.d("bhaskar", "forever count=" + count);
                        //cancelAlarm(context, (int) rem_id, intent);
                        Calendar next_r = Calendar.getInstance();
                        next_r.setFirstDayOfWeek(Calendar.SUNDAY);
                        int position = next_r.get(Calendar.DAY_OF_WEEK) - 1;
                        int k = 0;
                        for (int i = (position + 1) % 7; i != position; i = (i + 1) % 7) {
                            k = k + 1;
                            int c = Integer.parseInt(days_array.substring(i, i + 1));
                            if (c == 1) {
                                if (i > position) {
                                    next_r.add(Calendar.DATE, k);
                                } else {
                                    next_r.add(Calendar.DATE, k + (interval - 1) * 7);
                                }
                                break;
                            }
                        }
                        createAlarm(next_r, context, (int) rem_id, -1, null, 5, interval, days_array, description);
                    }

                    break;
                case 6://monthly on same day
                    if (!until.equals("NA")) {//check if there is an until
                        Calendar until_cal = Calendar.getInstance();
                        until_cal.set(until_year, until_month, until_day);
                        if (Calendar.getInstance().before(until_cal))//time not passed, so create another reminder
                        {
                            Log.d("bhaskar", "until is not NA and Valid time");
                            Calendar next_r = Calendar.getInstance();

                            next_r.add(Calendar.MONTH, interval);
                            next_r.set(Calendar.DAY_OF_MONTH, 1);

                            // search until wednesday
                            while (next_r.get(Calendar.DAY_OF_WEEK) != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                                next_r.add(Calendar.DAY_OF_MONTH, 1);
                            }
                            Log.d("bhaskar", "next_date=" + next_r.get(Calendar.DATE));
                            Log.d("bhaskar", "next_month=" + next_r.get(Calendar.MONTH));
                            Log.d("bhaskar", "next_year=" + next_r.get(Calendar.YEAR));

                            //  cancelAlarm(context, (int) rem_id, intent);
                            createAlarm(next_r, context, (int) rem_id, -1, until, 6, interval, days_array, description);

                        }//else leave it dont create another reminder
                        else {
                            Log.d("bhaskar", "until is not NA and inValid time");
                        }
                    } else if (count > 1) {//should repeat atleast once more
                        Log.d("bhaskar", "count=" + count);
                        Calendar next_r = Calendar.getInstance();

                        next_r.add(Calendar.MONTH, interval);
                        next_r.set(Calendar.DAY_OF_MONTH, 1);

                        // search until wednesday
                        while (next_r.get(Calendar.DAY_OF_WEEK) != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                            next_r.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        Log.d("bhaskar", "next_date=" + next_r.get(Calendar.DATE));
                        Log.d("bhaskar", "next_month=" + next_r.get(Calendar.MONTH));
                        Log.d("bhaskar", "next_year=" + next_r.get(Calendar.YEAR));

                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, count - 1, null, 6, interval, days_array, description);
                    } else if (until.equals("NA") && count == -1) {//repeat forever
                        Calendar next_r = Calendar.getInstance();

                        next_r.add(Calendar.MONTH, interval);
                        next_r.set(Calendar.DAY_OF_MONTH, 1);

                        // search until wednesday
                        while (next_r.get(Calendar.DAY_OF_WEEK) != Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                            next_r.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, -1, null, 6, interval, days_array, description);
                    }
                    break;
                case 7://monthly
                    if (!until.equals("NA")) {//check if there is an until
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(until_year, until_month, until_day);
                        if (Calendar.getInstance().before(calendar))//time not passed, so create another reminder
                        {
                            Log.d("bhaskar", "until is not NA and Valid time");
                            Calendar next_r = Calendar.getInstance();
                            next_r.add(Calendar.MONTH, interval);
                            //  cancelAlarm(context, (int) rem_id, intent);
                            createAlarm(next_r, context, (int) rem_id, -1, until, 7, interval, days_array, description);

                        }//else leave it dont create another reminder
                        else {
                            Log.d("bhaskar", "until is not NA and inValid time");
                        }
                    } else if (count > 1) {//should repeat atleast once more
                        Log.d("bhaskar", "count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.add(Calendar.MONTH, interval);

                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, count - 1, null, 7, interval, days_array, description);
                    } else if (until.equals("NA") && count == -1) {//repeat forever
                        Log.d("bhaskar", "forever count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.add(Calendar.MONTH, interval);
                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, -1, null, 7, interval, days_array, description);
                    }
                    break;

                case 8://yearly
                    if (!until.equals("NA")) {//check if there is an until
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(until_year, until_month, until_day);
                        if (Calendar.getInstance().before(calendar))//time not passed, so create another reminder
                        {
                            Log.d("bhaskar", "until is not NA and Valid time");
                            Calendar next_r = Calendar.getInstance();
                            next_r.add(Calendar.YEAR, interval);
                            //  cancelAlarm(context, (int) rem_id, intent);
                            createAlarm(next_r, context, (int) rem_id, -1, until, 8, interval, days_array, description);

                        }//else leave it dont create another reminder
                        else {
                            Log.d("bhaskar", "until is not NA and inValid time");
                        }
                    } else if (count > 1) {//should repeat atleast once more
                        Log.d("bhaskar", "count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.add(Calendar.YEAR, interval);

                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, count - 1, null, 8, interval, days_array, description);
                    } else if (until.equals("NA") && count == -1) {//repeat forever
                        Log.d("bhaskar", "forever count=" + count);
                        Calendar next_r = Calendar.getInstance();
                        next_r.add(Calendar.YEAR, interval);
                        //cancelAlarm(context, (int) rem_id, intent);
                        createAlarm(next_r, context, (int) rem_id, -1, null, 8, interval, days_array, description);
                    }
                case 0:
                default:
            }

        }
    }

    public void createAlarm(Calendar calendar, Context context, int rem_id, int count_1, String until, int type, int interval, String days_array, String description) {

        Log.d("bhaskar", "next alarm at date" + calendar.get(Calendar.DATE) + ",month=" + calendar.get(Calendar.MONTH) + ",year=" + calendar.get(Calendar.YEAR));
        Intent myIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);

        myIntent.putExtra("count", count_1);
        if (until == null)
            until = "NA";

        myIntent.putExtra(context.getString(R.string.desc_extra), description);
        myIntent.putExtra(context.getString(R.string.days_array), days_array);
        myIntent.putExtra(context.getString(R.string.until), until);
        myIntent.putExtra(context.getString(R.string.id), rem_id);
        myIntent.putExtra(context.getString(R.string.type), type);
        myIntent.putExtra(context.getString(R.string.interval), interval);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), rem_id, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        Log.d("bhaskar", "repeating alarm with count = " + count_1);

    }

    public void cancelAlarm(Context context, int rem_id, Intent myIntent) {

        PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), rem_id, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


}
