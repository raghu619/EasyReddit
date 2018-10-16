package com.example.android.easyreddit.ui;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.fragments.RedditDetailFragment;
import com.example.android.easyreddit.googleanalytics.AnalyticsApplication;
import com.example.android.easyreddit.model.CommentData;
import com.example.android.easyreddit.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedditDetailActivity extends AppCompatActivity
{


    private AnalyticsApplication global_data;
    ArrayList<CommentData> mcomments_data=new ArrayList<>();

    private static String LOG_TAG = RedditDetailActivity.class.getSimpleName();
    interface CommentsProcessor{

        void onSuccessLoad(ArrayList<CommentData> mcomments_data);

    }
    String id;
    String commentsUrl;
    CommentsProcessor processor;


    @BindView(R.id.toolbar)
    Toolbar mtoolbar;

    Bundle receiving_data;

    private ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_detail);

        global_data=AnalyticsApplication.getmInstance();

        mdialog=new ProgressDialog(this);

        mdialog.setTitle(getString(R.string.dialog_title));

        mdialog.setMessage(getString(R.string.dialog_message));
        ButterKnife.bind(this);
        mdialog.show();
        setSupportActionBar(mtoolbar);
        receiving_data=getIntent().getExtras();

        id=receiving_data.getString(getString(R.string.reddit_data_id));

        commentsUrl=AppConstants.reddit_base_url+ receiving_data.getString(getString(R.string.reddit_data_permalink))+AppConstants.jsonExt;
        setTitle(receiving_data.getString(getString(R.string.reddit_data_subreddit)));

        Log.v(LOG_TAG,receiving_data.getString(getString(R.string.reddit_data_subreddit)));


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        if(savedInstanceState==null) {
            processor = new CommentsProcessor() {
                @Override
                public void onSuccessLoad(ArrayList<CommentData> mcomments_data) {

                    initFragmentView(mcomments_data);
                    mdialog.dismiss();





                }
            };


            new CommentsAsyncTask().execute();



        }

        else {

            mcomments_data.clear();
            mcomments_data=savedInstanceState.getParcelableArrayList(getString(R.string.saved_instance_commments_key));
            initFragmentView(mcomments_data);
            mdialog.dismiss();


        }







    }

    private void initFragmentView(ArrayList<CommentData> mcomments_data) {



        RedditDetailFragment redditDetailFragment=new RedditDetailFragment();



        Bundle data=new Bundle();

        data.putParcelableArrayList(getString(R.string.fragment_comments_data),mcomments_data);

        redditDetailFragment.setArguments(data);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout,redditDetailFragment)
                .commit();









    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            NavUtils.navigateUpTo(this,new Intent(this,MainActivity.class));
            return  true;

        }



        return super.onOptionsItemSelected(item);
    }


    public void fetchingCommentsFromUrl(String url, final CommentsProcessor processor){



        JsonArrayRequest jsonArRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
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



        CommentData commentData=new CommentData();

        try {



            commentData.setHtmlText(data_object.getString( getString(R.string.comments_json_body_key)));
            Log.v("Checking_values",data_object.getString( getString(R.string.comments_json_body_key))+"\n"
                    +data_object.getLong("created_utc"));
            int points=data_object.getInt( getString(R.string.comments_json_ups_key))-data_object.getInt(getString(R.string.comments_json_downs_key));
            commentData.setPoints(points+"");
            commentData.setAuthor(data_object.getString(getString(R.string.comments_json_author_key)));
            commentData.setPostedOn(getDate(data_object.getLong(getString(R.string.comments_json_created_utc_key))));
            mcomments_data.add(commentData);


        }

        catch (Exception e){



        }








    }

    private  String getDate(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("HH:mm  dd/MM/yy", cal).toString();
        return date;

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.saved_instance_commments_key),mcomments_data);

    }


//    private void setupWindowAnimations() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Slide slide = new Slide();
//            slide.setSlideEdge(Gravity.LEFT);
//            slide.setDuration(1000);
//            getWindow().setExitTransition(slide);
//        }
//    }



    class CommentsAsyncTask extends AsyncTask

    {

        @Override
        protected Object doInBackground(Object[] objects) {

            fetchingCommentsFromUrl(commentsUrl, processor);

            return null;
        }


    }


    }



