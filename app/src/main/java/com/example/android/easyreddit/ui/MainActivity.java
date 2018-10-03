package com.example.android.easyreddit.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.adapters.RedditListViewAdapter;
import com.example.android.easyreddit.data.FavoriteContract;
import com.example.android.easyreddit.data.FavoriteCursorLoader;
import com.example.android.easyreddit.fragments.RedditDetailFragment;
import com.example.android.easyreddit.model.CommentData;
import com.example.android.easyreddit.model.RedditData;
import com.example.android.easyreddit.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RedditListViewAdapter.OnItemClickListener,LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = MainActivity.class.getSimpleName();


    public  static  int   COLUMN_POST_ID=1;
    public  static  int   COLUMN_AUTHOR=2;
    public  static  int   COLUMN_THUMBNAIL=3;
    public  static  int   COLUMN_TITLE=4;
    public  static  int   COLUMN_PERMALINK=5;
    public  static  int   COLUMN_URL=6;
    public  static  int   COLUMN_IMAGE_URL=7;
    public  static  int   COLUMN_POINTS=8;
    public  static  int   COLUMN_COMMENTS=9;
    public  static  int   COLUMN_FAVORITES=10;
    public  static  int   COLUMN_POSTED_ON=11;
    public  static  int   COLUMN_SUBREDDIT =12;


    private static final int DRAWER_MENU_GROUP_ID=34;


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

    SharedPreferences mprefs;

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

        mprefs=this.getSharedPreferences(getString(R.string.Subreddits_shared_preferences),Context.MODE_PRIVATE);



        settingUpDrawerLayout();
        settingNavbarSubreddits();

        settingUpNavigationView();


        if(savedInstanceState==null) {
            makingCustomUrl(getResources().getString(R.string.HomePage));
            getSupportActionBar().setTitle(R.string.HomePage);

        }






    }

    private void settingNavbarSubreddits() {
     if(mprefs.getBoolean(getString(R.string.first_run),true)){

         mprefs.edit().putString(getString(R.string.Subreddits_key),getString(R.string.initial_subs)).commit();
         mprefs.edit().putBoolean(getString(R.string.first_run),false).commit();


     }

        String subString = mprefs.getString(getString(R.string.Subreddits_key), "");

        List<String>mItems= Arrays.asList(subString.split(","));
        for(int i=0;i<mItems.size();i++){
            MenuItem item=drawerMenu.add(DRAWER_MENU_GROUP_ID, Menu.NONE, Menu.NONE,mItems.get(i));
            item.setIcon(R.drawable.reddit_logo);

        }

        mNavigationView.setItemIconTintList(null);



    }

    private void settingUpNavigationView() {

                    mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                            if(item.getGroupId()==R.id.fav_group){


                                initFavoriteLoader();
                                toolbar.setTitle("Favorites");
                                mDrawerLayout.closeDrawers();
                                return true;
                            }

                            if(item.getGroupId()==R.id.subscribed_group){


                                mDrawerLayout.closeDrawers();

                                return true;
                            }
                            else
                                makingCustomUrl(item.toString());


                            mDrawerLayout.closeDrawers();
                            return true;
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






                   public  void makingCustomUrl(String subReddit) {


                          this.subReddit=subReddit;
                          counter = 0;
                          toolbar.setTitle(subReddit);

                          String subRedditSortBy = "";
                          if(!TextUtils.isEmpty(sortBy)){

                              subRedditSortBy="/"+sortBy;


                          }

                       if(mSearchView!=null) {
                           mSearchView.setQuery("", false);
                           mSearchView.setIconified(true);
                       }

                       if(subReddit.equals(getResources().getString(R.string.HomePage))){
                              subReddit=AppConstants.reddit_base_url+AppConstants.json_end;
                              sortView(false,getString(R.string.sort));




                       }else {

                           sortView(true,getString(R.string.sort));

                           subReddit = AppConstants.reddit_base_url + AppConstants.subreddit_url +
                                   subReddit +
                                   subRedditSortBy +
                                   AppConstants.json_end;
                       }

                          updateList(subReddit);




                   }



                 private  void makingSearchQueryUrl(String subReddit,String query){

                     this.subReddit=subReddit;
                     toolbar.setTitle(subReddit);







                 }





                public void updateList( String url)
                {


                    Log.d(LOG_TAG, url);

                    adapter=new RedditListViewAdapter(this,mreddit_item_list);

                    mRecyclerView.setAdapter(adapter);
                    adapter.SetOnItemClickListener(this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        return new FavoriteCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {



                 gettingDataFromCursor(data);

                  adapter.SetOnItemClickListener(this);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



    private  void gettingDataFromCursor(Cursor cursor) {
        mreddit_item_list.clear();
        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {


                RedditData item = new RedditData();
                item.setId(cursor.getString(COLUMN_POST_ID));
                item.setAuthor(cursor.getString(COLUMN_AUTHOR));
                item.setThumbnail(cursor.getString(COLUMN_THUMBNAIL));
                item.setTitle(cursor.getString(COLUMN_TITLE));
                item.setPermalink(cursor.getString(COLUMN_PERMALINK));
                item.setUrl(cursor.getString(COLUMN_URL));
                item.setImageUrl(cursor.getString(COLUMN_IMAGE_URL));
                item.setScore(cursor.getInt(COLUMN_POINTS));
                item.setNumComments(cursor.getInt(COLUMN_COMMENTS));
                item.setPostedOn(cursor.getLong(COLUMN_POSTED_ON));
                item.setOver18(false);
                item.setSubreddit(cursor.getString(COLUMN_SUBREDDIT));
                mreddit_item_list.add(item);


            }

            updateView(mreddit_item_list);

        }
     }


     private  void updateView( ArrayList<RedditData> reddit_item_list){
        adapter=new RedditListViewAdapter(this,reddit_item_list);
        mRecyclerView.setAdapter(adapter);




     }









    private  void initFavoriteLoader(){


        getLoaderManager().initLoader(0,null,this);

    }


    private void setMenuSubreddits(){




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu,menu);
        this.sortMenu=menu;
        mSearchView=(SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menuSearch));
        settingSearchView(mSearchView);
        return super.onCreateOptionsMenu(menu);

    }

    private void settingSearchView(final SearchView mSearchView) {


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                mSearchView.clearFocus();

               settingSearchQuery(subReddit,query);

               return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });




    }

    private void settingSearchQuery(String subreddit, String searchQuery){

        this.subReddit=subreddit;
        toolbar.setTitle(subreddit);
        String searchQuerySetup=AppConstants.search_json+"?q="+searchQuery;
        if(subReddit.equals(getResources().getString(R.string.HomePage))){
            subreddit = AppConstants.reddit_base_url + searchQuerySetup;
            sortView(true,getString(R.string.sort));
            sortView(false,getString(R.string.sort));

        }else {

            sortView(true,getString(R.string.sort));

            subreddit =  AppConstants.reddit_base_url + AppConstants.subreddit_url + subreddit + "/"+searchQuerySetup;
        }

        updateList( subreddit);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (subReddit.equals(getResources().getString(R.string.HomePage))
                || subReddit.equals(getResources().getString(R.string.title_favourites))) {
            Toast.makeText(this, "Sorting cannot be applied to Home and Favourites.", Toast.LENGTH_LONG).show();
            return true;
        }

        switch (item.getItemId()){

            case R.id.menuSortHot:
                sortBy="hot";
                break;
            case R.id.menuSortNew:
                sortBy = "new";
                break;
            case R.id.menuSortControversial:
                sortBy = "controversial";
                break;
            case R.id.menuSortTop:
                sortBy = "top";
                break;

            default:
                return super.onOptionsItemSelected(item);





        }
      makingCustomUrl(this.subReddit);
        return  true;

    }


    public void sortView(boolean showMenu, String title){
        if (sortMenu == null)
            return;


        for(int i=0;i<sortMenu.size();i++){
            if(title.equals(sortMenu.getItem(i).getTitle())){
                sortMenu.getItem(i).setVisible(showMenu);


            }


        }




    }


}


