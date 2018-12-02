package com.example.android.easyreddit.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;


import com.example.android.easyreddit.adapters.RedditRecyclierView;
import com.example.android.easyreddit.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MangeSubreddit extends AppCompatActivity {

    private static String LOG_TAG = MangeSubreddit.class.getSimpleName();
    private LinearLayoutManager linearLayoutManager;
    private RedditRecyclierView mRecyclerViewAdapter;
    private ItemTouchHelper mItemTouchHelper;
    final Context c = this;
    SharedPreferences prefs;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.subList)
    RecyclerView subredditList;


    @BindView(R.id.fab)
    FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mange_subreddit);
        ButterKnife.bind(this);
    }



}
