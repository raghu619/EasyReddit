package com.example.android.easyreddit.utils;

import android.app.Application;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class AppRequestGlobal  extends Application {





    private  static  AppRequestGlobal  mInstance;
    private RequestQueue mRequestQueue;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=(AppRequestGlobal)getApplicationContext();

    }

    private synchronized RequestQueue getmRequestQueue(){

        if(mRequestQueue==null){

            mRequestQueue = Volley.newRequestQueue( mInstance, new HurlStack());

        }
        return  mRequestQueue;

    }

    public synchronized <T>void  add_request_to_queue(Request<T> request){

        request.setTag("data");
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getmRequestQueue().add(request);


    }


    public static AppRequestGlobal getmInstance(){


        return mInstance;

    }

}
