package com.wecast.mobile.ui.screen.live.channel.details.progamme.details;

import android.view.View;
import android.widget.RadioButton;

import com.wecast.core.data.db.entities.ChannelTimeShiftStream;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardRadioButtonBinding;
import com.wecast.mobile.ui.common.adapter.SingleChoiceAdapter;
import com.wecast.mobile.ui.common.adapter.viewHolder.SingleChoiceViewHolder;

import java.util.List;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class ProgrammeTimeshiftAdapter extends SingleChoiceAdapter<ChannelTimeShiftStream, ProgrammeTimeshiftAdapter.VodDetailsProfileViewHolder> {

    ProgrammeTimeshiftAdapter(PreferenceManager preferenceManager, List<ChannelTimeShiftStream> list, OnCheckListener<ChannelTimeShiftStream> listener) {
        super(preferenceManager, list, listener);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.card_radio_button;
    }

    @Override
    protected VodDetailsProfileViewHolder getViewHolder(ViewDataBinding dataBinding) {
        return new VodDetailsProfileViewHolder(dataBinding);
    }

    @Override
    protected boolean findLastCheckedPosition(PreferenceManager preferenceManager, ChannelTimeShiftStream item) {
        return false;
    }

    /**
     * VIEW HOLDER
     */

    public class VodDetailsProfileViewHolder extends SingleChoiceViewHolder<ChannelTimeShiftStream> {

        private final CardRadioButtonBinding binding;

        VodDetailsProfileViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = (CardRadioButtonBinding) binding;
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
            RadioButton rb = view.findViewById(R.id.button);
            rb.setChecked(false);
        }
    }
}
