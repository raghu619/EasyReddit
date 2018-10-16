package com.example.android.easyreddit.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.model.CommentData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AsyncCommentsFetcher extends AsyncTask {
    private Context mContext;
    ArrayList<CommentData> mcomments_data;
    String commentsUrl;


    RedditDetailActivity.CommentsProcessor processor;







    public AsyncCommentsFetcher(Context mContext, ArrayList<CommentData> mcomments_data,
                                String commentsUrl, RedditDetailActivity.CommentsProcessor processor){
        this.mContext=mContext;

        this.mcomments_data=mcomments_data;

        this.commentsUrl=commentsUrl;

        this.processor=processor;



    }

    @Override
    protected Object doInBackground(Object[] objects) {

          fetchingCommentsFromUrl(commentsUrl,processor);

        return null;
    }






    public void fetchingCommentsFromUrl(String url, final RedditDetailActivity.CommentsProcessor processor){


        RequestQueue requestQueue= Volley.newRequestQueue(mContext);
        JsonArrayRequest jsonArRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.v("values", response.toString());

                try {
                    JSONArray base_data_array = new JSONArray(response.toString())
                            .getJSONObject(1)
                            .getJSONObject(mContext.getString(R.string.comments_json_data_key))
                            .getJSONArray(mContext.getString(R.string.comments_json_children_key));


                    for (int i = 0; i < base_data_array.length(); i++) {


                        JSONObject data_object = base_data_array.getJSONObject(i)
                                .getJSONObject(mContext.getString(R.string.comments_json_data_key));


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


        requestQueue.add(jsonArRequest);







    }


    private void settingCommentsData(JSONObject data_object) {



        CommentData commentData=new CommentData();

        try {



            commentData.setHtmlText(data_object.getString(mContext. getString(R.string.comments_json_body_key)));
            Log.v("Checking_values",data_object.getString( mContext.getString(R.string.comments_json_body_key))+"\n"
                    +data_object.getLong("created_utc"));
            int points=data_object.getInt( mContext.getString(R.string.comments_json_ups_key))-data_object.getInt(mContext.getString(R.string.comments_json_downs_key));
            commentData.setPoints(points+"");
            commentData.setAuthor(data_object.getString(mContext.getString(R.string.comments_json_author_key)));
            commentData.setPostedOn(getDate(data_object.getLong(mContext.getString(R.string.comments_json_created_utc_key))));
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













}
