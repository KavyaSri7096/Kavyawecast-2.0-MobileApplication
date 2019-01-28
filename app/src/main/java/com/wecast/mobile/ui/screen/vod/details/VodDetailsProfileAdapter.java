package com.wecast.mobile.ui.screen.vod.details;

import androidx.databinding.ViewDataBinding;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;

import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardRadioButtonBinding;
import com.wecast.mobile.ui.common.adapter.SingleItemChoiceAdapter;
import com.wecast.mobile.ui.common.viewHolder.SingleItemChoiceViewHolder;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class VodDetailsProfileAdapter extends SingleItemChoiceAdapter<VodSourceProfile, VodDetailsProfileAdapter.VodDetailsProfileViewHolder> {

    VodDetailsProfileAdapter(PreferenceManager preferenceManager, List<VodSourceProfile> list, SingleItemChoiceAdapter.OnCheckListener<VodSourceProfile> listener) {
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
    protected boolean findLastCheckedPosition(PreferenceManager preferenceManager, VodSourceProfile item) {
        return false;
    }

    /**
     * VIEW HOLDER
     */

    public class VodDetailsProfileViewHolder extends SingleItemChoiceViewHolder<VodSourceProfile> {

        private final CardRadioButtonBinding binding;

        VodDetailsProfileViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = (CardRadioButtonBinding) binding;
        }

        @Override
        public void bind(VodSourceProfile item) {
            binding.name.setText(Html.fromHtml(item.getName()));
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
