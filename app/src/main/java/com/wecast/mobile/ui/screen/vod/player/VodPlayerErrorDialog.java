package com.wecast.mobile.ui.screen.vod.player;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogPlayerErrorBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.core.utils.ViewUtils;

/**
 * Created by ageech@live.com
 */

public class VodPlayerErrorDialog extends BaseDialog {

    public static final String TAG = VodPlayerErrorDialog.class.getName();

    private DialogPlayerErrorBinding binding;
    private OnRetryListener onRetryListener;

    public static VodPlayerErrorDialog newInstance() {
        return new VodPlayerErrorDialog();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TrackDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_player_error, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initClickEvents();
    }

    private void initClickEvents() {
        binding.retry.setOnClickListener(v -> {
            if (onRetryListener != null) {
                onRetryListener.retry();
                dismiss();
            }
        });

        binding.cancel.setOnClickListener(v -> dismiss());
    }

    public void setOnRetryListener(OnRetryListener listener) {
        this.onRetryListener = listener;
    }

    public interface OnRetryListener {
        void retry();
    }
}