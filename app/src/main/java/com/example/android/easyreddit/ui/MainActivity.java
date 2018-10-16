package com.example.android.easyreddit.ui;

import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.adapters.RedditListViewAdapter;
import com.example.android.easyreddit.data.FavoriteCursorLoader;
import com.example.android.easyreddit.fragments.RedditDetailFragment;
import com.example.android.easyreddit.googleanalytics.AnalyticsApplication;
import com.example.android.easyreddit.model.CommentData;
import com.example.android.easyreddit.model.RedditData;
import com.example.android.easyreddit.utils.AppConstants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RedditListViewAdapter.OnItemClickListener,LoaderManager.LoaderCallbacks<Cursor>,RedditDetailActivity.CommentsProcessor {

    private static String LOG_TAG = MainActivity.class.getSimpleName();
    private  Tracker mTracker;



    private AnalyticsApplication global_data;


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

    private  Bundle  mSaveInstance;


    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;


    private ActionBarDrawerToggle mDrawerToggle;

    @BindView(R.id.reddititem_list)
    RecyclerView mRecyclerView;


    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;



    @BindView(R.id.no_internet_layout_id)
    View no_internet_layout_id;

    @BindView(R.id.main_content_layout)
    View main_content_layout;


    @BindView(R.id.refresh_main_content)
    SwipeRefreshLayout mrefresherLayout;



    @BindView(R.id.connection_retry)
    ImageButton mconnectionImageButton;


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

    private  ArrayList<CommentData> mcomments_data=new ArrayList<>();
    ;

    int position=0;




    @Override
    protected void onResume() {
        super.onResume();
        if(isNetworkAvailable())settingNavbarSubreddits();
       mTracker.setScreenName(getString(R.string.tracker_main_list_activiy));
       mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupWindowAnimations();

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        global_data=AnalyticsApplication.getmInstance();

        mTracker = application.getDefaultTracker();

        mSaveInstance=savedInstanceState;

        mrefresherLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_orange_dark));
        mrefresherLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                final  Bundle savedInstance=mSaveInstance;


                initView(savedInstance);

                Toast.makeText(MainActivity.this, getString(R.string.refresh_toast_message), Toast.LENGTH_SHORT).show();


            }
        });


        mconnectionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initView( mSaveInstance);

            }
        });



        if(findViewById(R.id.reddititem_detail_container)!=null)
            mTwoPane=true;
        else
            mTwoPane=false;


        initView( mSaveInstance);

    }

    private void initView(Bundle savedInstanceState) {
        if (isNetworkAvailable()) {

            no_internet_layout_id.setVisibility(View.GONE);
            main_content_layout.setVisibility(View.VISIBLE);
            settingUpToolBar();

            drawerMenu = mNavigationView.getMenu();

            mprefs = this.getSharedPreferences(getString(R.string.Subreddits_shared_preferences), Context.MODE_PRIVATE);


            settingUpDrawerLayout();
            settingNavbarSubreddits();

            settingUpNavigationView();








            if (savedInstanceState == null) {
                makingCustomUrl(getResources().getString(R.string.HomePage));

                getSupportActionBar().setTitle(R.string.HomePage);

            } else {


                mreddit_item_list = savedInstanceState.getParcelableArrayList(getString(R.string.listitems));
                adapter = new RedditListViewAdapter(this, mreddit_item_list);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setHasFixedSize(true);
                if(mrefresherLayout.isRefreshing())
                    mrefresherLayout.setRefreshing(false);
                adapter.SetOnItemClickListener(this);


            }





            sortView(false, getString(R.string.sort));


        }

        else{


          no_internet_layout_id.setVisibility(View.VISIBLE);
            main_content_layout.setVisibility(View.GONE);


        }



    }

    private void settingNavbarSubreddits() {
     if(mprefs.getBoolean(getString(R.string.first_run),true)){

         mprefs.edit().putString(getString(R.string.subreddits_key),getString(R.string.initial_subs)).commit();
         mprefs.edit().putBoolean(getString(R.string.first_run),false).commit();


     }

        String subString = mprefs.getString(getString(R.string.subreddits_key), "");

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


                       this.subReddit = subReddit;
                       counter = 0;
                       toolbar.setTitle(subReddit);

                       String subRedditSortBy = "";
                       if (!TextUtils.isEmpty(sortBy)) {

                           subRedditSortBy = "/" + sortBy;
                       }

                       if (mSearchView != null) {
                           mSearchView.setQuery("", false);
                           mSearchView.setIconified(true);
                       }

                       if (subReddit.equals(getResources().getString(R.string.HomePage))) {
                           subReddit = AppConstants.reddit_base_url + AppConstants.json_end;
                           sortView(false, getString(R.string.sort));


                       } else {

                           sortView(true, getString(R.string.sort));

                           subReddit = AppConstants.reddit_base_url + AppConstants.subreddit_url +
                                   subReddit +
                                   subRedditSortBy +
                                   AppConstants.json_end;
                       }

                       updateList(subReddit);


                   }









                public void updateList( String url)
                {


                    Log.d(LOG_TAG, url);



                    adapter=new RedditListViewAdapter(this,mreddit_item_list);
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    adapter.SetOnItemClickListener(this);

                    adapter.clearAdapter();
                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {


                            Log.d(LOG_TAG, response.toString());


                            try {

                                            JSONObject data = response.getJSONObject(getString(R.string.reddit_json_data_key));
                                            after_id = data.getString(getString(R.string.reddit_json_after_key));
                                            JSONArray children_array=data.getJSONArray(getString(R.string.reddit_json_children_key));
                                                        for(int i=0;i<children_array.length();i++)

                                                        {
                                                               JSONObject each_object=children_array.getJSONObject(i).getJSONObject(getString(R.string.reddit_json_data_key));

                                                               RedditData data_item=new RedditData();

                                                               data_item.setTitle(each_object.getString(getString(R.string.reddit_json_title_key)));
                                                               data_item.setAuthor(each_object.getString(getString(R.string.reddit_json_author_key)));
                                                               data_item.setNumComments(each_object.getInt(getString(R.string.reddit_json_num_comments_key)));
                                                               data_item.setScore(each_object.getInt(getString(R.string.reddit_json_score_key)));
                                                               data_item.setThumbnail(each_object.getString(getString(R.string.reddit_json_thumbnail_key)));
                                                               data_item.setOver18(each_object.getBoolean(getString(R.string.reddit_json_over_18_key)));
                                                               data_item.setUrl(each_object.getString(getString(R.string.reddit_json_url_key)));
                                                               data_item.setId(each_object.getString(getString(R.string.reddit_json_id_key)));
                                                               data_item.setSubreddit(each_object.getString(getString(R.string.reddit_json_subreddit_key)));
                                                               data_item.setPermalink(each_object.getString(getString(R.string.reddit_json_permalink_key)));
                                                               data_item.setPostedOn(each_object.getLong(getString(R.string.reddit_json_created_utc_key)));


                                                           try
                                                           {
                                                               data_item.setImageUrl(each_object.getJSONObject(getString(R.string.reddit_json_preview_key))
                                                                       .getJSONArray(getString(R.string.reddit_json_images_key))
                                                                       .getJSONObject(0)
                                                                       .getJSONArray(getString(R.string.reddit_json_resolutions_key))
                                                                       .getJSONObject(5).getString(getString(R.string.reddit_json_url_key)));

                                                           }

                                                           catch (JSONException e)
                                                           {
                                                               e.printStackTrace();

                                                           }


                                                            if ( mreddit_item_list == null) {
                                                                mreddit_item_list = new ArrayList<>();
                                                            }
                                                            mreddit_item_list.add(data_item);




                                                        }



                                                       }
                                                        catch (JSONException e)
                                                           {
                                                               e.printStackTrace();

                                                           }



                                                    adapter.notifyDataSetChanged();

                                        if(!mrefresherLayout.isRefreshing()) {

                                            if (mTwoPane) {
                                                String commentsUrl = AppConstants.reddit_base_url + mreddit_item_list.get(0).getPermalink() + AppConstants.jsonExt;
                                                fetchingCommentsFromUrl(commentsUrl, MainActivity.this);
                                            }

                                        }

                                                    if(mrefresherLayout.isRefreshing())
                                                        mrefresherLayout.setRefreshing(false);









                                  }



                                        },

                            new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            } });










                    global_data.add_request_to_queue(jsonObjectRequest);





                }


                @Override
                public void OnItemClick(View view, int position) {

                     RedditData data_item=mreddit_item_list.get(position);
                      if( mTwoPane) {
                          this.position=position;
                          String commentsUrl = AppConstants.reddit_base_url + mreddit_item_list.get(position).getPermalink() + AppConstants.jsonExt;
                          fetchingCommentsFromUrl(commentsUrl,this);
                      }


                    else {


                          Intent DetailActivityIntent = new Intent(getBaseContext(), RedditDetailActivity.class);

                          Bundle sending_arguments = new Bundle();

                          sending_arguments.putString(getString(R.string.reddit_data_title), data_item.getTitle());
                          sending_arguments.putString(getString(R.string.reddit_data_thumbnail), data_item.getThumbnail());
                          sending_arguments.putLong(getString(R.string.reddit_data_posted_on), data_item.getPostedOn());
                          sending_arguments.putInt(getString(R.string.reddit_data_num_comments), data_item.getNumComments());
                          sending_arguments.putString(getString(R.string.reddit_data_permalink), data_item.getPermalink());
                          sending_arguments.putString(getString(R.string.reddit_data_id), data_item.getId());
                          sending_arguments.putString(getString(R.string.reddit_data_author), data_item.getAuthor());
                          sending_arguments.putString(getString(R.string.reddit_data_subreddit), data_item.getSubreddit());
                          sending_arguments.putString(getString(R.string.reddit_data_image_url), data_item.getImageUrl());
                          sending_arguments.putString(getString(R.string.reddit_data_url), data_item.getUrl());
                          sending_arguments.putInt(getString(R.string.reddit_data_score), data_item.getScore());

                          DetailActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                          DetailActivityIntent.putExtras(sending_arguments);


                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                  startActivity(DetailActivityIntent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                              }
                          }
                          else {

                              startActivity(DetailActivityIntent);

                          }


                      }



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
        mRecyclerView.setHasFixedSize(true);




     }









    private  void initFavoriteLoader(){


        getLoaderManager().initLoader(0,null,this);

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
            Toast.makeText(this, getString(R.string.toast_sorting_message), Toast.LENGTH_LONG).show();
            return true;
        }

        switch (item.getItemId()){

            case R.id.menuSortHot:
                sortBy=getString(R.string.sort_category_hot);
                break;
            case R.id.menuSortNew:
                sortBy = getString(R.string.sort_category_new);
                break;
            case R.id.menuSortControversial:
                sortBy = getString(R.string.sort_category_controversial);
                break;
            case R.id.menuSortTop:
                sortBy = getString(R.string.sort_category_top);
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

    public void initFragment(RedditData redditData,ArrayList<CommentData> mcomments_data){

        Bundle arguments = setBundleArgumentsRedditData(redditData);
        arguments.putParcelableArrayList(getString(R.string.fragment_comments_data),mcomments_data);

      RedditDetailFragment fragment=new RedditDetailFragment();

        fragment.setArguments(arguments);

      getSupportFragmentManager().beginTransaction().replace(R.id.reddititem_detail_container,fragment).commit();

    }


    public Bundle setBundleArgumentsRedditData(RedditData  item){
        Bundle arguments = new Bundle();
        arguments.putString(getString(R.string.reddit_data_title), item.getTitle());
        arguments.putString(getString(R.string.reddit_data_subreddit), item.getSubreddit());
        arguments.putString(getString(R.string.reddit_data_image_url), item.getImageUrl());
        arguments.putString(getString(R.string.reddit_data_url), item.getUrl());
        arguments.putInt(getString(R.string.reddit_data_score), item.getScore());
        arguments.putString(getString(R.string.reddit_data_thumbnail), item.getThumbnail());
        arguments.putLong(getString(R.string.reddit_data_posted_on), item.getPostedOn());
        arguments.putInt(getString(R.string.reddit_data_num_comments), item.getNumComments());
        arguments.putString(getString(R.string.reddit_data_permalink), item.getPermalink());
        arguments.putString(getString(R.string.reddit_data_id), item.getId());
        arguments.putString(getString(R.string.reddit_data_author), item.getAuthor());

        return arguments;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(getString(R.string.listitems),mreddit_item_list);

    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onStart() {
        super.onStart();
        initView(  mSaveInstance);

    }

    private void setupWindowAnimations() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.LEFT);
            slide.setDuration(500);
            getWindow().setEnterTransition(slide);

            Fade fade=new Fade();
            fade.setDuration(500);
            getWindow().setExitTransition(fade);
        }
    }


    @Override
    public void onSuccessLoad(ArrayList<CommentData> mcomments_data) {


        initFragment(mreddit_item_list.get(position),mcomments_data);


    }


    public void fetchingCommentsFromUrl(String url, final RedditDetailActivity.CommentsProcessor processor){


//      requestQueue= Volley.newRequestQueue(this);
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


}


