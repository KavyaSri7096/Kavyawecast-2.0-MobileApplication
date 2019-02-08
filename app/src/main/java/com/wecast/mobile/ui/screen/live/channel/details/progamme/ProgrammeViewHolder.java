package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import android.content.Context;
import android.view.View;

import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.utils.ReminderUtils;
import com.wecast.core.utils.TVGuideUtils;
import com.wecast.mobile.databinding.CardProgrammeBinding;
import com.wecast.mobile.ui.base.BaseOnClickListener;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class ProgrammeViewHolder extends BaseViewHolder<TVGuideProgramme> {

    private CardProgrammeBinding binding;
    private ReminderUtils reminderUtils;
    private ProgrammeViewModel viewModel;

    public ProgrammeViewHolder(ViewDataBinding binding, ReminderUtils reminderUtils) {
        super(binding.getRoot());
        this.binding = (CardProgrammeBinding) binding;
        this.reminderUtils = reminderUtils;
    }

    @Override
    public void onBind(Context context, BaseOnClickListener onClickListener, TVGuideProgramme item) {
        attachOnClickListener((ListRowOnClickListener) onClickListener, item);

        viewModel = new ProgrammeViewModel(item);
        binding.setViewModel(viewModel);

        // Set catchup indicator
        long now = System.currentTimeMillis();
        binding.catchup.setVisibility(item.getStartDate().getTime() < now ? View.VISIBLE : View.GONE);

        // Set reminder indicator
        boolean hasReminder = reminderUtils.isEventInCalendar(item.getStart());
        binding.reminder.setVisibility(hasReminder ? View.VISIBLE : View.GONE);

        // Set programme current progress
        if (item.isCurrent()) {
            binding.progress.setMax(TVGuideUtils.getMax(item));
            binding.progress.setProgress(TVGuideUtils.getProgress(item));
            binding.progress.setVisibility(View.VISIBLE);
        } else {
            binding.progress.setVisibility(View.GONE);
        }

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    private void attachOnClickListener(ListRowOnClickListener onClickListener, TVGuideProgramme item) {
        if (onClickListener != null) {
            itemView.setOnClickListener(view -> onClickListener.onClick(item, view));
        }
    }
}
