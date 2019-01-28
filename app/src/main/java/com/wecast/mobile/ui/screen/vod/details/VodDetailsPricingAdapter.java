package com.wecast.mobile.ui.screen.vod.details;

import androidx.databinding.ViewDataBinding;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;

import com.wecast.core.data.db.entities.VodSourceProfilePricing;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardRadioButtonBinding;
import com.wecast.mobile.ui.common.adapter.SingleItemChoiceAdapter;
import com.wecast.mobile.ui.common.viewHolder.SingleItemChoiceViewHolder;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class VodDetailsPricingAdapter extends SingleItemChoiceAdapter<VodSourceProfilePricing, VodDetailsPricingAdapter.VodPricingViewHolder> {

    VodDetailsPricingAdapter(PreferenceManager preferenceManager, List<VodSourceProfilePricing> list, SingleItemChoiceAdapter.OnCheckListener<VodSourceProfilePricing> listener) {
        super(preferenceManager, list, listener);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.card_radio_button;
    }

    @Override
    protected VodPricingViewHolder getViewHolder(ViewDataBinding dataBinding) {
        return new VodPricingViewHolder(dataBinding);
    }

    @Override
    protected boolean findLastCheckedPosition(PreferenceManager preferenceManager, VodSourceProfilePricing item) {
        return false;
    }

    /**
     * VIEW HOLDER
     */

    public class VodPricingViewHolder extends SingleItemChoiceViewHolder<VodSourceProfilePricing> {

        private final CardRadioButtonBinding binding;

        VodPricingViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = (CardRadioButtonBinding) binding;
        }

        @Override
        public void bind(VodSourceProfilePricing item) {
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
