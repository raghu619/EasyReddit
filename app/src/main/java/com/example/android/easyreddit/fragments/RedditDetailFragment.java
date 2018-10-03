package com.example.android.easyreddit.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.adapters.CommentsViewAdapter;
import com.example.android.easyreddit.data.FavoriteContract;
import com.example.android.easyreddit.model.CommentData;
import com.example.android.easyreddit.ui.MainActivity;
import com.example.android.easyreddit.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RedditDetailFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {



    private static String LOG_TAG = RedditDetailFragment.class.getSimpleName();
    private Bundle mextra;
    private boolean isFavourite;
    private  String request_url;
    String id;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CommentData> mcomments_data;


    @BindView(R.id.commentRecycler)
    RecyclerView mRecyclerView;


    @BindView(R.id.headerImage)
    ImageView  mheaderImage;



    @BindView(R.id.comments)
    TextView mcomments;



    @BindView(R.id.score)
       TextView mscore;



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

        mextra=getActivity().getIntent().getExtras();

        id =  mextra.getString(getString(R.string.reddit_data_id));

        final String commentlink=AppConstants.reddit_base_url+mextra.getString("permalink")+AppConstants.jsonExt;;

        Log.i(LOG_TAG, commentlink);



        settingCommentViews(commentlink);

        Glide.with(getActivity()).load(String.valueOf(mextra.get("image_url"))).into(mheaderImage);
        mcomments.setText(String.valueOf(mextra.getInt("num_comments")));

        mscore.setText(String.valueOf(mextra.getInt("score")));


        favStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavourite){

                    ContentValues values = new ContentValues();
                    values.put(FavoriteContract.favorite.COLUMN_TITLE, mextra.getString("title"));
                    values.put(FavoriteContract.favorite.COLUMN_AUTHOR, mextra.getString("author"));
                    values.put(FavoriteContract.favorite.COLUMN_PERMALINK, mextra.getString("permalink"));
                    values.put(FavoriteContract.favorite.COLUMN_POINTS, mextra.getInt("score"));
                    values.put(FavoriteContract.favorite.COLUMN_COMMENTS, mextra.getInt("num_comments"));
                    values.put(FavoriteContract.favorite.COLUMN_IMAGE_URL, mextra.getString("image_url"));
                    values.put(FavoriteContract.favorite.COLUMN_URL, mextra.getString("url"));
                    values.put(FavoriteContract.favorite.COLUMN_THUMBNAIL, mextra.getString("thumbnail"));
                    values.put(FavoriteContract.favorite.COLUMN_POSTED_ON, mextra.getLong("postedOn"));
                    values.put(FavoriteContract.favorite.COLUMN_POST_ID, id);
                    values.put(FavoriteContract.favorite.COLUMN_SUBREDDIT, mextra.getString("subreddit"));
                    values.put(FavoriteContract.favorite.COLUMN_FAVORITES, 1);
                    getContext().getContentResolver().insert(FavoriteContract.favorite.CONTENT_URI, values);

                    favStar.setSelected(true);
                    isFavourite=true;

                    Toast.makeText(getContext(),"SuccessFully added as a Favorite",Toast.LENGTH_LONG).show();


                }
                else {


                    favStar.setSelected(false);
                    isFavourite=false;
                    getContext().getContentResolver().delete(FavoriteContract.favorite.CONTENT_URI
                            ,FavoriteContract.favorite.COLUMN_POST_ID+"=?",new String[]{id});


                }
            }
        });


        initLoader();


        return rootView;

    }

    private void settingCommentViews(String request_url) {


        mcommentViewAdapter=new CommentsViewAdapter(getActivity(),mcomments_data);

        mRecyclerView.setAdapter(mcommentViewAdapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        fetchingCommentsFromUrl(request_url,getActivity());








    }


    public void fetchingCommentsFromUrl(String url, Context mcontext){

        mcommentViewAdapter.clearAdapter();
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


                          settingCommentsData(data_object);





                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mcommentViewAdapter.notifyDataSetChanged();

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

            commentData.setHtmlText(data_object.getString("body"));
            Log.v("Checking_values",data_object.getString("body")+"\n"
                    +data_object.getLong("created_utc"));
            int points=data_object.getInt("ups")-data_object.getInt("downs");
            commentData.setPoints(points+"");
            commentData.setAuthor(data_object.getString("author"));
            commentData.setPostedOn(getDate(data_object.getLong("created_utc")));
            mcomments_data.add(commentData);


        }

        catch (Exception e){



        }








    }


    private void initLoader() {

        getLoaderManager().initLoader(0, null, this);
    }

    private  String getDate(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("HH:mm  dd/MM/yy", cal).toString();
        return date;

    }


    @Override
    public Loader<Cursor> onCreateLoader(int Id, Bundle args) {
        return new CursorLoader(getContext(),FavoriteContract.favorite.CONTENT_URI,null,
                FavoriteContract.favorite.COLUMN_POST_ID+"=?",new String[]{String.valueOf(id)},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int fav=0;
        isFavourite=false;

        if(data!=null && data.getCount()>0){

            if(data.moveToFirst()){

                fav=data.getInt(MainActivity.COLUMN_FAVORITES);


            }


        }

        if(fav==1){
            isFavourite=true;
            favStar.setSelected(true);
        }
        else
        favStar.setSelected(false);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
