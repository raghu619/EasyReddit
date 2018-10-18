package com.example.android.easyreddit.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

 public class FavoriteContentProvider  extends ContentProvider {


    private static String LOG_TAG = FavoriteContentProvider.class.getSimpleName();
    private static final UriMatcher mUriMatcher;
    private static final int TABLE = 1;
    private static  final  int TABLE_ITEM=101;
    private SQLiteOpenHelper mDbHelper;

    static {


        mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(FavoriteContract.AUTHORITY,FavoriteContract.favorite.TABLE_NAME,TABLE);
        mUriMatcher.addURI(FavoriteContract.AUTHORITY,FavoriteContract.favorite.TABLE_NAME+"/#",TABLE_ITEM);



    }



    public  FavoriteContentProvider(){


    }

    @Override
    public boolean onCreate() {
        mDbHelper=new FavoriteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (mUriMatcher.match(uri)){
            case TABLE:
                cursor=db.query(FavoriteContract.favorite.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unsupported Uri" + uri);

        }


            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case  TABLE:
                return FavoriteContract.favorite.CONTENT_TYPE;

            case TABLE_ITEM:
                return FavoriteContract.favorite.CONTENT_ITEM_TYPE;
             default:
                 throw new IllegalArgumentException("Unsupported Uri" + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri return_uri=null;
        switch (mUriMatcher.match(uri)){
            case TABLE:
                long id=db.insert(FavoriteContract.favorite.TABLE_NAME,null,values);

                if(id!=-1){

                    return_uri=FavoriteContract.favorite.buildUri(id);

                }

                break;


            default:
                throw new IllegalArgumentException("Unsupported Uri For Insertion " + uri);





        }



            getContext().getContentResolver().notifyChange(uri, null);


        return return_uri;







    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        int delete;

        switch (mUriMatcher.match(uri)) {

            case TABLE_ITEM :
                delete=db.delete(FavoriteContract.favorite.TABLE_NAME,selection,selectionArgs);
                break;

            case TABLE:
                delete=db.delete(FavoriteContract.favorite.TABLE_NAME,selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported Uri For Deletion " + uri);


        }

        if( delete!=0)
            getContext().getContentResolver().notifyChange(uri, null);



        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int update;
        switch (mUriMatcher.match(uri)){

            case TABLE_ITEM:{
                update = db.update  (FavoriteContract.favorite.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            default:
                throw new IllegalArgumentException("Unsupported Uri For Updating " + uri);



        }

        if(getContext()!=null)
            getContext().getContentResolver().notifyChange(uri, null);

        return update;
    }
}
