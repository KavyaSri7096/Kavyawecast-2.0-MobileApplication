package com.wecast.mobile.ui.screen.settings.quality;

import androidx.databinding.ViewDataBinding;
import android.view.View;
import android.widget.RadioButton;

import com.wecast.core.data.db.entities.ChannelStreamingProfile;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardRadioButtonBinding;
import com.wecast.mobile.ui.common.adapter.SingleItemChoiceAdapter;
import com.wecast.mobile.ui.common.viewHolder.SingleItemChoiceViewHolder;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class VideoQualityAdapter extends SingleItemChoiceAdapter<ChannelStreamingProfile, VideoQualityAdapter.VideoQualityViewHolder> {

    public VideoQualityAdapter(PreferenceManager preferenceManager, List<ChannelStreamingProfile> list, SingleItemChoiceAdapter.OnCheckListener<ChannelStreamingProfile> listener) {
        super(preferenceManager, list, listener);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.card_radio_button;
    }

    @Override
    protected VideoQualityViewHolder getViewHolder(ViewDataBinding dataBinding) {
        return new VideoQualityViewHolder(dataBinding);
    }

    @Override
    protected boolean findLastCheckedPosition(PreferenceManager preferenceManager, ChannelStreamingProfile item) {
        return item.getId() == preferenceManager.getVideoQuality().getId();
    }

    /**
     * VIEW HOLDER
     */

    public class VideoQualityViewHolder extends SingleItemChoiceViewHolder<ChannelStreamingProfile> {

        private final CardRadioButtonBinding binding;

        VideoQualityViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = (CardRadioButtonBinding) binding;
        }

        @Override
        public void bind(ChannelStreamingProfile item) {
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
            RadioButton rb = view.findViewById(R.id.button);
            rb.setChecked(false);
        }
    }
}
