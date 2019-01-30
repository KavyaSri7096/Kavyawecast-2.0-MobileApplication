package com.wecast.mobile.ui.screen.live.channel.details.progamme.details;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.ChannelTimeShiftStream;
import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.data.db.entities.TVGuideReminder;
import com.wecast.core.utils.ReminderUtils;
import com.wecast.core.utils.TVGuideUtils;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityProgrammeDetailsBinding;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.utils.BindingUtils;
import com.wecast.mobile.utils.CommonUtils;
import com.wecast.mobile.utils.PermissionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ProgrammeDetailsActivity extends BaseActivity<ActivityProgrammeDetailsBinding, ProgrammeDetailsActivityViewModel> implements ProgrammeDetailsActivityNavigator {

    @Inject
    PermissionUtils permissionUtils;
    @Inject
    ReminderUtils reminderUtils;
    @Inject
    ProgrammeDetailsActivityViewModel viewModel;

    private ActivityProgrammeDetailsBinding binding;
    private int channelId;
    private Channel channel;
    private TVGuideProgramme programme;

    public static void open(AppCompatActivity activity, Channel channel, TVGuideProgramme programme) {
        Intent intent = new Intent(activity, ProgrammeDetailsActivity.class);
        intent.putExtra("CHANNEL_ID", channel.getId());
        intent.putExtra("PROGRAMME_ID", programme.getId());
        activity.startActivityForResult(intent, 104);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_programme_details;
    }

    @Override
    public ProgrammeDetailsActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        setStatusTranslucent(false);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            channelId = bundle.getInt("CHANNEL_ID");
            String programmeId = bundle.getString("PROGRAMME_ID");
            programme = viewModel.getProgrammeByID(programmeId);
        }

        // Get channel details by id
        getById();
    }

    private void getById() {
        Disposable disposable = viewModel.getById(channelId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        channel = response;
                        showData();
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void setupListeners() {
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.controls.play.setOnClickListener(view -> playCatchup());
        binding.controls.reminder.setOnClickListener(view -> checkForPermission());
        binding.controls.reminder.setOnLongClickListener(v -> {
            updateReminder();
            return true;
        });
        binding.controls.addFavorite.setOnClickListener(view -> addToFavorites());
        binding.controls.removeFavorite.setOnClickListener(view -> removeFromFavorites());
        binding.controls.timeShift.setOnClickListener(v -> openTimeshiftDialog());
    }

    private void showData() {
        if (channel == null || programme == null) {
            return;
        }

        // Setup toolbar title
        if (CommonUtils.notNullOrEmpty(channel.getTitle())) {
            binding.toolbar.title.setText(channel.getTitle());
        }

        // Set channel logo
        if (CommonUtils.notNullOrEmpty(channel.getLogoUrl())) {
            BindingUtils.bindLogo(binding.logo, channel.getLogoUrl());
        } else {
            binding.logo.setVisibility(View.GONE);
        }

        // Set channel isFavorite
        binding.controls.setIsFavorite(channel.isFavorite());

        // Set programme title
        if (CommonUtils.notNullOrEmpty(programme.getTitle())) {
            binding.title.setText(programme.getTitle());
        }

        // Set programme start-end time
        binding.time.setText(TVGuideUtils.getStartEnd(programme));

        // Set programme description
        if (CommonUtils.notNullOrEmpty(programme.getDescription())) {
            binding.description.setText(programme.getDescription());
        } else {
            binding.descriptionRoot.setVisibility(View.GONE);
        }

        // Set progress bar for current programme
        if (programme.isCurrent()) {
            binding.progress.setMax(TVGuideUtils.getMax(programme));
            binding.progress.setProgress(TVGuideUtils.getProgress(programme));
            binding.progress.setVisibility(View.VISIBLE);
        } else {
            binding.progress.setVisibility(View.GONE);
        }

        // Set reminder add/remove icon
        long eventId = reminderUtils.getEventId(programme);
        if (eventId != -1) {
            Drawable icon = getDrawable(R.drawable.ic_reminder_on);
            binding.controls.reminder.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
        } else {
            Drawable icon = getDrawable(R.drawable.ic_reminder_off);
            binding.controls.reminder.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
        }

        // Show/hide reminder option
        long now = System.currentTimeMillis();
        if (programme.getStartDate().getTime() > now) {
            binding.controls.reminder.setAlpha(1);
            binding.controls.reminder.setEnabled(true);
        }

        // Show/hide catchup option
        if (channel.isCatchupEnabled()) {
            if (programme.isCurrent() || programme.getStopDate().getTime() < now) {
                binding.controls.play.setAlpha(1);
                binding.controls.play.setEnabled(true);
            }
        }

        // Show hide times shift option
        if (channel.isTimeShiftEnabled()) {
            binding.controls.timeShift.setAlpha(1);
            binding.controls.timeShift.setEnabled(true);
        }
    }

    private void playCatchup() {
        if (channel == null || channel.getCatchupUrlParams() == null) {
            return;
        }

        String url = channel.getCatchupUrl();
        long startTime;
        long duration;

        switch (channel.getCatchupUrlParams().getStartTimeFormat()) {
            case "timestamp":
                startTime = programme.getStart();
                break;
            default:
                startTime = 0;
                break;
        }

        switch (channel.getCatchupUrlParams().getDurationFormat()) {
            case "ms":
                duration = programme.getDurationMin() * 1000;
                break;
            case "s":
                duration = programme.getDurationMin() * 60;
                break;
            default:
                duration = 0;
                break;
        }

        long history = (System.currentTimeMillis() / 1000) - channel.getCatchupDurationTotal();
        if (programme.getStartDate().getTime() >= history) {
            url = url.replace("{start_time}", String.valueOf(startTime));
            url = url.replace("{duration}", String.valueOf(duration));
            Intent returnIntent = new Intent();
            returnIntent.putExtra("OVERRIDE_URL", url);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            toast(R.string.message_catchup_not_supported);
        }
    }

    private void openTimeshiftDialog() {
        ProgrammeTimeshiftDialog dialog = ProgrammeTimeshiftDialog.newInstance(channel);
        dialog.setTimeshiftSelectListener(this::playShifted);
        dialog.show(getSupportFragmentManager(), ProgrammeTimeshiftDialog.TAG);
    }

    private void playShifted(ChannelTimeShiftStream timeShiftStream) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("OVERRIDE_URL", timeShiftStream.getStreamUrl());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * ADD OR REMOVE REMINDER
     */

    private void checkForPermission() {
        String[] permissions = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
        permissionUtils.request(this, permissions, 1, new PermissionUtils.PermissionListener() {
            @Override
            public void onAllowed() {
                checkIfEventExist();
            }

            @Override
            public void onDeclined() {
                toast(R.string.message_calendar_permission_denied);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkIfEventExist() {
        long eventId = reminderUtils.getEventId(programme);
        if (eventId != -1) {
            removeReminder(eventId);
        } else {
            addReminder();
        }
    }

    private void addReminder() {
        Disposable disposable = viewModel.addReminder(channelId, channel.getEpgChannelId(), programme.getStringId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isTokenExpired()) {
                        refreshToken(this::addReminder);
                    } else if (response.isSuccessful()) {
                        viewModel.addReminderToDatabase(response.getData());
                        reminderUtils.createEvent(programme);
                        updateReminderIcon(true);
                    } else {
                        toast(response.getMessage());
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void removeReminder(long eventId) {
        // Get reminder from database
        TVGuideReminder reminder = viewModel.getReminderById(programme.getStringId());

        // Remove reminder remote and local
        Disposable disposable = viewModel.removeReminder(reminder.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isTokenExpired()) {
                        refreshToken(() -> removeReminder(eventId));
                    } else if (response.isSuccessful()) {
                        viewModel.removeReminderFromDatabase(reminder.getId());
                        reminderUtils.removeReminder(eventId, reminder);
                        updateReminderIcon(false);
                    } else {
                        toast(response.getMessage());
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void updateReminder() {
        long eventId = reminderUtils.getEventId(programme);
        if (eventId != -1) {
            RadialTimePickerDialogFragment dialog = new RadialTimePickerDialogFragment()
                    .setOnTimeSetListener((dialog1, hourOfDay, minute) -> {
                        int minutes = (hourOfDay * 60) + minute;
                        editReminder(minutes);
                    })
                    .setStartTime(0, 10)
                    .setDoneText("OK")
                    .setCancelText(getString(R.string.cancel))
                    .setThemeCustom(R.style.CustomRadialTimePickerDialog);
            dialog.show(getSupportFragmentManager(), dialog.getTag());
        } else {
            addReminder();
        }
    }

    private void editReminder(int minutes) {
        // Get reminder from database
        TVGuideReminder reminder = viewModel.getReminderById(programme.getStringId());
        String remindMe = String.valueOf(minutes);

        // Edit reminder remote and local
        Disposable disposable = viewModel.editReminder(reminder.getId(), remindMe)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isTokenExpired()) {
                        refreshToken(() -> editReminder(minutes));
                    } else if (response.isSuccessful()) {
                        viewModel.addReminderToDatabase(response.getData());
                        long eventId = reminderUtils.getEventId(programme);
                        reminderUtils.updateReminder(eventId, minutes);
                        toast(R.string.message_reminder_updated);
                    } else {
                        toast(response.getMessage());
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void updateReminderIcon(boolean isAdded) {
        Drawable icon = getDrawable(isAdded ? R.drawable.ic_reminder_on : R.drawable.ic_reminder_off);
        binding.controls.reminder.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
        toast(isAdded ? R.string.message_reminder_added : R.string.message_reminder_removed);
    }

    /**
     * ADD OR REMOVE FAVORITE
     */

    private void addToFavorites() {
        Disposable disposable = viewModel.addToFavorites(channelId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    toast(response.getMessage());
                    if (response.isTokenExpired()) {
                        refreshToken(this::addToFavorites);
                    } else if (response.isSuccessful()) {
                        binding.controls.setIsFavorite(true);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    toast(throwable.getMessage());
                });
        subscribe(disposable);
    }

    private void removeFromFavorites() {
        Disposable disposable = viewModel.removeFromFavorites(channelId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    toast(response.getMessage());
                    if (response.isTokenExpired()) {
                        refreshToken(this::removeFromFavorites);
                    } else if (response.isSuccessful()) {
                        binding.controls.setIsFavorite(false);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    toast(throwable.getMessage());
                });
        subscribe(disposable);
    }
}
