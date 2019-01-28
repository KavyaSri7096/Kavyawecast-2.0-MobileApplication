package com.wecast.mobile.ui.widget.wecast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.mobile.R;
import com.wecast.mobile.WeApp;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class WeCastRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    @Inject
    ChannelDao channelDao;

    private Context context;
    private List<Channel> channels = new ArrayList<>();

    WeCastRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        ((WeApp) context.getApplicationContext()).getAppComponent().inject(this);
    }

    @Override
    public void onDataSetChanged() {
        channels = channelDao.getFavoritesAsList();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Channel item = channels.get(position);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_wecast_app_item);
        // Set channel title
        views.setTextViewText(R.id.title, item.getTitle());
        try {
            // Set channel logo
            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(item.getLogoUrl())
                    .submit(512, 512)
                    .get();
            views.setImageViewBitmap(R.id.logo, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Set channel categories
        if (item.getCategories() != null && item.getCategories().size() > 0) {
            StringBuilder categories = new StringBuilder();
            for (int i = 0; i < item.getCategories().size(); i++) {
                categories.append(item.getCategories().get(i).getName());
                if (i < item.getCategories().size() - 1) {
                    categories.append(", ");
                }
            }
            views.setTextViewText(R.id.categories, categories.toString());
        }
        // Set OnClick listener
        Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.root, fillInIntent);
        return views;
    }

    @Override
    public int getCount() {
        return channels.size();
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
        // Do nothing.
    }
}
