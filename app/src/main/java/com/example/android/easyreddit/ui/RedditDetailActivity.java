package com.example.android.easyreddit.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.android.easyreddit.R;
import com.example.android.easyreddit.fragments.RedditDetailFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedditDetailActivity extends AppCompatActivity {

    private static String LOG_TAG = RedditDetailActivity.class.getSimpleName();


    String id;


    @BindView(R.id.toolbar)
    Toolbar mtoolbar;
    Bundle receiving_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_detail);

        ButterKnife.bind(this);
        setSupportActionBar(mtoolbar);
        receiving_data=getIntent().getExtras();
        id=receiving_data.getString(getString(R.string.reddit_data_id));

        mtoolbar.setTitle(receiving_data.getString(getString(R.string.reddit_data_subreddit)));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        initFragmentView();




    }

    private void initFragmentView() {

        RedditDetailFragment redditDetailFragment=new RedditDetailFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout,redditDetailFragment)
                .commit();




    }
}
