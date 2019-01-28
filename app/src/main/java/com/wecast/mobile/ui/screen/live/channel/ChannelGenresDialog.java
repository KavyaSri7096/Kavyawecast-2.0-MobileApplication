package com.wecast.mobile.ui.screen.live.channel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.ChannelGenre;
import com.wecast.core.data.repository.ChannelGenreRepository;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogChannelGenresBinding;
import com.wecast.mobile.ui.base.BaseDialog;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ChannelGenresDialog extends BaseDialog implements ChannelGenreSelectListener {

    public static final String TAG = ChannelGenresDialog.class.getName();

    @Inject
    ChannelGenreRepository channelGenreRepository;

    private DialogChannelGenresBinding binding;
    private ChannelGenreSelectListener genreSelectListener;
    private List<ChannelGenre> genreList;

    public static ChannelGenresDialog newInstance() {
        return new ChannelGenresDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_channel_genres, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        getGenres();
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> {
            int position = binding.genres.getValue();
            genreSelected(position);
        });
    }

    private void getGenres() {
        Disposable disposable = channelGenreRepository.getGenres(true)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            showData(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            toast(response.message);
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::getGenres);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            showData(response.data);
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void showData(List<ChannelGenre> data) {
        this.genreList = data;

        if (genreList == null || genreList.size() == 0) {
            return;
        }

        // Create category all channels
        ChannelGenre all = new ChannelGenre();
        all.setId(-1);
        all.setName(getResources().getString(R.string.all_channels));
        genreList.add(0, all);

        // Set genre picker
        binding.genres.setMinValue(0);
        binding.genres.setMaxValue(genreList.size() - 1);
        String[] genres = new String[genreList.size()];
        for (int i = 0; i < genreList.size(); i++) {
            genres[i] = genreList.get(i).getName();
        }
        binding.genres.setDisplayedValues(genres);
    }

    private void genreSelected(int position) {
        ChannelGenre channelGenre = genreList.get(position);

        if (channelGenre.getChildren() != null && channelGenre.getChildren().size() > 0) {
            ChannelGenresChildrenDialog dialog = ChannelGenresChildrenDialog.newInstance(channelGenre.getId());
            dialog.setGenreSelectListener(this);
            dialog.show(getChildFragmentManager());
        } else {
            genreSelectListener.onGenreSelected(channelGenre);
            dismiss();
        }
    }

    void setGenreSelectListener(ChannelGenreSelectListener genreSelectListener) {
        this.genreSelectListener = genreSelectListener;
    }

    @Override
    public void onGenreSelected(ChannelGenre channelGenre) {
        genreSelectListener.onGenreSelected(channelGenre);
        dismiss();
    }
}
