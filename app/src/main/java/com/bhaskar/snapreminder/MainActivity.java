package com.bhaskar.snapreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.bhaskar.snapreminder.controller.AlarmReceiver;
import com.bhaskar.snapreminder.controller.ReminderCardsAdapter;
import com.bhaskar.snapreminder.controller.RemindersContract;
import com.bhaskar.snapreminder.model.CustomTypefaceSpan;
import com.bhaskar.snapreminder.model.ReminderCards;
import com.bhaskar.snapreminder.view.ReminderDetailActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory() + "/.SnapReminder/";
    private Uri imageUri;
    SharedPreferences preferences;
    RecyclerView recyclerView;
    ReminderCardsAdapter adapter;
    ArrayList<ReminderCards> arrayList;

    public static final String WIDGET_UPDATE_ACTION = "com.bhaskar.snapreminder.ACTION_DATA_UPDATE";
    private Paint p = new Paint();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    String _path;
    private CallbackManager callbackManager;
    AccessToken currentAccess;
    private AccessTokenTracker accessTokenTracker;

    Tracker mTracker;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mTracker = ((MainApplication) getApplication()).getDefaultTracker();
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                currentAccess = currentAccessToken;
            }
        };


        // If the access token is available already assign it.

        File Folder = new File(DATA_PATH);
        if (Folder.mkdirs()) {
            File nomediaFile = new File(Environment.getExternalStorageDirectory() + "/SnapReminder/" + ".nomedia");
            if (!nomediaFile.exists()) {
                try {
                    nomediaFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        preferences = getSharedPreferences(getString(R.string.preference_id), MODE_PRIVATE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        navigationView.setNavigationItemSelectedListener(this);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 100);
                //finish();
            }
        });

        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCamerPermission();
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                long reminder_no = preferences.getLong(getString(R.string.preference_reminder_id), 0) + 1;
                editor.putLong(getString(R.string.preference_reminder_id), reminder_no);
                editor.commit();
                Intent intent = new Intent(MainActivity.this, ReminderDetailActivity.class);
                intent.putExtra(getString(R.string.reminder_no_photo), reminder_no);
                startActivity(intent);

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportLoaderManager().initLoader(0, null, MainActivity.this);
        checkWritingPermission();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public void getdetails() {
        Log.d("bhaskar", "inside getdetails");
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccess,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {/*
                        if (object != null) {
                            String id = null;
                            try {
                                id = object.getString("id");
                                String name = object.getString("name");
                                String birthday = object.getString("birthday");

                                String dp = "https://graph.facebook.com/" + id + "/picture?type=large";
                                String desc = name + "'s birthday";

                                String where = RemindersContract.RemindersEntry.COLUMN_REMINDER_FACEBOOK + "=?";
                                String[] whereArgs = new String[]{String.valueOf(id)};
                                final String[] projection = {RemindersContract.RemindersEntry.COLUMN_REMINDER_ID};
                                Cursor cursor = MainActivity.this.getContentResolver().query(RemindersContract.RemindersEntry.CONTENT_URI, projection, where, whereArgs, null);
                                if (!cursor.moveToFirst()) {
                                    FacebookUser user = new FacebookUser(birthday, desc, id, dp);
                                    new DownloadImageFromURl(user).execute(dp);
                                } else {
                                    Log.d("bhaskar", "user already exists");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        Log.d("bhaskar", "inside new me request completed");
                        // Application code
                        Log.d("bhaskar", "user=" + object);*/
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,birthday,picture");
        request.setParameters(parameters);
        request.executeAsync();
        GraphRequest request_friends = GraphRequest.newMyFriendsRequest(
                currentAccess, new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        Log.d("bhaskar", "inside new my friends request completed");
                        if (objects != null) {
                            if (objects.length() == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(getString(R.string.facebook_sync_dialog_title));
                                builder.setMessage(getString(R.string.facebook_dialog_message));
                                builder.setCancelable(false);
                                builder.setPositiveButton(getString(R.string.dialog_positive_title), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();


                                    }
                                });
                                builder.show();
                            } else {
                                for (int i = 0; i < objects.length(); i++) {
                                    try {
                                        JSONObject json = objects.getJSONObject(i);
                                        Log.d("bhaskar", "friend=" + json);
                                        if (json != null) {
                                            String id = json.getString("id");
                                            String name = json.getString("name");
                                            String birthday = json.getString("birthday");

                                            String dp = "https://graph.facebook.com/" + id + "/picture?type=large";
                                            String desc = name + getString(R.string.birthday_suffix);

                                            String where = RemindersContract.RemindersEntry.COLUMN_REMINDER_FACEBOOK + "=?";
                                            String[] whereArgs = new String[]{String.valueOf(id)};
                                            final String[] projection = {RemindersContract.RemindersEntry.COLUMN_REMINDER_ID};
                                            Cursor cursor = MainActivity.this.getContentResolver().query(RemindersContract.RemindersEntry.CONTENT_URI, projection, where, whereArgs, null);
                                            if (!cursor.moveToFirst()) {
                                                FacebookUser user = new FacebookUser(birthday, desc, id, dp);
                                                new DownloadImageFromURl(user).execute(dp);
                                            } else {
                                                Log.d("bhaskar", "user already exists");
                                            }
                                        }

                                        //https://graph.facebook.com/991763907543557/picture?type=large


                                    } catch (JSONException e) {
                                        Log.d("bhaskar", "caught exception" + e.toString());
                                    }
                                }
                            }

                        }
                    }
                }

        );
        request_friends.setParameters(parameters);
        request_friends.executeAsync();


    }

    @Override
    public void onDestroy() {

        accessTokenTracker.stopTracking();

        super.onDestroy();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            Log.d("bhaskar", "permission granted for writing files");
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkAlarmPermission();
            }
        } else if (requestCode == 1) {
            Log.d("bhaskar", "permission granted for camera");
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraActivity();
            }
        } else if (requestCode == 2) {
            Log.d("bhaskar", "permission granted for camera");
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }


    public class DownloadImageFromURl extends AsyncTask<String, Void, Void> {
        FacebookUser user;

        DownloadImageFromURl(FacebookUser user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.d("bhaskar", "inside do in background" + params[0]);
            //URL imageurl = null;
            long reminder_no = preferences.getLong(getString(R.string.preference_reminder_id), 0) + 1;
            File file1 = new File(DATA_PATH);
            file1.mkdirs();
            String _path = DATA_PATH + reminder_no + ".jpg";
            File file = new File(_path);
            //imageurl = new URL(params[0]);
            // Log.d("bhaskar", "url" + imageurl);


            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(params[0]).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
                int minheight = bitmap.getHeight() < 600 ? bitmap.getHeight() : 600;
                int minwidth = bitmap.getWidth() < 600 ? bitmap.getWidth() : 600;
                Bitmap picture = ThumbnailUtils.extractThumbnail(bitmap, minwidth, minheight);
                FileOutputStream fo = new FileOutputStream(file);
                picture.compress(Bitmap.CompressFormat.JPEG, 100, fo);
                fo.flush();
                fo.close();
                String birthday = user.getReminder_date();
                String month = birthday.substring(0, 2);
                String date = birthday.substring(3, 5);
                String year = birthday.substring(6, 10);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, Integer.parseInt(month));
                calendar.set(Calendar.DATE, Integer.parseInt(date));
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.YEAR, 1);

                }
                year = String.valueOf(calendar.get(Calendar.YEAR));
                birthday = year + "," + month + "," + date;
                Log.d("bhaskar", "checking date" + month + date + year);
                ContentValues cv = new ContentValues();
                cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_ID, reminder_no);
                cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE, birthday);
                cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME, user.getReminder_time());
                cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION, user.getReminder_desc());
                cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_FREQ, user.getReminder_freq());
                cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE, _path);
                cv.put(RemindersContract.RemindersEntry.COLUMN_REMINDER_FACEBOOK, user.getFb_id());
                getApplicationContext().getContentResolver().insert(RemindersContract.RemindersEntry.CONTENT_URI, cv);
                Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                myIntent.putExtra(getString(R.string.id), reminder_no);
                myIntent.putExtra(getString(R.string.days_array), "NA");
                myIntent.putExtra(getString(R.string.until), "NA");
                myIntent.putExtra(getString(R.string.count), "-1");
                myIntent.putExtra(getString(R.string.type), 8);
                myIntent.putExtra(getString(R.string.desc_extra), user.getReminder_desc());
                myIntent.putExtra(getString(R.string.interval), 1);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) reminder_no, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                //  alarmManager.cancel(pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(getString(R.string.preference_reminder_id), reminder_no);
                editor.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void cameraActivity() {

        long reminder_no = preferences.getLong(getString(R.string.preference_reminder_id), 0) + 1;
        File file1 = new File(DATA_PATH);
        file1.mkdirs();
        String _path = DATA_PATH + reminder_no + ".jpg";
        File file = new File(_path);
        imageUri = Uri.fromFile(file);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, 0);
    }

    private void checkWritingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("bhaskar", "inside check writing Permission positive");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        } else {
            Log.d("bhaskar", "check writing permission already granted");
            checkAlarmPermission();
        }
    }

    private void checkAlarmPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            Log.d("bhaskar", "inside check alarm Permission positive");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 2);

        } else {
            Log.d("bhaskar", "check alarm permission already granted");
            //checkCamerPermission();
        }
    }

    private void checkCamerPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("bhaskar", "inside checkCamera Permission positive");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);

        } else {
            Log.d("bhaskar", "check camera permission already granted");
            cameraActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);

            Log.d("bhaskar", "requestcode=" + requestCode + "resultcode=" + resultCode);
        } else if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                long reminder_no = preferences.getLong(getString(R.string.preference_reminder_id), 0) + 1;
                _path = DATA_PATH + reminder_no + ".jpg";

                File file = new File(_path);
                Bitmap image = BitmapFactory.decodeFile(_path);
                int minheight = image.getHeight() < 600 ? image.getHeight() : 600;
                int minwidth = image.getWidth() < 600 ? image.getWidth() : 600;
                Bitmap picture = ThumbnailUtils.extractThumbnail(image, minwidth, minheight);
                //Bitmap picture = (Bitmap) data.getExtras().get("data");
                //Bitmap picture = decodeFile(_path);
                //picture = Bitmap.createScaledBitmap(picture, picture.getWidth(), picture.getHeight(), false);
                //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //picture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                try {
                    //file.delete();
                    // file.createNewFile();
                    //  Log.d("bhaskar", "path=" + Uri.fromFile(file).toString());
                    //compressImage(_path);
                    FileOutputStream fo = new FileOutputStream(file);
                    picture.compress(Bitmap.CompressFormat.JPEG, 100, fo);
                    fo.flush();
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(getString(R.string.preference_reminder_id), reminder_no);
                editor.commit();
                Intent intent = new Intent(MainActivity.this, ReminderDetailActivity.class);
                intent.putExtra(getString(R.string.reminder_id_new), reminder_no);
                intent.putExtra(getString(R.string.photo_path), _path);
                Intent intent_b = new Intent(WIDGET_UPDATE_ACTION);
                getApplicationContext().sendBroadcast(intent_b);
                Log.d("bhaskar", "broadcast sent");
                startActivity(intent);


            }

        } else if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    long reminder_no = preferences.getLong(getString(R.string.preference_reminder_id), 0) + 1;
                    _path = DATA_PATH + reminder_no + ".jpg";
                    File file1 = new File(DATA_PATH);
                    file1.mkdirs();
                    File file = new File(_path);
                    imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap image = BitmapFactory.decodeStream(imageStream);
                    int minheight = image.getHeight() < 600 ? image.getHeight() : 600;
                    int minwidth = image.getWidth() < 600 ? image.getWidth() : 600;
                    Bitmap picture = ThumbnailUtils.extractThumbnail(image, minwidth, minheight);
                    FileOutputStream fo = new FileOutputStream(file);
                    picture.compress(Bitmap.CompressFormat.JPEG, 100, fo);
                    fo.flush();
                    fo.close();

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong(getString(R.string.preference_reminder_id), reminder_no);
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, ReminderDetailActivity.class);
                    intent.putExtra(getString(R.string.reminder_id_new), reminder_no);
                    intent.putExtra(getString(R.string.photo_path), _path);
                    Intent intent_b = new Intent(WIDGET_UPDATE_ACTION);
                    getApplicationContext().sendBroadcast(intent_b);
                    Log.d("bhaskar", "broadcast sent");
                    startActivity(intent);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.reminders:
                getSupportLoaderManager().initLoader(0, null, MainActivity.this);
                break;
            case R.id.wo_image_reminder:
                getSupportLoaderManager().initLoader(1, null, MainActivity.this);
                break;
            case R.id.image_reminder:
                getSupportLoaderManager().initLoader(2, null, MainActivity.this);
                break;
            case R.id.facebook:
                if (isNetworkAvailable(MainActivity.this)) {
                    currentAccess = AccessToken.getCurrentAccessToken();
                    if (currentAccess == null) {
                        Log.d("bhaskar", "current access is null");

                        List<String> permissionNeeds = Arrays.asList("public_profile", "email", "user_friends");

                        LoginManager.getInstance().logInWithReadPermissions(
                                this,
                                permissionNeeds);
                        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                currentAccess = loginResult.getAccessToken();
                                Log.d("bhaskar",
                                        "User ID: "
                                                + loginResult.getAccessToken().getUserId()
                                                + "\n" +
                                                "Auth Token: "
                                                + loginResult.getAccessToken().getToken()
                                );
                                getdetails();
                            }

                            @Override
                            public void onCancel() {
                                Log.d("bhaskar", "login cancelled");

                            }

                            @Override
                            public void onError(FacebookException e) {
                                Log.d("bhaskar", "login error" + e);
                            }
                        });
                    } else {


                        getdetails();
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("No Internet Connection.");
                    builder.setTitle("Information");
                    builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.suggest:
                Intent mailClient = new Intent(Intent.ACTION_VIEW);
                mailClient.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                mailClient.putExtra(Intent.EXTRA_EMAIL, new String[]{"snap.reminder.bg@gmail.com"});
                mailClient.putExtra(Intent.EXTRA_SUBJECT, "Snap Reminder User Feedback");
                startActivity(mailClient);
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.share_intent_prefix) + MainActivity.this.getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.rate_us:
                Uri uri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
                }

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("bhaskar", "inside on create loader");

        final String[] projection = {RemindersContract.RemindersEntry._ID,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_ID,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION,
                RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE};
        if (id == 0) {
            return new CursorLoader(MainActivity.this, RemindersContract.RemindersEntry.CONTENT_URI, projection, null, null, null);
        } else if (id == 1) {
            String where = RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE + "==?";
            String[] whereArgs = new String[]{"NA"};
            return new CursorLoader(MainActivity.this, RemindersContract.RemindersEntry.CONTENT_URI, projection, where, whereArgs, null);
        } else if (id == 2) {
            String where = RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE + "<>?";
            String[] whereArgs = new String[]{"NA"};
            return new CursorLoader(MainActivity.this, RemindersContract.RemindersEntry.CONTENT_URI, projection, where, whereArgs, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("bhaskar", "inside on load finished " + data.getCount());
        arrayList = new ArrayList<ReminderCards>();
        if (data.moveToFirst()) {
            do {
                Log.d("bhaskar", "inside while");
                String desc = data.getString(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION));
                String image_path = data.getString(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE));
                String date = data.getString(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE));
                long rem_id = data.getLong(data.getColumnIndex(RemindersContract.RemindersEntry.COLUMN_REMINDER_ID));
                Log.d("bhaskar", "acs" + desc + image_path + date);
                arrayList.add(new ReminderCards(image_path, desc, date, rem_id));
            } while (data.moveToNext());
        }
        adapter = new ReminderCardsAdapter(arrayList, MainActivity.this);
        recyclerView.setAdapter(adapter);

        Intent intent = new Intent(WIDGET_UPDATE_ACTION);
        getApplicationContext().sendBroadcast(intent);
        Log.d("bhaskar", "broadcast sent");
        recyclerView.invalidate();
        initSwipe();
        adapter.SetOnItemClickListener(new ReminderCardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ReminderDetailActivity.class);
                ReminderCards card = arrayList.get(position);
                intent.putExtra(getString(R.string.item_click_reminder_id), card.getRem_id());
                if (!card.getImage_path().equals("NA")) {
                    intent.putExtra(getString(R.string.image_clickable), true);
                }


                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        if (arrayList.size() == 0) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }


    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getString(R.string.delete_reminder_dialog_title));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.delete_reminder_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.removeItem(position);
                            dialog.dismiss();

                            Snackbar.make(findViewById(R.id.snack), R.string.delete_reminder_snack_message, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();


                        }
                    });
                    builder.setNegativeButton(getString(R.string.delete_reminder_negative), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            adapter.notifyDataSetChanged();
                            // recyclerView.invalidate();
                        }
                    });

                    builder.show();


                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;


                    p.setColor(getResources().getColor(R.color.colorAccent));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_forever_white_24dp);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);

                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("bhaskar", "inside on loader reset");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",

                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),

                Uri.parse("android-app://com.bhaskar.snapreminder/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.bhaskar.snapreminder/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class FacebookUser {
        String reminder_date, reminder_freq, reminder_time, reminder_desc, fb_id, image;

        FacebookUser(String reminder_date, String reminder_desc, String fb_id, String image) {
            this.reminder_date = reminder_date;
            this.reminder_freq = "FREQ=YEARLY;WKST=SU";
            this.reminder_time = "0,0";
            this.reminder_desc = reminder_desc;
            this.fb_id = fb_id;
            this.image = image;

        }

        public void setReminder_freq(String reminder_freq) {
            this.reminder_freq = reminder_freq;
        }

        public void setReminder_time(String reminder_time) {
            this.reminder_time = reminder_time;
        }

        public void setReminder_desc(String reminder_desc) {
            this.reminder_desc = reminder_desc;
        }

        public void setReminder_date(String reminder_date) {
            this.reminder_date = reminder_date;
        }

        public void setFb_id(String fb_id) {
            this.fb_id = fb_id;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getReminder_time() {
            return reminder_time;
        }

        public String getReminder_freq() {
            return reminder_freq;
        }

        public String getReminder_desc() {
            return reminder_desc;
        }

        public String getReminder_date() {
            return reminder_date;
        }

        public String getFb_id() {
            return fb_id;
        }

        public String getImage() {
            return image;
        }
    }

}
