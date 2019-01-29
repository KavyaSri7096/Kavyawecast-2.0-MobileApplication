package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.utils.TVGuideUtils;
import com.wecast.mobile.databinding.CardProgrammeBinding;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.common.adapter.ItemMultiChoiceAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ageech@live.com
 */

public class ProgrammeAdapter extends RecyclerView.Adapter<BaseViewHolder<TVGuideProgramme>> {

    private Context context;
    private List<TVGuideProgramme> items;
    private ProgrammeViewModel.OnClickListener onClickListener;

    ProgrammeAdapter(Context context, ProgrammeViewModel.OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.items = new ArrayList<>();
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

    public void clearItems() {
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
