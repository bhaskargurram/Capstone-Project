package com.bhaskar.snapreminder.controller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by bhaskar on 1/2/16.
 */
public class RemindersContract {


    /*
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_ORIGINAL_TITLE = 2;
    public static final int COL_TITLE = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_BACKDROP_PATH = 6;
    public static final int COL_ADULT = 7;
    public static final int COL_ORIGINAL_LANGUAGE = 8;
    public static final int COL_POSTER_PATH = 9;
    public static final int COL_POPULARITY = 10;
    public static final int COL_VOTE_AVERAGE = 11;
    public static final int COL_VOTE_COUNT = 12;
*/

    public static final String CONTENT_AUTHORITY = "com.bhaskar.snapreminder";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_REMINDER = "reminders";
    public static final String PATH_TASK = "tasks";

    public static class RemindersEntry implements BaseColumns {
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_REMINDER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_REMINDER;
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_REMINDER).build();

        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_REMINDER_ID = "reminder_id";
        public static final String COLUMN_REMINDER_DATE = "reminder_date";
        public static final String COLUMN_REMINDER_TIME = "reminder_time";
        public static final String COLUMN_REMINDER_FREQ = "reminder_freq";
        public static final String COLUMN_REMINDER_DESCRIPTION = "reminder_desc";
        public static final String COLUMN_REMINDER_IMAGE = "reminder_image";
        public static final String COLUMN_REMINDER_FACEBOOK = "facebook_id";

        public static Uri buildRemindersUri(long id) {

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class TasksEntry implements BaseColumns {
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_TASK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_TASK;
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_TASK).build();

        public static final String TABLE_NAME = "task";
        public static final String COLUMN_REMINDER_ID = "reminder_id";
        public static final String COLUMN_SUBTASK_NO = "subtask_no";
        public static final String COLUMN_TASK_NAME = "task_name";
        public static final String COLUMN_SELECTED = "column_selected";

        public static Uri buildTasksUri(long id) {

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
