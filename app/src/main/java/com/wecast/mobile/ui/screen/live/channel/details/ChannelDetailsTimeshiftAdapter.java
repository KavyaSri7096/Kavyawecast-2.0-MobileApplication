package com.wecast.mobile.ui.screen.live.channel.details;

import android.view.View;
import android.widget.RadioButton;

import com.wecast.core.data.db.entities.ChannelTimeShiftStream;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardTrackBinding;
import com.wecast.mobile.ui.common.adapter.SingleChoiceAdapter;
import com.wecast.mobile.ui.common.adapter.viewHolder.SingleChoiceViewHolder;
import com.wecast.player.data.model.WePlayerTrack;
import com.wecast.player.data.player.exo.trackSelector.ExoPlayerTrackSelector;

import java.util.List;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class ChannelDetailsTimeshiftAdapter extends SingleChoiceAdapter<ChannelTimeShiftStream, ChannelDetailsTimeshiftAdapter.TrackViewHolder> {

    ChannelDetailsTimeshiftAdapter(PreferenceManager preferenceManager, List<ChannelTimeShiftStream> arrayList, OnCheckListener<ChannelTimeShiftStream> onCheckListener) {
        super(preferenceManager, arrayList, onCheckListener);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.card_track;
    }

    @Override
    protected TrackViewHolder getViewHolder(ViewDataBinding dataBinding) {
        return new TrackViewHolder(dataBinding);
    }

    @Override
    protected boolean findLastCheckedPosition(PreferenceManager preferenceManager, ChannelTimeShiftStream item) {
        return false;
    }

    /**
     * VIEW HOLDER
     */

    public class TrackViewHolder extends SingleChoiceViewHolder<ChannelTimeShiftStream> {

        private final CardTrackBinding binding;

        TrackViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = (CardTrackBinding) binding;
        }

        @Override
        public void bind(ChannelTimeShiftStream item) {
            binding.name.setText(item.getTitle());
        }

        @Override
        public void onItemChecked() {
            binding.button.setChecked(true);
        }

        @Override
        public void onItemUnChecked() {
            binding.button.setChecked(false);
        }

        @Override
        public void onItemUnChecked(View view) {
            if (view != null) {
                RadioButton rb = view.findViewById(R.id.button);
                rb.setChecked(false);
            }
        }
    }
}
