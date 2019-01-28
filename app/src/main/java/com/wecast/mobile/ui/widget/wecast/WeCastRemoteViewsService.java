package com.wecast.mobile.ui.widget.wecast;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by ageech@live.com
 */

public class WeCastRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WeCastRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
