package com.example.android.easyreddit.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.fragments.RedditDetailFragment;
import com.example.android.easyreddit.googleanalytics.AnalyticsApplication;
import com.example.android.easyreddit.model.CommentData;
import com.example.android.easyreddit.utils.AppConstants;
import com.example.android.easyreddit.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedditDetailActivity extends AppCompatActivity  implements RedditDetailFragment.ProgressBarLoad{


    private AnalyticsApplication global_data;
    ArrayList<CommentData> mcomments_data = new ArrayList<>();

    private static String LOG_TAG = RedditDetailActivity.class.getSimpleName();

    @Override
    public void onSucessfulLoad() {

        mdialog.dismiss();

/**/    }

    interface CommentsProcessor {

        void onSuccessLoad(ArrayList<CommentData> mcomments_data);

    }

    String id;
    String commentsUrl;
    CommentsProcessor processor;

    SharedPreferences mprefs;
    @BindView(R.id.toolbar)
    Toolbar mtoolbar;
    Bundle receiving_data;
    private ProgressDialog mdialog;
    @BindView(R.id.no_internet_reddit_detail_layout_id)
    View no_internet_layout_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_detail);
<<<<<<< HEAD

        mprefs = this.getSharedPreferences(getString(R.string.Subreddits_shared_preferences), Context.MODE_PRIVATE);


        global_data = AnalyticsApplication.getmInstance();

        mdialog = new ProgressDialog(this);

        mdialog.setTitle(getString(R.string.dialog_title));
        mdialog.setCancelable(false);
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.setIndeterminate(false);
        mdialog.setMessage(getString(R.string.dialog_message));
        ButterKnife.bind(this);
        mdialog.show();
        setSupportActionBar(mtoolbar);
        receiving_data = getIntent().getExtras();

        id = receiving_data.getString(getString(R.string.reddit_data_id));

        commentsUrl = AppConstants.reddit_base_url + receiving_data.getString(getString(R.string.reddit_data_permalink)) + AppConstants.jsonExt;
        setTitle(receiving_data.getString(getString(R.string.reddit_data_subreddit)));

        Log.v(LOG_TAG, receiving_data.getString(getString(R.string.reddit_data_subreddit)));


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            processor = new CommentsProcessor() {
                @Override
                public void onSuccessLoad(ArrayList<CommentData> mcomments_data) {

                    initFragmentView(mcomments_data);
                 // mdialog.dismiss();



                }
            };


            new CommentsAsyncTask().execute();


        } else {

            mcomments_data.clear();
            mcomments_data = savedInstanceState.getParcelableArrayList(getString(R.string.saved_instance_commments_key));
            initFragmentView(mcomments_data);
          //  mdialog.dismiss();


=======
        ButterKnife.bind(this);
        if(NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            no_internet_layout_id.setVisibility(View.GONE);
            initView(savedInstanceState);
>>>>>>> 3e90bbb9578d538acbeef5f70caaca92efb1842f
        }
        else
          no_internet_layout_id.setVisibility(View.VISIBLE);


    }


    private void initFragmentView(ArrayList<CommentData> mcomments_data) {


        RedditDetailFragment redditDetailFragment = new RedditDetailFragment();
        Bundle data = new Bundle();
        data.putParcelableArrayList(getString(R.string.fragment_comments_data), mcomments_data);
        redditDetailFragment.setArguments(data);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, redditDetailFragment)
                .commit();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainIntent);
            // NavUtils.navigateUpTo(this,new Intent(this,MainActivity.class));


        }


        return super.onOptionsItemSelected(item);
    }


    public void fetchingCommentsFromUrl(String url, final CommentsProcessor processor) {


        JsonArrayRequest jsonArRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.v("values", response.toString());

                try {
                    JSONArray base_data_array = new JSONArray(response.toString())
                            .getJSONObject(1)
                            .getJSONObject(getString(R.string.comments_json_data_key))
                            .getJSONArray(getString(R.string.comments_json_children_key));


                    for (int i = 0; i < base_data_array.length(); i++) {


                        JSONObject data_object = base_data_array.getJSONObject(i)
                                .getJSONObject(getString(R.string.comments_json_data_key));


                        settingCommentsData(data_object);


                    }


                    processor.onSuccessLoad(mcomments_data);


                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });


        global_data.add_request_to_queue(jsonArRequest);


    }


    private void settingCommentsData(JSONObject data_object) {


        CommentData commentData = new CommentData();

        try {


            commentData.setHtmlText(data_object.getString(getString(R.string.comments_json_body_key)));
            Log.v("Checking_values", data_object.getString(getString(R.string.comments_json_body_key)) + "\n"
                    + data_object.getLong("created_utc"));
            int points = data_object.getInt(getString(R.string.comments_json_ups_key)) - data_object.getInt(getString(R.string.comments_json_downs_key));
            commentData.setPoints(points + "");
            commentData.setAuthor(data_object.getString(getString(R.string.comments_json_author_key)));
            commentData.setPostedOn(getDate(data_object.getLong(getString(R.string.comments_json_created_utc_key))));
            mcomments_data.add(commentData);


        } catch (Exception e) {


        }


    }

    private String getDate(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("HH:mm  dd/MM/yy", cal).toString();
        return date;

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.saved_instance_commments_key), mcomments_data);

    }


    class CommentsAsyncTask extends AsyncTask

    {

        @Override
        protected Object doInBackground(Object[] objects) {

            fetchingCommentsFromUrl(commentsUrl, processor);

            return null;
        }


    }

    private void initView(Bundle savedInstanceState) {
        mprefs = this.getSharedPreferences(getString(R.string.Subreddits_shared_preferences), Context.MODE_PRIVATE);
        global_data = AnalyticsApplication.getmInstance();
        mdialog = new ProgressDialog(this);
        mdialog.setTitle(getString(R.string.dialog_title));
        mdialog.setCancelable(false);
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.setIndeterminate(false);
        mdialog.setMessage(getString(R.string.dialog_message));
        mdialog.show();
        setSupportActionBar(mtoolbar);
        receiving_data = getIntent().getExtras();

        id = receiving_data.getString(getString(R.string.reddit_data_id));

        commentsUrl = AppConstants.reddit_base_url + receiving_data.getString(getString(R.string.reddit_data_permalink)) + AppConstants.jsonExt;
        setTitle(receiving_data.getString(getString(R.string.reddit_data_subreddit)));

        Log.v(LOG_TAG, receiving_data.getString(getString(R.string.reddit_data_subreddit)));


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            processor = new CommentsProcessor() {
                @Override
                public void onSuccessLoad(ArrayList<CommentData> mcomments_data) {

                    initFragmentView(mcomments_data);



                }
            };


            new CommentsAsyncTask().execute();


        } else {

            mcomments_data.clear();
            mcomments_data = savedInstanceState.getParcelableArrayList(getString(R.string.saved_instance_commments_key));
            initFragmentView(mcomments_data);



        }
    }







}



