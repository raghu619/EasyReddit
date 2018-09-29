package com.example.android.easyreddit.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.adapters.RedditListViewAdapter;
import com.example.android.easyreddit.fragments.RedditDetailFragment;
import com.example.android.easyreddit.model.CommentData;
import com.example.android.easyreddit.model.RedditData;
import com.example.android.easyreddit.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RedditListViewAdapter.OnItemClickListener{

    private static String LOG_TAG = MainActivity.class.getSimpleName();


    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;


    private ActionBarDrawerToggle mDrawerToggle;

    @BindView(R.id.reddititem_list)
    RecyclerView mRecyclerView;


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;


    Menu drawerMenu;

    SharedPreferences prefs;

    Parcelable mListState;
    private ArrayList<RedditData> mreddit_item_list = new ArrayList<RedditData>();
    private String sortBy = "";
    private String subReddit = "";
    private String mSearch = "";
    private int counter = 0;


    private RedditListViewAdapter adapter;


    private boolean mTwoPane;
    private String after_id;

    private Menu sortMenu;

    private SearchView mSearchView;






                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);
                    ButterKnife.bind(this);
                    settingUpToolBar();

                    drawerMenu=mNavigationView.getMenu();

                    settingUpDrawerLayout();


                    settingUpNavigationView();

                    makingCustomUrl();

                    CommentsAsyncTask task=new CommentsAsyncTask();

                    task.execute();







                }

                private void settingUpNavigationView() {

                    mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                            if(item.getGroupId()==R.id.fav_group){

                                mDrawerLayout.closeDrawers();
                                return true;
                            }

                            if(item.getGroupId()==R.id.subscribed_group){


                                mDrawerLayout.closeDrawers();

                                return true;
                            }

                            return false;
                        }
                    });


                }




                private void settingUpDrawerLayout() {

                    mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,
                            R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    mDrawerLayout.addDrawerListener(mDrawerToggle);
                    mDrawerToggle.syncState();


                }

                private void settingUpToolBar() {

                    setSupportActionBar(toolbar);
                    toolbar.setTitle(getTitle());
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                    toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
                }






                   public  void makingCustomUrl() {
                          String subReddit= AppConstants.reddit_base_url + AppConstants.json_end;
                          updateList(subReddit);




                   }





                public void updateList( String url)
                {


                    Log.d(LOG_TAG, url);

                    adapter=new RedditListViewAdapter(this,mreddit_item_list);

                    mRecyclerView.setAdapter(adapter);
                    RequestQueue requestQueue= Volley.newRequestQueue(this);

                    adapter.clearAdapter();
                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {

                            Log.d(LOG_TAG, response.toString());


                            try {

                                            JSONObject data = response.getJSONObject("data");
                                            after_id = data.getString("after");
                                            JSONArray children_array=data.getJSONArray("children");
                                                        for(int i=0;i<children_array.length();i++)

                                                        {
                                                               JSONObject each_object=children_array.getJSONObject(i).getJSONObject("data");

                                                               RedditData data_item=new RedditData();

                                                               data_item.setTitle(each_object.getString("title"));

                                                               data_item.setAuthor(each_object.getString("author"));
                                                               data_item.setNumComments(each_object.getInt("num_comments"));
                                                               data_item.setScore(each_object.getInt("score"));
                                                               data_item.setThumbnail(each_object.getString("thumbnail"));
                                                               data_item.setOver18(each_object.getBoolean("over_18"));
                                                               data_item.setUrl(each_object.getString("url"));
                                                               data_item.setId(each_object.getString("id"));
                                                               data_item.setSubreddit(each_object.getString("subreddit"));
                                                               data_item.setPermalink(each_object.getString("permalink"));
                                                               data_item.setPostedOn(each_object.getLong("created_utc"));
                                                               data_item.setId(each_object.getString("id"));

                                                           try
                                                           {
                                                               data_item.setImageUrl(each_object.getJSONObject("preview")
                                                                       .getJSONArray("images")
                                                                       .getJSONObject(0)
                                                                       .getJSONObject("source")
                                                                       .getString("url"));

                                                           }

                                                           catch (JSONException e)
                                                           {
                                                               e.printStackTrace();

                                                           }




                                                           mreddit_item_list.add(data_item);




                                                        }



                                                       }
                                                        catch (JSONException e)
                                                           {
                                                               e.printStackTrace();

                                                           }


                                                    adapter.notifyDataSetChanged();

                                                   String temp=AppConstants.reddit_base_url + "r/gaming/comments/9ixmx1/finding_a_partner_for_life/"+AppConstants.json_end;





                                              }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }

                                        });



                                        requestQueue.add(jsonObjectRequest);

                }


                @Override
                public void OnItemClick(View view, int position) {



                    Intent DetailActivityIntent = new Intent(getBaseContext(), RedditDetailActivity.class);
                    RedditData data_item=mreddit_item_list.get(position);

                    Bundle sending_arguments=new Bundle();

                    sending_arguments.putString("title",data_item.getTitle());
                    sending_arguments.putString("thumbnail", data_item.getThumbnail());
                    sending_arguments.putLong("postedOn", data_item.getPostedOn());
                    sending_arguments.putInt("num_comments", data_item.getNumComments());
                    sending_arguments.putString("permalink", data_item.getPermalink());
                    sending_arguments.putString("id", data_item.getId());
                    sending_arguments.putString("author", data_item.getAuthor());
                    sending_arguments.putString("subreddit", data_item.getSubreddit());
                    sending_arguments.putString("image_url", data_item.getImageUrl());
                    sending_arguments.putString("url", data_item.getUrl());
                    sending_arguments.putInt("score", data_item.getScore());


                    DetailActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    DetailActivityIntent.putExtras(sending_arguments);

                    startActivity(DetailActivityIntent);








                }


    class CommentsAsyncTask extends AsyncTask<String, Void, String> {

         ArrayList<CommentData>commentData;
        @Override
        protected String doInBackground(String... strings) {





            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            commentData=RedditDetailFragment.fetchingCommentsFromUrl("https://www.reddit.com/r/gaming/comments/9ixmx1/finding_a_partner_for_life/.json",getApplicationContext());
            Log.v(LOG_TAG,"commentData");

            Log.v(LOG_TAG,"commentdata"+commentData.get(1).getAuthor());

            super.onPostExecute(s);
        }
    }





}


