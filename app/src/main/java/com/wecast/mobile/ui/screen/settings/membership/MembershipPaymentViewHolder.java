package com.wecast.mobile.ui.screen.settings.membership;

import com.wecast.core.data.db.entities.PaymentHistory;
import com.wecast.mobile.databinding.CardPaymentBinding;
import com.wecast.mobile.ui.base.BaseViewHolder;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class MembershipPaymentViewHolder extends BaseViewHolder<PaymentHistory> {

    private CardPaymentBinding binding;

    public MembershipPaymentViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardPaymentBinding) binding;
    }

    @Override
    public void onBind(PaymentHistory item) {
        // Set date
        Date expirationDate;
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            expirationDate = parser.parse(item.getCreated());
            binding.date.setText(format.format(expirationDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Set price
        binding.price.setText(String.format(Locale.getDefault(), "%1$s %2$.2f", item.getCurrency(), item.getPrice()));

        // Set title
        binding.title.setText(item.getRecordTitle());
    }
}