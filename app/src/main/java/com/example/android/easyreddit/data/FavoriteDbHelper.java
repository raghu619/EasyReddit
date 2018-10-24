package com.example.android.easyreddit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteDbHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favorite.db";


    public FavoriteDbHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_QUERY = "CREATE TABLE " + FavoriteContract.favorite.TABLE_NAME
                + " ("
                + FavoriteContract.favorite._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoriteContract.favorite.COLUMN_POST_ID + " STRING NOT NULL, "
                + FavoriteContract.favorite.COLUMN_AUTHOR + " TEXT, "
                + FavoriteContract.favorite.COLUMN_THUMBNAIL + " TEXT, "
                + FavoriteContract.favorite.COLUMN_TITLE + " TEXT NOT NULL, "
                + FavoriteContract.favorite.COLUMN_PERMALINK + " TEXT, "
                + FavoriteContract.favorite.COLUMN_URL + " TEXT, "
                + FavoriteContract.favorite.COLUMN_IMAGE_URL + " TEXT, "
                + FavoriteContract.favorite.COLUMN_POINTS + " INTEGER, "
                + FavoriteContract.favorite.COLUMN_COMMENTS + " INTEGER, "
                + FavoriteContract.favorite.COLUMN_FAVORITES + " INTEGER NOT NULL, "
                + FavoriteContract.favorite.COLUMN_POSTED_ON + " LONG, "
                + FavoriteContract.favorite.COLUMN_SUBREDDIT + " TEXT);";


        db.execSQL(CREATE_TABLE_QUERY);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.favorite.TABLE_NAME);
        onCreate(db);
    }
}
