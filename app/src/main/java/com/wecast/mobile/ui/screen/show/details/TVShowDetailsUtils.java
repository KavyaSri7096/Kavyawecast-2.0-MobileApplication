package com.wecast.mobile.ui.screen.show.details;

import android.content.Context;
import android.text.SpannableString;

import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.VodImage;
import com.wecast.core.data.db.entities.VodMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ageech@live.com
 */

class TVShowDetailsUtils {

    static List<VodImage> getImages(TVShow tvShow) {
        List<VodImage> images = new ArrayList<>();
        if (tvShow.getBanners() != null && tvShow.getBanners().size() > 0) {
            images.addAll(tvShow.getBanners());
        }
        if (tvShow.getGallery() != null && tvShow.getGallery().size() > 0) {
            images.addAll(tvShow.getGallery());
        }
        return images;
    }

    static SpannableString getMembers(Context context, List<VodMember> members, int job) {
        StringBuilder stringBuilder = new StringBuilder();
        int count = members.size() < 3 ? members.size() : 3;
        for (int i = 0; i < count; i++) {
            stringBuilder.append(members.get(i).getName().trim());
            if (i != count - 1) {
                stringBuilder.append(", ");
            }
        }
        String source = context.getResources().getString(job) + stringBuilder.toString();
        return new SpannableString(source);
    }
}
