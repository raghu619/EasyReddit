package com.example.android.easyreddit.widget;


import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.easyreddit.R;
import com.example.android.easyreddit.data.FavoriteContract;
import com.example.android.easyreddit.model.RedditData;
import com.example.android.easyreddit.ui.MainActivity;

public class FavoriteWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavoriteViewsFactory();
    }


    class FavoriteViewsFactory implements RemoteViewsService.RemoteViewsFactory {


        Cursor mCursor = null;


        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

            if (mCursor != null)
                mCursor.close();


            final long identifyToken = Binder.clearCallingIdentity();

            mCursor = getContentResolver().query(FavoriteContract.favorite.CONTENT_URI, null,
                    null,
                    null,
                    null);


            Binder.restoreCallingIdentity(identifyToken);


        }

        @Override
        public void onDestroy() {

            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }

        }

        @Override
        public int getCount() {
            return mCursor == null ? 0 : mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION ||
                    mCursor == null || !mCursor.moveToPosition(position)) {

                return null;
            }


            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.favorite_item);
            String title = mCursor.getString(MainActivity.COLUMN_TITLE);
            String subreddit = mCursor.getString(MainActivity.COLUMN_SUBREDDIT);
            int points = mCursor.getInt(MainActivity.COLUMN_POINTS);
            int comments = mCursor.getInt(MainActivity.COLUMN_COMMENTS);

            views.setTextViewText(R.id.widgetTitle, title);
            views.setTextViewText(R.id.widgeSub, "r/" + subreddit);
            views.setTextViewText(R.id.widgetPoints, String.valueOf(points));
            views.setTextViewText(R.id.widgetComments, String.valueOf(comments));

            Intent openDetailActivity = new Intent();


            RedditData item = new RedditData();
            item.setId(mCursor.getString(MainActivity.COLUMN_POST_ID));
            item.setTitle(mCursor.getString(MainActivity.COLUMN_TITLE));
            item.setAuthor(mCursor.getString(MainActivity.COLUMN_AUTHOR));
            item.setThumbnail(mCursor.getString(MainActivity.COLUMN_THUMBNAIL));
            item.setPermalink(mCursor.getString(MainActivity.COLUMN_PERMALINK));
            item.setUrl(mCursor.getString(MainActivity.COLUMN_URL));
            item.setImageUrl(mCursor.getString(MainActivity.COLUMN_IMAGE_URL));
            item.setNumComments(mCursor.getInt(MainActivity.COLUMN_COMMENTS));
            item.setScore(mCursor.getInt(MainActivity.COLUMN_POINTS));
            item.setPostedOn(mCursor.getLong(MainActivity.COLUMN_POSTED_ON));
            item.setOver18(false);
            item.setSubreddit(mCursor.getString(MainActivity.COLUMN_SUBREDDIT));


            openDetailActivity.putExtra(getString(R.string.reddit_data_title), item.getTitle());
            openDetailActivity.putExtra(getString(R.string.reddit_data_subreddit), item.getSubreddit());
            openDetailActivity.putExtra(getString(R.string.reddit_data_image_url), item.getImageUrl());
            openDetailActivity.putExtra(getString(R.string.reddit_data_url), item.getUrl());
            openDetailActivity.putExtra(getString(R.string.reddit_data_score), item.getScore());
            openDetailActivity.putExtra(getString(R.string.reddit_data_thumbnail), item.getThumbnail());
            openDetailActivity.putExtra(getString(R.string.reddit_data_posted_on), item.getPostedOn());
            openDetailActivity.putExtra(getString(R.string.reddit_data_num_comments), item.getNumComments());
            openDetailActivity.putExtra(getString(R.string.reddit_data_permalink), item.getPermalink());
            openDetailActivity.putExtra(getString(R.string.reddit_data_id), item.getId());
            openDetailActivity.putExtra(getString(R.string.reddit_data_author), item.getAuthor());
            openDetailActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            views.setOnClickFillInIntent(R.id.widget_list_item, openDetailActivity);
            return views;


        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.favorite_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }


}
