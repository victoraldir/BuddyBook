package com.quartzodev.widgets;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.quartzodev.buddybook.DetailActivity;
import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;

/**
 * Created by victoraldir on 21/05/2017.
 */

public class BuddyBookWidgetProvider extends AppWidgetProvider {

    private String mUserId;

    @SuppressLint("PrivateResource")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.buddy_widget_list);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, remoteViews);
            } else {
                setRemoteAdapterV11(context, remoteViews);
            }

            Intent clickIntentTemplate = new Intent(context, DetailActivity.class);

            PendingIntent pendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.widget_list, pendingIntentTemplate);
            remoteViews.setEmptyView(R.id.widget_list, R.id.widget_empty);
            remoteViews.setContentDescription(R.id.widget_list, context.getString(R.string.widget_cd));
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_list);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        mUserId = intent.getStringExtra(MainActivity.EXTRA_USER_ID);

        super.onReceive(context, intent);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {

        views.setRemoteAdapter(R.id.widget_list, createRemoteAdapterIntent(context));
    }


    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {

        views.setRemoteAdapter(0, R.id.widget_list, createRemoteAdapterIntent(context));
    }

    private Intent createRemoteAdapterIntent(Context context) {
        Intent it = new Intent(context, BuddyBookWidgetService.class);
        it.putExtra(MainActivity.EXTRA_USER_ID, mUserId);
        return it;
    }

}
