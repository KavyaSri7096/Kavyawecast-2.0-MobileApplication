package com.wecast.mobile.ui.screen.live.channel.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.ChannelTimeShiftStream;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.utils.ViewUtils;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogTrackBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.adapter.SingleChoiceAdapter;
import com.wecast.mobile.ui.screen.live.channel.details.progamme.details.ProgrammeTimeshiftOnSelectListener;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by ageech@live.com
 */

public class ChannelDetailsTimeshiftDialog extends BaseDialog implements SingleChoiceAdapter.OnCheckListener<ChannelTimeShiftStream> {

    public static final String TAG = ChannelDetailsTimeshiftDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    ChannelDao channelDao;

    private DialogTrackBinding binding;
    private ProgrammeTimeshiftOnSelectListener timeshiftSelectListener;
    private Channel channel;

    public static ChannelDetailsTimeshiftDialog newInstance(Channel channel) {
        ChannelDetailsTimeshiftDialog dialog = new ChannelDetailsTimeshiftDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", channel.getId());
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewUtils.dpToPx(getResources().getBoolean(R.bool.isPhone) ? 300 : 600);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_track, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int id = bundle.getInt("ID");
            channel = channelDao.getById(id);
        }

        if (channel == null) {
            return;
        }

        // Check if channel has shifted streams
        List<ChannelTimeShiftStream> shiftStreams = channel.getTimeShiftedStreams();
        if (shiftStreams == null || shiftStreams.size() == 0) {
            return;
        }

        // Set title
        binding.title.setText(getResources().getString(R.string.timeshift));

        // Add manual shift stream for Live content
        ChannelTimeShiftStream timeShiftStream = new ChannelTimeShiftStream();
        timeShiftStream.setTitle(getString(R.string.live_tv));
        timeShiftStream.setStreamUrl(channel.getPrimaryUrl());
        shiftStreams.add(0, timeShiftStream);

        binding.data.setLayoutManager(new LinearLayoutManager(getActivity()));
        ChannelDetailsTimeshiftAdapter adapter = new ChannelDetailsTimeshiftAdapter(preferenceManager, shiftStreams, this);
        binding.data.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.close.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onItemChecked(ChannelTimeShiftStream item) {
        if (timeshiftSelectListener != null) {
            timeshiftSelectListener.onSelect(item);
            dismiss();
        }
    }

    void setTimeshiftSelectListener(ProgrammeTimeshiftOnSelectListener timeshiftSelectListener) {
        this.timeshiftSelectListener = timeshiftSelectListener;
    }
}
