package com.example.android.easyreddit.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {




    public static final String AUTHORITY = "com.udacity.easyreddit";


    public static final Uri BASE_URI=Uri.parse("content://"+AUTHORITY);



    public static final  class  favorite implements BaseColumns{

        public static final  String TABLE_NAME="favorites";
        public static final Uri CONTENT_URI=BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static final String CONTENT_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_URI.toString();
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_URI.toString();




        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_PERMALINK = "permalink";
        public static final String COLUMN_COMMENTS = "num_comments";
        public static final String COLUMN_THUMBNAIL= "thumbnail";
        public static final String COLUMN_FAVORITES = "favorites";
        public static final String COLUMN_SUBREDDIT = "subreddit";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_POSTED_ON  = "posted_on";
        public static final String COLUMN_POINTS = "points";
        public static final String COLUMN_AUTHOR = "author";





       public static Uri buildUri(Long id){


           return ContentUris.withAppendedId(CONTENT_URI,id);
       }













    }






}
