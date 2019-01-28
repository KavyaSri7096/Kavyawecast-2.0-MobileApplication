package com.wecast.mobile.ui.screen.show.genre;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wecast.core.data.db.entities.TVShowGenre;
import com.wecast.mobile.databinding.CardTvShowGenreBinding;
import com.wecast.mobile.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public class TVShowGenreAdapter extends RecyclerView.Adapter<BaseViewHolder<TVShowGenre>> {

    private Context context;
    private List<TVShowGenre> items;

    public TVShowGenreAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    @NonNull
    @Override
    public BaseViewHolder<TVShowGenre> onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        CardTvShowGenreBinding binding = CardTvShowGenreBinding.inflate(inflater, viewGroup, false);
        return new TVShowGenreViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<TVShowGenre> viewHolder, int position) {
        TVShowGenre item = items.get(position);
        viewHolder.onBind(item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void addItems(List<TVShowGenre> newList) {
        if (newList != null) {
            items.addAll(newList);
            notifyDataSetChanged();
        }
    }
}
