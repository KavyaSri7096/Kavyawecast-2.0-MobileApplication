package com.wecast.mobile.ui.widget.banner;

import com.wecast.core.data.db.entities.Banner;

/**
 * Created by ageech@live.com
 */

public interface BannerOnClickListener {

    void onClick(Banner banner,  boolean clicked, boolean closed);
}
