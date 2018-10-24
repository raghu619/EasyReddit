package com.example.android.easyreddit.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.example.android.easyreddit.R;
import com.example.android.easyreddit.ui.RedditDetailActivity;

public class FavoriteWidget extends AppWidgetProvider {

    private static String LOG_TAG = FavoriteWidget.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_widget_layout);

        if (intent.hasExtra(context.getString(R.string.widget_id_key))) {

            int[] ids = intent.getExtras().getIntArray(context.getString(R.string.widget_id_key));
            if (intent.getAction().equals(context.getString(R.string.data_update_key))) {


                this.onUpdate(context, AppWidgetManager.getInstance(context), ids);

            }


        } else {


            super.onReceive(context, intent);
        }
        ComponentName component = new ComponentName(context, FavoriteWidget.class);
        AppWidgetManager.getInstance(context).updateAppWidget(component, views);

    }


    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {


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
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
            appWidgetManager.updateAppWidget(appWidgetId, views);



        }

    }
}
