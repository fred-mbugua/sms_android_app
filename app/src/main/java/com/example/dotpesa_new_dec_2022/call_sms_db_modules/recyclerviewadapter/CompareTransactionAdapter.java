package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.C2BTransactionModel;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.CompareTransactionModel;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.NodeSmsModel;
import com.example.dotpesa_new_dec_2022.utilities.KenyanTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dotpesa_new_dec_2022.R;

public class CompareTransactionAdapter extends RecyclerView.Adapter<CompareTransactionAdapter.ViewHolder> {

    private final Context context;
    private final List<CompareTransactionModel> compareList;

    public CompareTransactionAdapter(Context context, List<CompareTransactionModel> compareList) {
        this.context = context;
        this.compareList = compareList != null ? compareList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_compare_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompareTransactionModel item = compareList.get(position);

        holder.transCodeTxt.setText(item.transCode != null && !item.transCode.isEmpty() ? item.transCode : "N/A");

        switch (item.status) {
            case MATCHED:
                holder.badgeTxt.setText("MATCHED");
                holder.badgeTxt.setTextColor(ContextCompat.getColor(context, R.color.misooGreen));
                break;
            case AMOUNT_MISMATCH:
                holder.badgeTxt.setText("MISMATCH");
                holder.badgeTxt.setTextColor(Color.parseColor("#FF9800"));
                break;
            case C2B_ONLY:
                holder.badgeTxt.setText("CLOUD C2B ONLY");
                holder.badgeTxt.setTextColor(Color.parseColor("#00BCD4"));
                break;
            case SMS_ONLY:
                holder.badgeTxt.setText("NODE SMS ONLY");
                holder.badgeTxt.setTextColor(Color.parseColor("#FF5722"));
                break;
        }

        // C2B Record Column
        if (item.c2bRecord != null) {
            C2BTransactionModel c2b = item.c2bRecord;
            holder.c2bAmountTxt.setText(String.format(Locale.getDefault(), "KES %.2f", c2b.amount));
            holder.c2bPhoneTxt.setText(c2b.msisdn != null && !c2b.msisdn.isEmpty() ? "Phone: " + c2b.msisdn : "Phone: N/A");
            holder.c2bNameTxt.setText(c2b.getFullName());
        } else {
            holder.c2bAmountTxt.setText("N/A");
            holder.c2bPhoneTxt.setText("No Cloud Record");
            holder.c2bNameTxt.setText("-");
        }

        // SMS Record Column
        if (item.smsRecord != null) {
            NodeSmsModel sms = item.smsRecord;
            double smsAmount = sms.getExtractedAmount();
            if (smsAmount > 0.0) {
                holder.smsAmountTxt.setText(String.format(Locale.getDefault(), "KES %.2f", smsAmount));
            } else {
                holder.smsAmountTxt.setText("Amount in Body");
            }
            holder.smsSenderTxt.setText(sms.sender != null && !sms.sender.isEmpty() ? "Sender: " + sms.sender : "Sender: N/A");
            String rawDateStr = sms.timestamp != null && !sms.timestamp.isEmpty() ? sms.timestamp : sms.createdAt;
            holder.smsTimeTxt.setText(KenyanTimeFormatter.formatToKenyanTime(rawDateStr));
        } else {
            holder.smsAmountTxt.setText("N/A");
            holder.smsSenderTxt.setText("No SMS Record");
            holder.smsTimeTxt.setText("-");
        }
    }

    @Override
    public int getItemCount() {
        return compareList.size();
    }

    public void updateData(List<CompareTransactionModel> newList) {
        if (newList != this.compareList) {
            this.compareList.clear();
            if (newList != null) {
                this.compareList.addAll(newList);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView transCodeTxt, badgeTxt;
        TextView c2bAmountTxt, c2bPhoneTxt, c2bNameTxt;
        TextView smsAmountTxt, smsSenderTxt, smsTimeTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transCodeTxt = itemView.findViewById(R.id.compareTransCode);
            badgeTxt = itemView.findViewById(R.id.compareMatchBadge);

            c2bAmountTxt = itemView.findViewById(R.id.compareC2bAmount);
            c2bPhoneTxt = itemView.findViewById(R.id.compareC2bPhone);
            c2bNameTxt = itemView.findViewById(R.id.compareC2bName);

            smsAmountTxt = itemView.findViewById(R.id.compareSmsAmount);
            smsSenderTxt = itemView.findViewById(R.id.compareSmsSender);
            smsTimeTxt = itemView.findViewById(R.id.compareSmsTime);
        }
    }
}
