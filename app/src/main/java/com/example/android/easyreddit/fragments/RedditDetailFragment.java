package com.example.android.easyreddit.fragments;


import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.adapters.CommentsViewAdapter;
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
import butterknife.internal.Utils;

public class RedditDetailFragment  extends Fragment {



    private static String LOG_TAG = RedditDetailFragment.class.getSimpleName();



   private Bundle mextra;
   private  String request_url;
 public static   ArrayList<CommentData> mcomments=new ArrayList<CommentData>();


    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CommentData> mcomments_data;


    @BindView(R.id.commentRecycler)
    RecyclerView mRecyclerView;



    CommentsViewAdapter mcommentViewAdapter;


    @BindView(R.id.menu)
    ImageButton menu;


    @BindView(R.id.addFav)
    ImageButton favStar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


         final View rootView=inflater.inflate(R.layout.fragment_detail,container,false);




        ButterKnife.bind(this,rootView);

        mcomments_data=new ArrayList<>();

        mextra=getArguments();

        if(mextra==null)
            mextra=getActivity().getIntent().getExtras();

        String commentlink=mextra.getString("permalink");

        request_url= AppConstants.reddit_base_url +commentlink+AppConstants.jsonExt;




        return rootView;

    }




    public  static ArrayList<CommentData>fetchingCommentsFromUrl(String url, Context mcontext){


        RequestQueue requestQueue= Volley.newRequestQueue(mcontext);
        JsonArrayRequest jsonArRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                Log.v("values", response.toString());

                try {
                    JSONArray base_data_array = new JSONArray(response.toString())
                            .getJSONObject(1)
                            .getJSONObject("data")
                            .getJSONArray("children");


                    for (int i = 0; i < base_data_array.length(); i++) {


                        JSONObject data_object = base_data_array.getJSONObject(i)
                                .getJSONObject("data");


                           mcomments= settingCommentsData(data_object);





                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        requestQueue.add(jsonArRequest);

        return mcomments;


    }

    private static   String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("HH:mm  dd/MM/yy", cal).toString();
        return date;

    }


    public  static  ArrayList<CommentData>settingCommentsData(JSONObject data_object) {

        CommentData commentData=new CommentData();

        try {

            commentData.setHtmlText(data_object.getString("body"));
            Log.v("Checking_values",data_object.getString("body")+"\n"
                    +data_object.getLong("created_utc"));
            int points=data_object.getInt("ups")-data_object.getInt("downs");
            commentData.setPoints(points+"");
            commentData.setAuthor(data_object.getString("author"));
            commentData.setPostedOn(getDate(data_object.getLong("created_utc")));
            mcomments.add(commentData);

            
        }

        catch (Exception e){



        }




         return mcomments;


    }




}
