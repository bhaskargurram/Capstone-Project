package com.bhaskar.snapreminder.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bhaskar on 1/2/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reminders"+".db";

    public DatabaseHelper(Context mcontext) {
        super(mcontext, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("bhaskar", "Database Helper");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("bhaskar", "on Create Database Helper");
        final String creatTask = "CREATE TABLE " + RemindersContract.TasksEntry.TABLE_NAME
                + " ( "
                + RemindersContract.TasksEntry._ID + " INTEGER PRIMARY KEY, "
                + RemindersContract.TasksEntry.COLUMN_REMINDER_ID + " INTEGER, "
                + RemindersContract.TasksEntry.COLUMN_SUBTASK_NO + " INTEGER, "
                + RemindersContract.TasksEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, "
                + RemindersContract.TasksEntry.COLUMN_SELECTED + " INTEGER NOT NULL); ";

        final String creatRem = "CREATE TABLE " + RemindersContract.RemindersEntry.TABLE_NAME
                + " ( "
                + RemindersContract.RemindersEntry._ID + " INTEGER PRIMARY KEY, "
                + RemindersContract.RemindersEntry.COLUMN_REMINDER_ID + " INTEGER UNIQUE NOT NULL, "
                + RemindersContract.RemindersEntry.COLUMN_REMINDER_DATE + " TEXT NOT NULL, "
                + RemindersContract.RemindersEntry.COLUMN_REMINDER_TIME + " TEXT NOT NULL, "
                + RemindersContract.RemindersEntry.COLUMN_REMINDER_DESCRIPTION + " TEXT NOT NULL, "
                + RemindersContract.RemindersEntry.COLUMN_REMINDER_FREQ + " TEXT NOT NULL, "
                + RemindersContract.RemindersEntry.COLUMN_REMINDER_IMAGE + " TEXT NOT NULL, "
                + RemindersContract.RemindersEntry.COLUMN_REMINDER_FACEBOOK + " TEXT NOT NULL " +
                ");";
        sqLiteDatabase.execSQL(creatTask);
        sqLiteDatabase.execSQL(creatRem);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RemindersContract.RemindersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RemindersContract.TasksEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
