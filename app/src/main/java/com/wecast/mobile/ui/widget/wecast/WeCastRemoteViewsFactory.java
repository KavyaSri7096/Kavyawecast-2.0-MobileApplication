package com.wecast.mobile.ui.widget.wecast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.dao.TVGuideDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.TVGuide;
import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.utils.TVGuideUtils;
import com.wecast.mobile.R;
import com.wecast.mobile.WeApp;
import com.wecast.mobile.ui.screen.live.channel.details.ChannelDetailsActivity;
import com.wecast.mobile.utils.BitmapUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class WeCastRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    @Inject
    ChannelDao channelDao;
    @Inject
    TVGuideDao tvGuideDao;

    private Context context;
    private Intent intent;
    private List<Channel> channels;

    WeCastRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        WeApp weApp = (WeApp) context.getApplicationContext();
        weApp.getAppComponent().inject(this);
    }

    @Override
    public void onDataSetChanged() {
        // Get favorite channels from database
        channels = channelDao.getFavoritesAsList();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (channels == null || channels.size() == 0) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.component_channel_empty);
            return views;
        }

        // Get channel from list
        Channel channel = channels.get(position);
        TVGuide tvGuide = tvGuideDao.getById(channel.getId());

        // Create list item layout
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_wecast_item);

        // Set channel title
        views.setTextViewText(R.id.title, channel.getTitle());

        // Set channel logo
        try {
            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(channel.getLogoUrl())
                    .submit(100, 100)
                    .get();
            views.setImageViewBitmap(R.id.logo, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tvGuide != null && tvGuide.getProgrammes() != null) {
            for (TVGuideProgramme programme : tvGuide.getProgrammes()) {
                if (programme.isCurrent()) {
                    // Set programme title
                    views.setTextViewText(R.id.programmeTitle, programme.getTitle());
                    // Set programme logo
                    views.setTextViewText(R.id.programmeTime, TVGuideUtils.getStartEnd(programme));
                    // Set programme progress
                    int max = TVGuideUtils.getMax(programme);
                    int progress = TVGuideUtils.getProgress(programme);
                    views.setProgressBar(R.id.programmeProgress, max, progress, false);
                }
            }
        }

        // On item click open channel details
        Intent intent = new Intent(context, ChannelDetailsActivity.class);
        intent.putExtra("ID", channel.getId());
        views.setOnClickFillInIntent(R.id.root, intent);

        // Return item layout
        return views;
    }

    @Override
    public int getCount() {
        return channels != null ? channels.size() : 0;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDestroy() {
        // Do nothing
    }
}
