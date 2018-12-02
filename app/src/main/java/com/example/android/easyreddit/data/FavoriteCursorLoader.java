package com.example.android.easyreddit.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

public class FavoriteCursorLoader extends AsyncTaskLoader<Cursor> {


    public FavoriteCursorLoader(Context context) {
        super(context);

    }


    Cursor mCursor = null;


    @Override
    protected void onStartLoading() {

        if (mCursor != null)
            deliverResult(mCursor);

        else
            forceLoad();


    }

    @Override
    public Cursor loadInBackground() {

        try {

            return getContext().getContentResolver().query(FavoriteContract.favorite.CONTENT_URI, null,
                    FavoriteContract.favorite.COLUMN_FAVORITES + "=?", new String[]{Integer.toString(1)}, null);
        } catch (Exception e) {

            e.printStackTrace();


            return null;

        }


    }

    @Override
    public void deliverResult(Cursor data) {
        mCursor = data;
        super.deliverResult(data);
    }


}


