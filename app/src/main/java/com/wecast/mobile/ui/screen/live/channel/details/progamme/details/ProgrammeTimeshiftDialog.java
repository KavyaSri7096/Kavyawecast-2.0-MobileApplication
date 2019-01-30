package com.wecast.mobile.ui.screen.live.channel.details.progamme.details;

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
import com.wecast.mobile.databinding.DialogTimeshiftBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.adapter.SingleChoiceAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by ageech@live.com
 */

public class ProgrammeTimeshiftDialog extends BaseDialog implements SingleChoiceAdapter.OnCheckListener<ChannelTimeShiftStream> {

    public static final String TAG = ProgrammeTimeshiftDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    ChannelDao channelDao;

    private DialogTimeshiftBinding binding;
    private ProgrammeTimeshiftOnSelectListener timeshiftSelectListener;
    private Channel channel;
    private ChannelTimeShiftStream timeShiftStream;

    public static ProgrammeTimeshiftDialog newInstance(Channel channel) {
        ProgrammeTimeshiftDialog dialog = new ProgrammeTimeshiftDialog();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_timeshift, container, false);
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

        // Add manual shift stream for Live content
        ChannelTimeShiftStream timeShiftStream = new ChannelTimeShiftStream();
        timeShiftStream.setTitle(getString(R.string.live_tv));
        timeShiftStream.setStreamUrl(channel.getPrimaryUrl());
        shiftStreams.add(0, timeShiftStream);

        binding.timeShift.setLayoutManager(new LinearLayoutManager(getActivity()));
        ProgrammeTimeshiftAdapter adapter = new ProgrammeTimeshiftAdapter(preferenceManager, shiftStreams, this);
        binding.timeShift.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> playShifted());
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    private void playShifted() {
        // No selected profile
        if (timeShiftStream == null) {
            toast(R.string.message_timeshift_length);
            return;
        }

        if (timeshiftSelectListener != null) {
            timeshiftSelectListener.onSelect(timeShiftStream);
            dismiss();
        }
    }

    @Override
    public void onItemChecked(ChannelTimeShiftStream item) {
        this.timeShiftStream = item;
    }

    void setTimeshiftSelectListener(ProgrammeTimeshiftOnSelectListener timeshiftSelectListener) {
        this.timeshiftSelectListener = timeshiftSelectListener;
    }
}
