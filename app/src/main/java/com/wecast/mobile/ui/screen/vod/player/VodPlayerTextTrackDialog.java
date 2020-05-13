package com.wecast.mobile.ui.screen.vod.player;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.utils.ViewUtils;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogTrackBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.adapter.SingleChoiceAdapter;
import com.wecast.player.data.model.WePlayerTrack;
import com.wecast.player.data.player.exo.trackSelector.ExoPlayerTrackSelector;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class VodPlayerTextTrackDialog extends BaseDialog implements SingleChoiceAdapter.OnCheckListener<WePlayerTrack> {

    public static final String TAG = VodPlayerTextTrackDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;

    private DialogTrackBinding binding;
    private ExoPlayerTrackSelector trackSelector;
    private VodPlayerOnTrackChangedListener trackSelectedListener;
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewUtils.dpToPx(400);
            params.height = ViewUtils.dpToPx(300);
            window.setAttributes(params);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TrackDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DialogTrackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (trackSelectedListener != null) {
            trackSelectedListener.onTrackDialogCreated(this);
        }

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        // Set track category title
        binding.title.setText(getResources().getString(R.string.subtitles));

        // Setup recycler view for data
        binding.data.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.data.setLayoutManager(layoutManager);
        ArrayList<WePlayerTrack> data = trackSelector != null ? trackSelector.getSubtitleTracks() : new ArrayList<>();
        VodPlayerTrackAdapter adapter = new VodPlayerTrackAdapter(preferenceManager, data, this);
        binding.data.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.close.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onItemChecked(WePlayerTrack item) {
        trackSelectedListener.onTrackChanged(item);
    }

    public void setTrackSelector(ExoPlayerTrackSelector trackSelector) {
        this.trackSelector = trackSelector;
    }

    public void setTrackSelectedListener(VodPlayerOnTrackChangedListener trackSelectedListener) {
        this.trackSelectedListener = trackSelectedListener;
    }
}
