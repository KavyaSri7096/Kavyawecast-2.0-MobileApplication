package com.wecast.mobile.ui.widget.listRow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wecast.mobile.R;
import com.wecast.mobile.ui.base.BaseAdapter;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.screen.live.channel.ChannelFavoriteViewHolder;
import com.wecast.mobile.ui.screen.live.channel.ChannelViewHolder;
import com.wecast.mobile.ui.screen.live.guide.TVGuideViewHolder;
import com.wecast.mobile.ui.screen.settings.membership.MembershipPaymentViewHolder;
import com.wecast.mobile.ui.screen.show.TVShowViewHolder;
import com.wecast.mobile.ui.screen.show.details.TVShowEpisodeViewHolder;
import com.wecast.mobile.ui.screen.show.genre.TVShowGenreViewHolder;
import com.wecast.mobile.ui.screen.vod.VodContinueViewHolder;
import com.wecast.mobile.ui.screen.vod.VodViewHolder;
import com.wecast.mobile.ui.screen.vod.genre.VodGenreViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class ListRowAdapter extends BaseAdapter {

    private ListRowType listRowType;

    public ListRowAdapter(Context context, ListRowType listRowType) {
        super(context);
        this.listRowType = listRowType;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (listRowType) {
            case CHANNELS:
                return new ChannelViewHolder(getViewBinding(context, R.layout.card_channel, parent));
            case FAVORITE_CHANNELS:
                return new ChannelFavoriteViewHolder(getViewBinding(context, R.layout.card_channel_favorite, parent));
            case MOVIES:
                return new VodViewHolder(getViewBinding(context, R.layout.card_vod, parent));
            case MOVIE_GENRES:
                return new VodGenreViewHolder(getViewBinding(context, R.layout.card_vod_genre, parent));
            case CONTINUE_WATCHING:
                return new VodContinueViewHolder(getViewBinding(context, R.layout.card_vod_continue_watching, parent));
            case TV_SHOWS:
                return new TVShowViewHolder(getViewBinding(context, R.layout.card_tv_show, parent));
            case TV_SHOW_GENRES:
                return new TVShowGenreViewHolder(getViewBinding(context, R.layout.card_tv_show_genre, parent));
            case EPISODES:
                return new TVShowEpisodeViewHolder(getViewBinding(context, R.layout.card_episode, parent));
            case TV_GUIDE:
                return new TVGuideViewHolder(getViewBinding(context, R.layout.card_tv_guide, parent));
            case PAYMENT_HISTORY:
                return new MembershipPaymentViewHolder(getViewBinding(context, R.layout.card_payment, parent));
            default:
                throw new IllegalStateException("Unsupported ViewHolder type");
        }
    }

    @Override
    public int getItemCount() {
        return getItems() == null ? 0 : getItems().size();
    }

    private ViewDataBinding getViewBinding(Context context, int layoutRes, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return DataBindingUtil.inflate(inflater, layoutRes, parent, false);
    }
}
