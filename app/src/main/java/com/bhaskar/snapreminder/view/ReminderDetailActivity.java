package com.bhaskar.snapreminder.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhaskar.snapreminder.MainActivity;
import com.bhaskar.snapreminder.R;
import com.bhaskar.snapreminder.controller.AlarmReceiver;
import com.bhaskar.snapreminder.controller.List_Add_Task_Adapter;
import com.bhaskar.snapreminder.controller.RemindersContract;
import com.bhaskar.snapreminder.model.Tasks;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.codetroopers.betterpickers.recurrencepicker.EventRecurrence;
import com.codetroopers.betterpickers.recurrencepicker.EventRecurrenceFormatter;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class ReminderDetailActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener, RecurrencePickerDialogFragment.OnRecurrenceSetListener, LoaderManager.LoaderCallbacks<Cursor> {
    TextView date_text, hrs_text, freq_text, description_text;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker_name";
    private static final String FRAG_TAG_RECUR_PICKER = "fragment_recur_picker_name";
    String mRrule;
    EventRecurrence mEventRecurrence = new EventRecurrence();
    String photo = null;
    boolean first_time = false;
    CheckBox reminder_check, add_tasks;
    List_Add_Task_Adapter adapter = null;
    TextView add_list_item;
    RecyclerView recyclerView;
    long rem_no = 0;
    int year_t = -1, month_t = -1, day_t = -1, hours_t = -1, minute_t = -1;
    boolean image_clickable;
    ImageView imageView;
    StringsStore store;
    boolean repeat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);
        store = new StringsStore("NA", "NA", "NA", "NA");
        Intent intent = getIntent();


        //Log.d("bhaskar", "interval_day" + AlarmManager.INTERVAL_DAY);
        // Log.d("bhaskar", "interval_hour" + AlarmManager.INTERVAL_HOUR);
        if (intent.getLongExtra(getString(R.string.reminder_id_new), 0) != 0) {
            rem_no = intent.getLongExtra(getString(R.string.reminder_id_new), 0);
            photo = intent.getStringExtra(getString(R.string.photo_path));
            image_clickable = true;
            first_time = true;
            ContentValues cv = new ContentValues();
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_ID, rem_no);
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE, store.getReminder_date());
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME, store.getReminder_time());
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION, store.getReminder_desc());
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_FREQ, store.getReminder_freq());
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE, photo);
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_FACEBOOK, "NA");
            this.getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI, cv);
            //Toast.makeText(getApplicationContext(), photo, Toast.LENGTH_LONG).show();
        } else if (intent.getLongExtra(getString(R.string.item_click_reminder_id), -1) != -1) {
            Log.d("bhaskar", "reminder no= " + intent.getLongExtra(getString(R.string.item_click_reminder_id), -1));
            rem_no = intent.getLongExtra(getString(R.string.item_click_reminder_id), -1);
            image_clickable = intent.getBooleanExtra(getString(R.string.image_clickable), false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                        .inflateTransition(R.transition.curve));
            }

            if (rem_no != -1) {
                getSupportLoaderManager().initLoader(0, null, ReminderDetailActivity.this);
                getSupportLoaderManager().initLoader(1, null, ReminderDetailActivity.this);
            }


        } else if (intent.getLongExtra(getString(R.string.reminder_no_photo), -1) != -1) {
            rem_no = intent.getLongExtra(getString(R.string.reminder_no_photo), 0);
            image_clickable = false;
            first_time = true;
            ContentValues cv = new ContentValues();
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_ID, rem_no);
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE, store.getReminder_date());
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME, store.getReminder_time());
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION, store.getReminder_desc());
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_FREQ, store.getReminder_freq());
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE, "NA");
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_FACEBOOK, "NA");
            this.getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI, cv);

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView) findViewById(R.id.image_rem);
        if (photo != null) {
            File imageFile = new File(photo);
            //Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //  Bitmap picture = ThumbnailUtils.extractThumbnail(bitmap, 500, 500);
            //imageView.setImageBitmap(picture);
            Picasso.with(ReminderDetailActivity.this)
                    .load(imageFile)
                    .error(R.drawable.reminder_footer)
                    .into(imageView);


        } else {
            Log.d("bhaskar", "photo is null");
        }
        if (image_clickable) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ReminderDetailActivity.this, PhotoActivity.class);
                    if (photo != null) {
                        intent.putExtra(getString(R.string.photo_file_name), photo);
                        Pair<View, String> p1 = Pair.create((View) imageView, "image_trans");

                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(ReminderDetailActivity.this, p1);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(intent, options.toBundle());
                        } else
                            startActivity(intent);
                    }

                }
            });
        }
        reminder_check = (CheckBox) findViewById(R.id.repeat_check_box);
        Typeface regular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        reminder_check.setTypeface(regular);
        reminder_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminder_check.isChecked()) {// CheckBox checked
                    if (month_t != -1 && year_t != -1 && day_t != -1) {
                        if (hours_t != -1 && minute_t != -1) {

                            Calendar cal = Calendar.getInstance();
                            cal.set(year_t, month_t, day_t, hours_t, minute_t);
                            if (!cal.before(Calendar.getInstance())) {
                                reminder_check.setChecked(false);
                                repeat = true;
                                showFreq();
                            } else {
                                Snackbar.make(imageView, "Date and Time already passed, please rectify them.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                repeat = false;
                                reminder_check.setChecked(false);

                            }

                        } else {
                            Snackbar.make(imageView, "Please set Time first.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            repeat = false;
                            reminder_check.setChecked(false);
                        }
                    } else {
                        Snackbar.make(imageView, "Please set date first.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        if (year_t != -1 && month_t != -1 && day_t != -1 && hours_t != -1 && minute_t != -1) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year_t, month_t, day_t, hours_t, minute_t);
                            createAlarm(calendar);
                        }
                        repeat = false;
                        reminder_check.setChecked(false);
                    }

                } else {
                    //CheckBox unchecked
                    store.setReminder_freq("NA");
                    update();
                    freq_text.setText("");
                    findViewById(R.id.linear_repeat).setVisibility(View.GONE);

                }
            }
        });
        add_tasks = (CheckBox) findViewById(R.id.task_check_box);
        add_tasks.setTypeface(regular);
        add_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_tasks.isChecked()) {// CheckBox checked
                    findViewById(R.id.linear_tasks).setVisibility(View.VISIBLE);
                    recyclerView = (RecyclerView) findViewById(R.id.list_tasks);
                    ArrayList<Tasks> arrayList = new ArrayList<Tasks>();

                    adapter = new List_Add_Task_Adapter(arrayList, ReminderDetailActivity.this, rem_no);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setItemAnimator(null);
                    adapter.add();

                    recyclerView.setLayoutManager(new LinearLayoutManager(ReminderDetailActivity.this));
                } else {
                    Log.d("bhaskar", "unchecked tasks");
                    //CheckBox unchecked
                    if (adapter != null) {
                        Log.d("bhaskar", "adpater not null");
                        adapter.changeList(new ArrayList<Tasks>());
                        recyclerView.invalidate();
                        String where = RemindersContract.TasksEntry.COLUMN_REMINDER_ID + "=?";
                        String[] whereArgs = new String[]{String.valueOf(rem_no)};
                        ReminderDetailActivity.this.getContentResolver().
                                delete(RemindersContract.TasksEntry.CONTENT_URI, where, whereArgs);
                    }
                    findViewById(R.id.linear_tasks).setVisibility(View.GONE);
                }
            }


        });

        date_text = (TextView) findViewById(R.id.date);

        hrs_text = (TextView) findViewById(R.id.time);

        freq_text = (TextView) findViewById(R.id.freq);
        date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();
            }
        });
        hrs_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bhaskar", "before=" + store.getReminder_date());
                showClock();
            }
        });
        description_text = (TextView) findViewById(R.id.description_text);
        description_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescr();

            }
        });


        TextView add_list_item = (TextView) findViewById(R.id.add_list_item);
        add_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null) {
                    adapter.add();
                    adapter.notifyDataSetChanged();

                }
            }
        });
        freq_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFreq();
            }
        });
        if (first_time) {
            showDate();
        }
        findViewById(R.id.fab_delete_reminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReminderDetailActivity.this);
                builder.setTitle(getString(R.string.delete_reminder_dialog_title));


                builder.setPositiveButton(getString(R.string.delete_reminder_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String where = RemindersContract.TasksEntry.COLUMN_REMINDER_ID + "=?";
                        String[] whereArgs = new String[]{String.valueOf(rem_no)};
                        getContentResolver().
                                delete(RemindersContract.TasksEntry.CONTENT_URI, where, whereArgs);
                        where = RemindersContract.RemindersEntry.COLUMN_REMINDER_ID + "=?";

                        getContentResolver().
                                delete(RemindersContract.RemindersEntry.CONTENT_URI, where, whereArgs);

                        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                        Log.d("bhaskar", "description_Text=" + description_text.getText());
                        myIntent.putExtra(getString(R.string.count), -1);
                        myIntent.putExtra(getString(R.string.desc_extra), description_text.getText().toString());
                        myIntent.putExtra(getString(R.string.until), "NA");
                        myIntent.putExtra(getString(R.string.id), 0);
                        myIntent.putExtra(getString(R.string.type), 0);
                        myIntent.putExtra(getString(R.string.interval), -1);
                        myIntent.putExtra(getApplicationContext().getString(R.string.days_array), "NA");
                        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), (int) rem_no, myIntent, 0);
                        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(sender);
                        Intent intent = new Intent(MainActivity.WIDGET_UPDATE_ACTION);
                        getApplicationContext().sendBroadcast(intent);
                        Intent main_intent = new Intent(ReminderDetailActivity.this, MainActivity.class);
                        startActivity(main_intent);
                        finish();
                        Log.d("bhaskar", "broadcast sent");
                    }
                });
                builder.setNegativeButton(getString(R.string.delete_reminder_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void showDescr() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReminderDetailActivity.this);
        builder.setTitle(getString(R.string.description_dialog_message));

        final EditText input = new EditText(ReminderDetailActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);


        builder.setPositiveButton(getString(R.string.description_dislog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String final_desc = input.getText().toString();
                store.setReminder_desc(final_desc);
                description_text.setText(final_desc);
                update();
                if (month_t != -1 && year_t != -1 && day_t != -1) {
                    if (hours_t != -1 && minute_t != -1) {

                        Calendar cal = Calendar.getInstance();
                        cal.set(year_t, month_t, day_t, hours_t, minute_t);
                        if (!cal.before(Calendar.getInstance())) {
                            createAlarm(cal);
                        }

                    }
                }
            }
        });
        builder.setNegativeButton(getString(R.string.description_dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void update() {
        // Log.d("bhaskar", "update" + reminder_date + "reminder_time=" + reminder_time);
        String where = RemindersContract.RemindersEntry.COLUMN_REMINDER_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(rem_no)};
        ContentValues cv = new ContentValues();
        cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_ID, rem_no);
        cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE, store.getReminder_date());
        cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME, store.getReminder_time());
        cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION, store.getReminder_desc());
        cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_FREQ, store.getReminder_freq());
        if (photo != null)
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE, photo);
        else
            cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE, "NA");

        ReminderDetailActivity.this.getContentResolver().update(RemindersContract.RemindersEntry.CONTENT_URI, cv, where, whereArgs);
        Intent intent = new Intent(MainActivity.WIDGET_UPDATE_ACTION);
        getApplicationContext().sendBroadcast(intent);
        Log.d("bhaskar", "broadcast sent");
    }

    public void showFreq() {

        FragmentManager fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        Time time = new Time();
        time.setToNow();
        bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
        bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);
        bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, mRrule);
        bundle.putBoolean(RecurrencePickerDialogFragment.BUNDLE_HIDE_SWITCH_BUTTON, true);

        RecurrencePickerDialogFragment rpd = new RecurrencePickerDialogFragment();
        rpd.setArguments(bundle);
        rpd.setOnRecurrenceSetListener(ReminderDetailActivity.this);
        rpd.show(fm, FRAG_TAG_RECUR_PICKER);


    }

    public void showDate() {

        DateTime towDaysAgo = DateTime.now();
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(ReminderDetailActivity.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
                .setDoneText(getString(R.string.positive_message))
                .setCancelText(getString(R.string.negative_message));

        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }


    public void showClock() {
        if (year_t != -1 && month_t != -1 && day_t != -1) {
            RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener(ReminderDetailActivity.this)
                    .setStartTime(10, 10)
                    .setDoneText(getString(R.string.positive_message))
                    .setCancelText(getString(R.string.negative_message)).setThemeDark();
            Log.d("bhaskar", "before=" + store.getReminder_date());
            rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
        } else

        {
            Snackbar.make(imageView, R.string.set_date_first, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

        String y = String.valueOf(year);
        String m = String.valueOf(monthOfYear + 1);
        String d = String.valueOf(dayOfMonth);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Calendar today = Calendar.getInstance();
        if (today.after(calendar)) {
            Toast.makeText(ReminderDetailActivity.this, R.string.date_passed_message, Toast.LENGTH_LONG).show();

            showDate();
        } else {
            store.setReminder_date(y + "," + m + "," + d);
            Log.d("bhaskar", "after dateset=" + store.getReminder_date());
            year_t = year;
            month_t = monthOfYear;
            day_t = dayOfMonth;




            String month_string = "";
            switch (month_t + 1) {
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
            date_text.setText(d + " " + month_string + " " + y);



            if (minute_t != -1 && hours_t != -1) {
                {
                    Calendar calendar_set = Calendar.getInstance();
                    calendar_set.set(year_t, month_t, day_t, hours_t, minute_t);
                    createAlarm(calendar_set);
                }
            }
            update();

            Log.d("bhaskar", "broadcast sent");
            if (first_time) {
                showClock();
            }
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {

        hours_t = hourOfDay;
        minute_t = minute;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year_t, month_t, day_t, hourOfDay, minute);
        Calendar today = Calendar.getInstance();
        Log.d("bhaskar", "" + today.get(Calendar.HOUR));
        Log.d("bhaskar", "" + today.get(Calendar.MINUTE));
        if (today.after(calendar)) {
            Toast.makeText(ReminderDetailActivity.this, R.string.time_passed_message, Toast.LENGTH_LONG).show();
            showClock();
        } else {
            hrs_text.setText(hourOfDay + ":" + minute);
            Log.d("bhaskar", date_text.getText().toString());
            store.setReminder_time(hourOfDay + "," + minute);
            Log.d("bhaskar", "after=" + store.getReminder_date());
            update();

            createAlarm(calendar);
            if (first_time) {
                showDescr();
            }
        }


        // getActivity().getContentResolver().insert(FavouritesContract.FavouritesEntry.CONTENT_URI, cv);

    }

    @Override
    public void onRecurrenceSet(String rrule) {

        mRrule = rrule;
        if (mRrule != null) {
            mEventRecurrence.parse(mRrule);
        }
        populateRepeats();


    }

    private String dayToString(int day, int dayOfWeekLength) {
        return DateUtils.getDayOfWeekString(dayToUtilDay(day), dayOfWeekLength);
    }

    private int dayToUtilDay(int day) {
        switch (day) {
            case EventRecurrence.SU:
                return Calendar.SUNDAY;
            case EventRecurrence.MO:
                return Calendar.MONDAY;
            case EventRecurrence.TU:
                return Calendar.TUESDAY;
            case EventRecurrence.WE:
                return Calendar.WEDNESDAY;
            case EventRecurrence.TH:
                return Calendar.THURSDAY;
            case EventRecurrence.FR:
                return Calendar.FRIDAY;
            case EventRecurrence.SA:
                return Calendar.SATURDAY;
            default:
                throw new IllegalArgumentException("bad day argument: " + day);
        }
    }

    private void populateRepeats() {
        Resources r = getResources();
        String repeatString = "";

        if (!TextUtils.isEmpty(mRrule)) {
            repeatString = EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);
        }
        if (!store.getReminder_date().equals("NA")) {
            if (!store.getReminder_time().equals("NA")) {


                findViewById(R.id.linear_repeat).setVisibility(View.VISIBLE);

                Log.d("bhaskar", "freq" + mEventRecurrence.freq);
                Calendar calendar = Calendar.getInstance();

                calendar.set(year_t, month_t, day_t, hours_t, minute_t);
                String until = mEventRecurrence.until;
                if (until != null) {
                    Calendar until_cal = Calendar.getInstance();
                    until_cal.set(Integer.parseInt(until.substring(0, 4)),
                            Integer.parseInt(until.substring(4, 6)) - 1, Integer.parseInt(until.substring(6, 8)));

                    if (until_cal.before(Calendar.getInstance())) {
                        Toast.makeText(ReminderDetailActivity.this, R.string.until_date_passed, Toast.LENGTH_LONG).show();
                        showFreq();
                    } else {
                        createAlarm(calendar);
                        store.setReminder_freq(mRrule);
                        repeat = true;
                        reminder_check.setChecked(true);
                        freq_text.setText(repeatString);
                        update();
                    }
                } else {
                    createAlarm(calendar);
                    store.setReminder_freq(mRrule);
                    repeat = true;
                    reminder_check.setChecked(true);
                    freq_text.setText(repeatString);
                    update();
                }

            } else {
                repeat = false;
                reminder_check.setChecked(false);
                Snackbar.make(imageView, R.string.set_time_firs, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        } else {
            reminder_check.setChecked(false);
            Snackbar.make(imageView, getString(R.string.set_date_first), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


    }


    public void createAlarm(Calendar calendar) {
        if (repeat) {
            Log.d("bhaskar", "repeat on");
            Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            myIntent.putExtra(getApplicationContext().getString(R.string.id), rem_no);
            Log.d("bhaskar", "description_Text=" + description_text.getText());
            myIntent.putExtra(getString(R.string.desc_extra), description_text.getText().toString());
            String until = mEventRecurrence.until;

            int count = mEventRecurrence.count;
            Log.d("bhaskar", "count=" + count);
            if (count == 0)
                count = -1;
            int interval = mEventRecurrence.interval;
            if (interval == 0)
                interval = 1;
            Log.d("bhaskar", "interval=" + interval);
            int type = mEventRecurrence.freq;
            switch (type) {
                case 3://hourly
                    myIntent.putExtra(getApplicationContext().getString(R.string.type), 3);
                    if (until == null) {
                        until = "NA";

                    }
                    myIntent.putExtra(getApplicationContext().getString(R.string.days_array), "NA");
                    myIntent.putExtra(getApplicationContext().getString(R.string.until), until);
                    Log.d("bhaskar", "until=" + until);
                    myIntent.putExtra(getApplicationContext().getString(R.string.count), count);
                    myIntent.putExtra(getApplicationContext().getString(R.string.interval), interval);
                    break;
                case 4://daily
                    myIntent.putExtra(getApplicationContext().getString(R.string.type), 4);

                    if (until == null) {
                        until = "NA";

                    }
                    myIntent.putExtra(getApplicationContext().getString(R.string.days_array), "NA");
                    myIntent.putExtra(getApplicationContext().getString(R.string.until), until);
                    myIntent.putExtra(getApplicationContext().getString(R.string.count), count);
                    myIntent.putExtra(getApplicationContext().getString(R.string.interval), interval);
                    break;
                case 5://weekly

                    int dayOfWeekLength = DateUtils.LENGTH_MEDIUM;
                    if (mEventRecurrence.bydayCount == 1) {
                        dayOfWeekLength = DateUtils.LENGTH_LONG;
                    }
                    String days_array = "";
                    int array[] = new int[7];
                    if (mEventRecurrence.bydayCount > 0) {

                        for (int i = 0; i < mEventRecurrence.bydayCount; i++) {
                            String str = dayToString(mEventRecurrence.byday[i], dayOfWeekLength);
                            if (str.equals("Sun")) {
                                array[0] = 1;
                            } else if (str.equals("Mon")) {
                                array[1] = 1;
                            } else if (str.equals("Tue")) {
                                array[2] = 1;
                            } else if (str.equals("Wed")) {
                                array[3] = 1;
                            } else if (str.equals("Thu")) {
                                array[4] = 1;
                            } else if (str.equals("Fri")) {
                                array[5] = 1;
                            } else if (str.equals("Sat")) {
                                array[6] = 1;
                            }

                        }
                        for (int i = 0; i < 7; i++) {
                            days_array = days_array + array[i];
                        }
                        Log.d("bhaskar", "days_array=" + days_array);

                    }
                    myIntent.putExtra(getApplicationContext().getString(R.string.days_array), days_array);
                    if (until == null) {
                        until = "NA";

                    }

                    myIntent.putExtra(getApplicationContext().getString(R.string.until), until);
                    myIntent.putExtra(getApplicationContext().getString(R.string.count), count);
                    myIntent.putExtra(getApplicationContext().getString(R.string.interval), interval);

                    myIntent.putExtra(getApplicationContext().getString(R.string.type), 5);
                    break;
                case 6://monthly

                    Log.d("bhaskar", "repeats monthly on day count" + mEventRecurrence.repeatsMonthlyOnDayCount());
                    if (mEventRecurrence.repeatsMonthlyOnDayCount())//First Same Day of each month
                    {
                        Log.d("bhaskar", "repeating monthly with interval=" + interval + "and count=" + count);
                        myIntent.putExtra(getApplicationContext().getString(R.string.type), 6);
                    } else {
                        myIntent.putExtra(getApplicationContext().getString(R.string.type), 7);
                    }
                    if (until == null) {
                        until = "NA";

                    }
                    myIntent.putExtra(getApplicationContext().getString(R.string.days_array), "NA");
                    myIntent.putExtra(getApplicationContext().getString(R.string.until), until);
                    myIntent.putExtra(getApplicationContext().getString(R.string.count), count);
                    myIntent.putExtra(getApplicationContext().getString(R.string.interval), interval);

                    break;
                case 7://yearly
                    myIntent.putExtra(getApplicationContext().getString(R.string.type), 8);

                    if (until != null) {

                        until = "NA";
                    }
                    myIntent.putExtra(getApplicationContext().getString(R.string.days_array), "NA");
                    myIntent.putExtra(getApplicationContext().getString(R.string.until), until);
                    Log.d("bhaskar", "until=" + until);
                    myIntent.putExtra(getApplicationContext().getString(R.string.count), count);
                    myIntent.putExtra(getApplicationContext().getString(R.string.interval), interval);
                    break;


            }


            Log.d("bhaskar", "alarm set");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) rem_no, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

        } else {
            Log.d("bhaskar", "created alarm with no repeat");
            Intent myIntent = new Intent(ReminderDetailActivity.this, AlarmReceiver.class);
            myIntent.putExtra(getString(R.string.desc_extra), description_text.getText().toString());
            myIntent.putExtra(getApplicationContext().getString(R.string.id), rem_no);
            myIntent.putExtra(getApplicationContext().getString(R.string.until), "NA");
            myIntent.putExtra(getApplicationContext().getString(R.string.type), 0);
            myIntent.putExtra(getApplicationContext().getString(R.string.count), -2);
            myIntent.putExtra(getApplicationContext().getString(R.string.days_array), "NA");
            myIntent.putExtra(getApplicationContext().getString(R.string.interval), -2);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ReminderDetailActivity.this, (int) rem_no, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            //alarmManager.cancel(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            Log.d("bhaskar", "on create loader");
            //getDetails = true;
            String where = RemindersContract.RemindersEntry.COLUMN_REMINDER_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(rem_no)};
            Log.d("bhaskar", "where=" + where);
            Log.d("bhaskar", "rem_no=" + String.valueOf(rem_no));
            final String[] projection = {RemindersContract.RemindersEntry._ID,
                    RemindersContract.RemindersEntry.COLUMN_REMINDER_ID,
                    RemindersContract.RemindersEntry.COLUMN_REMINDER_FREQ,
                    RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME,
                    RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE,
                    RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION,
                    RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE};
            return new CursorLoader(ReminderDetailActivity.this, RemindersContract.RemindersEntry.CONTENT_URI, projection, where, whereArgs, null);
        } else {
            //getDetails = false;
            String where = RemindersContract.TasksEntry.COLUMN_REMINDER_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(rem_no)};
            Log.d("bhaskar", "id=1 of loader");
            Log.d("bhaskar", "where=" + where);
            Log.d("bhaskar", "rem_no=" + String.valueOf(rem_no));
            final String[] projection = {RemindersContract.TasksEntry._ID,
                    RemindersContract.TasksEntry.COLUMN_REMINDER_ID,
                    RemindersContract.TasksEntry.COLUMN_TASK_NAME,
                    RemindersContract.TasksEntry.COLUMN_SELECTED,
                    RemindersContract.TasksEntry.COLUMN_SUBTASK_NO
            };
            return new CursorLoader(ReminderDetailActivity.this, RemindersContract.TasksEntry.CONTENT_URI, projection, where, whereArgs, null);
        }


    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == 0) {
            Log.d("bhaskar", "on load finished");
            if (data.moveToFirst()) {
                Log.d("bhaskar", "inside if date.movetofirst");
                String desc = data.getString(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION));
                String image_path = data.getString(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE));
                String date = data.getString(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE));
                String freq = data.getString(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_FREQ));
                String time = data.getString(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME));
                int rem_id = data.getInt(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_ID));
                Log.d("bhaskar", "date=" + date);
                store.setReminder_time(time);
                store.setReminder_freq(freq);
                store.setReminder_date(date);
                store.setReminder_desc(desc);
                if (!time.equals("NA")) {
                    String[] hrs_array = time.split(",");
                    hours_t = Integer.parseInt(hrs_array[0]);
                    minute_t = Integer.parseInt(hrs_array[1]);
                    hrs_text.setText(hrs_array[0] + ":" + hrs_array[1]);
                } else {
                    hrs_text.setText(time);
                }
                if (!date.equals("NA")) {
                    String[] date_array = date.split(",");
                    String year = date_array[0];
                    year_t = Integer.parseInt(year);

                    int month = Integer.parseInt(date_array[1]);
                    month_t = month - 1;
                    String d = date_array[2];
                    day_t = Integer.parseInt(d);
                    Log.d("bhaskar", "year_t=" + year_t + " month_t=" + month_t + " day_t=" + day_t);
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
                    date_text.setText(d + " " + month_string + " " + year);
                } else {
                    date_text.setText(date);
                }

                String repeatString = "";
                Resources r = getResources();
                if (!freq.equals("NA")) {
                    mEventRecurrence.parse(freq);

                    if (!TextUtils.isEmpty(freq)) {
                        repeatString = EventRecurrenceFormatter.getRepeatString(this, r, mEventRecurrence, true);
                    }

                    findViewById(R.id.linear_repeat).setVisibility(View.VISIBLE);
                    repeat = true;
                    reminder_check.setChecked(true);
                }
                freq_text.setText(repeatString);
                description_text.setText(desc);
                File file = new File(image_path);

                if (!image_path.equals("NA")) {
                    photo = image_path;
                    Picasso.with(ReminderDetailActivity.this)
                            .load(file)
                            .error(R.drawable.reminder_footer)
                            .into(imageView);
                }

            }
        } else {
            if (data.moveToFirst()) {
                ArrayList<Tasks> list = new ArrayList<Tasks>();
                do {
                    int selected_int = data.getInt(data.getColumnIndex(RemindersContract.TasksEntry.COLUMN_SELECTED));
                    String rem_id = data.getString(data.getColumnIndex(RemindersContract.TasksEntry.COLUMN_REMINDER_ID));
                    String task_name = data.getString(data.getColumnIndex(RemindersContract.TasksEntry.COLUMN_TASK_NAME));
                    int subtask_no = data.getInt(data.getColumnIndex(RemindersContract.TasksEntry.COLUMN_SUBTASK_NO));
                    boolean check = (selected_int == 0) ? false : true;
                    Tasks task = new Tasks(task_name, check, subtask_no);
                    list.add(task);
                    Log.d("bhaskar", "rem_id=" + rem_id + "subtask_no=" + subtask_no);

                } while (data.moveToNext());
                if (list.size() > 0) {
                    add_tasks.setChecked(true);
                }
                adapter = new List_Add_Task_Adapter(list, ReminderDetailActivity.this, rem_no);
                findViewById(R.id.linear_tasks).setVisibility(View.VISIBLE);
                recyclerView = (RecyclerView) findViewById(R.id.list_tasks);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ReminderDetailActivity.this));

            }
        }
        Intent intent = new Intent(MainActivity.WIDGET_UPDATE_ACTION);
        getApplicationContext().sendBroadcast(intent);
        Log.d("bhaskar", "broadcast sent");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class StringsStore {
        String reminder_date, reminder_freq, reminder_time, reminder_desc;

        StringsStore(String reminder_date, String reminder_freq, String reminder_time, String reminder_desc) {
            this.reminder_date = reminder_date;
            this.reminder_freq = reminder_freq;
            this.reminder_time = reminder_time;
            this.reminder_desc = reminder_desc;
        }

        public void setReminder_date(String reminder_date) {
            this.reminder_date = reminder_date;
        }

        public void setReminder_desc(String reminder_desc) {
            this.reminder_desc = reminder_desc;
        }

        public void setReminder_freq(String reminder_freq) {
            this.reminder_freq = reminder_freq;
        }

        public void setReminder_time(String reminder_time) {
            this.reminder_time = reminder_time;
        }

        public String getReminder_date() {
            return reminder_date;
        }

        public String getReminder_desc() {
            return reminder_desc;
        }

        public String getReminder_freq() {
            return reminder_freq;
        }

        public String getReminder_time() {
            return reminder_time;
        }
    }
}
