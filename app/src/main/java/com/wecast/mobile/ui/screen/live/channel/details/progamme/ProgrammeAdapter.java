package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.utils.ReminderUtils;
import com.wecast.core.utils.TVGuideUtils;
import com.wecast.mobile.databinding.CardProgrammeBinding;
import com.wecast.mobile.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public class ProgrammeAdapter extends RecyclerView.Adapter<BaseViewHolder<TVGuideProgramme>> {

    private List<TVGuideProgramme> items;
    private Context context;
    private ReminderUtils reminderUtils;
    private ProgrammeViewModel.OnClickListener onClickListener;

    ProgrammeAdapter(Context context, ReminderUtils reminderUtils, ProgrammeViewModel.OnClickListener onClickListener) {
        this.items = new ArrayList<>();
        this.context = context;
        this.reminderUtils = reminderUtils;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public BaseViewHolder<TVGuideProgramme> onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        CardProgrammeBinding binding = CardProgrammeBinding.inflate(inflater, viewGroup, false);
        return new ProgrammeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<TVGuideProgramme> viewHolder, int position) {
        TVGuideProgramme item = items.get(position);
        viewHolder.onBind(item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void addItems(List<TVGuideProgramme> newList) {
        if (newList != null) {
            items.addAll(newList);
            notifyDataSetChanged();
        }
    }

    void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public List<TVGuideProgramme> getItems() {
        return items;
    }

    TVGuideProgramme getItem(int position) {
        return items.get(position);
    }

    /**
     * VIEW HOLDER
     */

    public class ProgrammeViewHolder extends BaseViewHolder<TVGuideProgramme> {

        private CardProgrammeBinding binding;
        private ProgrammeViewModel viewModel;

        ProgrammeViewHolder(CardProgrammeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void onBind(TVGuideProgramme item) {
            viewModel = new ProgrammeViewModel(item, onClickListener);
            binding.setViewModel(viewModel);

            // Set catchup indicator
            long now = System.currentTimeMillis();
            binding.catchup.setVisibility(item.getStartDate().getTime() < now ? View.VISIBLE : View.GONE);

            // Set reminder indicator
            long eventId = reminderUtils.getEventId(item);
            binding.reminder.setVisibility(eventId != -1 ? View.VISIBLE : View.GONE);

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
    }
}
