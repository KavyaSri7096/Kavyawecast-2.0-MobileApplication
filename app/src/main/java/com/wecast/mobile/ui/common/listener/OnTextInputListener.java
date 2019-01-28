package com.wecast.mobile.ui.common.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import io.reactivex.ObservableOnSubscribe;

/**
 * Created by ageech@live.com
 */

public class OnTextInputListener {

    public static ObservableOnSubscribe<String> watch(EditText editText) {
        return emitter -> editText.addTextChangedListener(new OnTextInputWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emitter.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                emitter.onNext(s.toString());
            }
        });
    }

    private static class OnTextInputWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
