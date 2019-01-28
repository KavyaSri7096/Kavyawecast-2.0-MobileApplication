package com.wecast.mobile.ui.screen.vod.details;

import android.content.Context;
import android.text.SpannableString;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodImage;
import com.wecast.core.data.db.entities.VodMember;
import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.mobile.ui.screen.vod.VodType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ageech@live.com
 */

class VodDetailsUtils {

    static List<VodImage> getImages(Vod vod) {
        List<VodImage> images = new ArrayList<>();
        if (vod.getBanners() != null && vod.getBanners().size() > 0) {
            images.addAll(vod.getBanners());
        }
        if (vod.getGallery() != null && vod.getGallery().size() > 0) {
            images.addAll(vod.getGallery());
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

    static ArrayList<VodSourceProfile> getSourceProfiles(Vod vod, boolean rented) {
        if (vod != null && vod.getMovieSource() != null && vod.getMovieSource().getProfiles() != null) {
            List<VodSourceProfile> profiles = vod.getMovieSource().getProfiles();
            return Stream.of(profiles)
                    .filter(profile -> rented ? isRented(profile) : isAvailableForRenting(profile))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return new ArrayList<>();
    }

    private static boolean isRented(VodSourceProfile profile) {
        return (profile.getBusinessModel().equals(VodType.RENTED) || profile.getBusinessModel().equals(VodType.S_VOD));
    }

    private static boolean isAvailableForRenting(VodSourceProfile profile) {
        return !(profile.getBusinessModel().equals(VodType.RENTED)) && !(profile.getBusinessModel().equals(VodType.S_VOD));
    }
}
