package com.wecast.mobile.ui.screen.settings.language;

import androidx.databinding.ViewDataBinding;
import android.view.View;
import android.widget.RadioButton;

import com.wecast.core.data.db.entities.Language;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardRadioButtonBinding;
import com.wecast.mobile.ui.common.adapter.ItemSingleChoiceAdapter;
import com.wecast.mobile.ui.common.adapter.viewHolder.ItemSingleChoiceViewHolder;
import com.wecast.mobile.utils.CommonUtils;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class LanguageAdapter extends ItemSingleChoiceAdapter<Language, LanguageAdapter.LanguageViewHolder> {

    LanguageAdapter(PreferenceManager preferenceManager, List<Language> list, ItemSingleChoiceAdapter.OnCheckListener<Language> listener) {
        super(preferenceManager, list, listener);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.card_radio_button;
    }

    @Override
    protected LanguageViewHolder getViewHolder(ViewDataBinding dataBinding) {
        return new LanguageViewHolder(dataBinding);
    }

    @Override
    protected boolean findLastCheckedPosition(PreferenceManager preferenceManager, Language item) {
        return item.getId() == preferenceManager.getLanguage().getId();
    }

    /**
     * VIEW HOLDER
     */

    public class LanguageViewHolder extends ItemSingleChoiceViewHolder<Language> {

        private final CardRadioButtonBinding binding;

        LanguageViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = (CardRadioButtonBinding) binding;
        }

        @Override
        public void bind(Language item) {
            binding.name.setText(CommonUtils.firstCapital(item.getName()));
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
