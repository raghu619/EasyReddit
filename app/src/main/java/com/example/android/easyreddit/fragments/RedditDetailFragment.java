package com.example.android.easyreddit.fragments;



import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.adapters.CommentsViewAdapter;
import com.example.android.easyreddit.data.FavoriteContract;
import com.example.android.easyreddit.googleanalytics.AnalyticsApplication;
import com.example.android.easyreddit.model.CommentData;
import com.example.android.easyreddit.ui.MainActivity;
import com.example.android.easyreddit.utils.GlideApp;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedditDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static String LOG_TAG = RedditDetailFragment.class.getSimpleName();
    String id;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CommentData> mcomments_data;
    @BindView(R.id.commentRecycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.headerImage)
    ImageView mheaderImage;
    @BindView(R.id.comments)
    TextView mcomments;
    @BindView(R.id.score)
    TextView mscore;
    CommentsViewAdapter mcommentViewAdapter;
    @BindView(R.id.menu)
    ImageButton menu;
    @BindView(R.id.addFav)
    ImageButton favStar;
    @BindView(R.id.linearheader)
    LinearLayout mheaderLayout;
    @BindView(R.id.headerTitle)
    TextView mheaderTextView;
    @BindView(R.id.fragment_progress_bar)
    ProgressBar mprogressbar;
    private Bundle mextras;
    private boolean isFavourite;
    private Tracker mTracker;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        ButterKnife.bind(this, rootView);


        mcomments_data = new ArrayList<>();

        mextras = getActivity().getIntent().getExtras();
        if (mextras == null && getArguments() != null) {
            mprogressbar.setVisibility(View.VISIBLE);
            mextras = getArguments();


        }


        id = mextras.getString(getString(R.string.reddit_data_id));

        GlideApp.with(getActivity()).load(String.valueOf(mextras.get(getString(R.string.reddit_data_url)))).diskCacheStrategy(DiskCacheStrategy.DATA).listener(new
           RequestListener<Drawable>() {
           @Override
           public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {


               mprogressbar.setVisibility(View.GONE);
               return false;
           }

           @Override
           public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
               mprogressbar.setVisibility(View.GONE);

                   return false;
               }
           }).into(mheaderImage);


        mcomments_data = getArguments().getParcelableArrayList(getString(R.string.fragment_comments_data));
        if(mcommentViewAdapter!=null)
            mcommentViewAdapter=null;
        mcommentViewAdapter = new CommentsViewAdapter(getActivity(), mcomments_data);

        mRecyclerView.setAdapter(mcommentViewAdapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mcomments.setText(String.valueOf(mextras.getInt(getString(R.string.reddit_data_num_comments))));
        mscore.setText(String.valueOf(mextras.getInt(getString(R.string.reddit_data_score))));
        mheaderTextView.setText(mextras.getString(getString(R.string.reddit_data_title)));


        initLoader();


        favStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavourite) {

                    ContentValues values = new ContentValues();
                    values.put(FavoriteContract.favorite.COLUMN_TITLE, mextras.getString(getString(R.string.reddit_data_title)));
                    values.put(FavoriteContract.favorite.COLUMN_AUTHOR, mextras.getString(getString(R.string.reddit_data_author)));
                    values.put(FavoriteContract.favorite.COLUMN_PERMALINK, mextras.getString(getString(R.string.reddit_data_permalink)));
                    values.put(FavoriteContract.favorite.COLUMN_POINTS, mextras.getInt(getString(R.string.reddit_data_score)));
                    values.put(FavoriteContract.favorite.COLUMN_COMMENTS, mextras.getInt(getString(R.string.reddit_data_num_comments)));
                    values.put(FavoriteContract.favorite.COLUMN_IMAGE_URL, mextras.getString(getString(R.string.reddit_data_image_url)));
                    values.put(FavoriteContract.favorite.COLUMN_URL, mextras.getString(getString(R.string.reddit_data_url)));
                    values.put(FavoriteContract.favorite.COLUMN_THUMBNAIL, mextras.getString(getString(R.string.reddit_data_thumbnail)));
                    values.put(FavoriteContract.favorite.COLUMN_POSTED_ON, mextras.getLong(getString(R.string.reddit_data_posted_on)));
                    values.put(FavoriteContract.favorite.COLUMN_POST_ID, id);
                    values.put(FavoriteContract.favorite.COLUMN_SUBREDDIT, mextras.getString(getString(R.string.reddit_data_subreddit)));
                    values.put(FavoriteContract.favorite.COLUMN_FAVORITES, 1);
                    getContext().getContentResolver().insert(FavoriteContract.favorite.CONTENT_URI, values);

                    favStar.setSelected(true);
                    isFavourite = true;

                    Toast.makeText(getContext(), getString(R.string.favorites_toast_message), Toast.LENGTH_LONG).show();


                } else {


                    favStar.setSelected(false);
                    isFavourite = false;
                    getContext().getContentResolver().delete(FavoriteContract.favorite.CONTENT_URI
                            , FavoriteContract.favorite.COLUMN_POST_ID + "=?", new String[]{id});

                    Toast.makeText(getContext(), getString(R.string.favorites_deleted_toast_message), Toast.LENGTH_LONG).show();


                }

                Context context = getContext();
                Intent dataUpdatedIntent = new Intent(getString(R.string.data_update_key))
                        .setPackage(context.getPackageName());
                context.sendBroadcast(dataUpdatedIntent);
            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), menu);

                popup.getMenuInflater()
                        .inflate(R.menu.share_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.open:

                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(mextras.getString(getString(R.string.share_link_url))));
                                startActivity(i);
                                break;
                            case R.id.share:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, mextras.getString(getString(R.string.share_link_url)));
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_link_title)));

                        }
                        return true;
                    }
                });

                popup.show();
            }
        });


        AdView mAdView = (AdView) rootView.findViewById(R.id.googleadView);
        AdRequest adRequest = new AdRequest.Builder().
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR).
                build();
        mAdView.loadAd(adRequest);


        return rootView;


    }


    private void initLoader() {

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int Id, Bundle args) {
        return new CursorLoader(getContext(), FavoriteContract.favorite.CONTENT_URI, null,
                FavoriteContract.favorite.COLUMN_POST_ID + "=?", new String[]{String.valueOf(id)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int fav = 0;
        isFavourite = false;

        if (data != null && data.getCount() > 0) {

            if (data.moveToFirst()) {

                fav = data.getInt(MainActivity.COLUMN_FAVORITES);


            }


        }

        if (fav == 1) {
            isFavourite = true;
            favStar.setSelected(true);
        } else
            favStar.setSelected(false);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(getString(R.string.tracker_screen_detail_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


}
