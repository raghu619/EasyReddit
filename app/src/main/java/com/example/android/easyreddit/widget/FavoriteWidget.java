package com.example.android.easyreddit.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.easyreddit.R;
import com.example.android.easyreddit.ui.RedditDetailActivity;

public class FavoriteWidget extends AppWidgetProvider {

  private static String LOG_TAG=FavoriteWidget.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);



        if(intent.getAction().equals(context.getString(R.string.data_update_key))){

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);


        }
    }



   private void setRemoteAdapter(Context context,@NonNull final RemoteViews views){


       views.setRemoteAdapter(R.id.widget_list,
               new Intent(context, FavoriteWidgetService.class));
   }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_widget_layout);
            Intent intent = new Intent(context, RedditDetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setPendingIntentTemplate(R.id.widget_list, pendingIntent);

            setRemoteAdapter(context, views);

            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            appWidgetManager.updateAppWidget(appWidgetId, views);



        }

    }
}
