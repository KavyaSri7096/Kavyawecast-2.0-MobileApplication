package com.wecast.mobile.ui.screen.live.channel.details;

import android.content.Context;
import androidx.databinding.ViewDataBinding;
import android.view.View;
import android.widget.RadioButton;

import com.wecast.core.data.db.entities.ChannelProfile;
import com.wecast.core.data.db.entities.Subscription;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardRadioButtonBinding;
import com.wecast.mobile.ui.common.adapter.SingleItemChoiceAdapter;
import com.wecast.mobile.ui.common.viewHolder.SingleItemChoiceViewHolder;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class ChannelDetailsProfileAdapter extends SingleItemChoiceAdapter<ChannelProfile, ChannelDetailsProfileAdapter.ChannelProfileViewHolder> {

    private PreferenceManager preferenceManager;

    ChannelDetailsProfileAdapter(PreferenceManager preferenceManager, List<ChannelProfile> list, SingleItemChoiceAdapter.OnCheckListener<ChannelProfile> listener) {
        super(preferenceManager, list, listener);
        this.preferenceManager = preferenceManager;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.card_radio_button;
    }

    @Override
    protected ChannelProfileViewHolder getViewHolder(ViewDataBinding dataBinding) {
        return new ChannelProfileViewHolder(dataBinding, preferenceManager);
    }

    @Override
    protected boolean findLastCheckedPosition(PreferenceManager preferenceManager, ChannelProfile item) {
        return false;
    }

    /**
     * VIEW HOLDER
     */

    public class ChannelProfileViewHolder extends SingleItemChoiceViewHolder<ChannelProfile> {

        private final CardRadioButtonBinding binding;
        private final PreferenceManager preferenceManager;

        ChannelProfileViewHolder(ViewDataBinding binding, PreferenceManager preferenceManager) {
            super(binding.getRoot());
            this.binding = (CardRadioButtonBinding) binding;
            this.preferenceManager = preferenceManager;
        }

        @Override
        public void bind(ChannelProfile item) {
            // Get user subscription
            Subscription subscription = preferenceManager.getAuthentication().getSubscription();
            // Get user currency code from subscription
            String currencyCode = subscription.getCurrencyCode();
            // Get context
            Context context = binding.getRoot().getContext();
            // Build profile name
            String name = String.format(context.getString(R.string.rent_for_with_days), item.getName(), item.getPrice() + " " + currencyCode, item.getDuration());
            binding.name.setText(name);
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
