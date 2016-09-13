package com.bhaskar.snapreminder.controller;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class RemindersProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = buildUriMatcher();
    DatabaseHelper dbhelper;
    static final int reminders = 100;
    static final int tasks = 200;

    @Override
    public boolean onCreate() {
        Log.d("bhaskar", "on Create Reminders Provider");
        dbhelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Cursor returnCursor = null;
        switch (match) {
            case reminders:
                returnCursor = db.query(RemindersContract.RemindersEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            case tasks:
                returnCursor = db.query(RemindersContract.TasksEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case reminders:
                return RemindersContract.RemindersEntry.CONTENT_TYPE;
            case tasks:
                return RemindersContract.TasksEntry.CONTENT_TYPE;


        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri returnUri = null;
        switch (match) {
            case reminders:
                long id = db.insert(RemindersContract.RemindersEntry.TABLE_NAME, null, contentValues);
                if (id > 0)
                    returnUri = RemindersContract.RemindersEntry.buildRemindersUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case tasks:
                long id2 = db.insert(RemindersContract.TasksEntry.TABLE_NAME, null, contentValues);
                if (id2 > 0)
                    returnUri = RemindersContract.RemindersEntry.buildRemindersUri(id2);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        int returnVal = 0;
        int match = uriMatcher.match(uri);
        switch (match) {
            case reminders:
                returnVal = db.delete(RemindersContract.RemindersEntry.TABLE_NAME, s, strings);
                break;
            case tasks:
                returnVal = db.delete(RemindersContract.TasksEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (returnVal != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnVal;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        int returnVal = 0;
        int match = uriMatcher.match(uri);
        switch (match) {
            case reminders:
                returnVal = db.update(RemindersContract.RemindersEntry.TABLE_NAME, contentValues, s, strings);
                break;
            case tasks:
                returnVal = db.update(RemindersContract.TasksEntry.TABLE_NAME, contentValues, s, strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (returnVal != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnVal;
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = RemindersContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, RemindersContract.PATH_REMINDER, reminders);
        matcher.addURI(authority, RemindersContract.PATH_TASK, tasks);

        return matcher;
    }
}
