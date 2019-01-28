package com.wecast.mobile.ui.common.dialog;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.utils.ViewUtils;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogPurchasePinBinding;
import com.wecast.mobile.ui.base.BaseDialog;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class PurchasePinDialog extends BaseDialog {

    public static final String TAG = PurchasePinDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;

    private DialogPurchasePinBinding binding;
    private OnPinInputListener onPinInputListener;

    public static PurchasePinDialog newInstance() {
        return new PurchasePinDialog();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_purchase_pin, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> {
            String enteredPIN = binding.pin.getText().toString().trim();
            String currentPIN = preferenceManager.getPurchasePIN();
            if (enteredPIN.equals(currentPIN)) {
                if (onPinInputListener != null) {
                    onPinInputListener.onEntered();
                    dismiss();
                }
            } else {
                toast(R.string.message_wrong_pin);
            }
        });
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    public void setOnPinInputListener(OnPinInputListener pinListener) {
        this.onPinInputListener = pinListener;
    }

    public interface OnPinInputListener {
        void onEntered();
    }
}
