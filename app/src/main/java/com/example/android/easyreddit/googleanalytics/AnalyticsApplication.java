package com.example.android.easyreddit.googleanalytics;

import android.app.Application;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.example.android.easyreddit.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsApplication extends Application {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    private RequestQueue mRequestQueue;
    private static AnalyticsApplication mInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        sAnalytics = GoogleAnalytics.getInstance(this);

        mInstance = (AnalyticsApplication) getApplicationContext();
    }


    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }


    private synchronized RequestQueue getmRequestQueue() {

        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(mInstance, new HurlStack());

        }
        return mRequestQueue;

    }

    public synchronized <T> void add_request_to_queue(Request<T> request) {

        request.setTag("data");
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getmRequestQueue().add(request);


    }


    public static AnalyticsApplication getmInstance() {


        return mInstance;

    }


}
