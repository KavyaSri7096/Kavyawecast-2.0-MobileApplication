package com.wecast.mobile.ui.widget.wecast;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.wecast.mobile.R;
import com.wecast.mobile.ui.screen.live.channel.details.ChannelDetailsActivity;
import com.wecast.mobile.ui.screen.navigation.NavigationActivity;

/**
 * Created by ageech@live.com
 */

public class WeCastWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // Refresh all widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, WeCastWidget.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.data);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_wecast);

        // Set up the collection
        views.setRemoteAdapter(R.id.data, new Intent(context, WeCastRemoteViewsService.class));

        // The empty view is displayed when the collection has no items.
        // It should be a sibling of the collection view.
        views.setEmptyView(R.id.data, R.id.noData);

        // Handle on item click
        Intent intent = new Intent(context, ChannelDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setPendingIntentTemplate(R.id.data, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, WeCastWidget.class));
        context.sendBroadcast(intent);
    }
}

