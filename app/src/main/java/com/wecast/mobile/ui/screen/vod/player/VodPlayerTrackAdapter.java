package com.wecast.mobile.ui.screen.vod.player;

import androidx.databinding.ViewDataBinding;
import android.view.View;
import android.widget.RadioButton;

import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardRadioButtonTrackBinding;
import com.wecast.mobile.ui.common.adapter.SingleItemChoiceAdapter;
import com.wecast.mobile.ui.common.viewHolder.SingleItemChoiceViewHolder;
import com.wecast.player.data.model.WePlayerTrack;
import com.wecast.player.data.player.exo.trackSelector.ExoPlayerTrackSelector;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class VodPlayerTrackAdapter extends SingleItemChoiceAdapter<WePlayerTrack, VodPlayerTrackAdapter.TrackViewHolder> {

    VodPlayerTrackAdapter(PreferenceManager preferenceManager, List<WePlayerTrack> arrayList, SingleItemChoiceAdapter.OnCheckListener<WePlayerTrack> onCheckListener) {
        super(preferenceManager, arrayList, onCheckListener);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.card_radio_button_track;
    }

    @Override
    protected TrackViewHolder getViewHolder(ViewDataBinding dataBinding) {
        return new TrackViewHolder(dataBinding);
    }

    @Override
    protected boolean findLastCheckedPosition(PreferenceManager preferenceManager, WePlayerTrack item) {
        if (item != null && item.getName() != null) {
            switch (item.getTrackType()) {
                case ExoPlayerTrackSelector.TRACK_TYPE_VIDEO:
                    return item.getName().equals(preferenceManager.getLastVideoTrack());
                case ExoPlayerTrackSelector.TRACK_TYPE_AUDIO:
                    return item.getName().equals(preferenceManager.getLastAudioTrack());
                case ExoPlayerTrackSelector.TRACK_TYPE_TEXT:
                    return item.getName().equals(preferenceManager.getLastTextTrack());
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    /**
     * VIEW HOLDER
     */

    public class TrackViewHolder extends SingleItemChoiceViewHolder<WePlayerTrack> {

        private final CardRadioButtonTrackBinding binding;

        TrackViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = (CardRadioButtonTrackBinding) binding;
        }

        @Override
        public void bind(WePlayerTrack item) {
            binding.name.setText(item.getName());
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
