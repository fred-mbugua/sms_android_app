package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.C2BTransactionModel;
import com.example.dotpesa_new_dec_2022.utilities.KenyanTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dotpesa_new_dec_2022.R;

public class C2BTransactionAdapter extends RecyclerView.Adapter<C2BTransactionAdapter.ViewHolder> {

    private final Context context;
    private final List<C2BTransactionModel> transactionList;

    public C2BTransactionAdapter(Context context, List<C2BTransactionModel> transactionList) {
        this.context = context;
        this.transactionList = transactionList != null ? transactionList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_c2b_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        C2BTransactionModel tx = transactionList.get(position);

        holder.txTransId.setText(tx.transId != null && !tx.transId.isEmpty() ? tx.transId : "N/A");
        holder.txAmount.setText(String.format(Locale.getDefault(), "KES %.2f", tx.amount));

        String fullName = tx.getFullName();
        if (fullName != null && !fullName.trim().isEmpty()) {
            holder.txCustomerName.setVisibility(View.VISIBLE);
            holder.txCustomerName.setText(fullName.trim());
        } else {
            holder.txCustomerName.setVisibility(View.GONE);
        }

        if (tx.msisdn != null && !tx.msisdn.trim().isEmpty() && !tx.msisdn.equalsIgnoreCase(fullName)) {
            holder.txMsisdn.setVisibility(View.VISIBLE);
            holder.txMsisdn.setText("Phone: " + tx.msisdn.trim());
        } else {
            holder.txMsisdn.setVisibility(View.GONE);
        }

        if (tx.billRefNumber != null && !tx.billRefNumber.trim().isEmpty()) {
            holder.txBillRef.setVisibility(View.VISIBLE);
            holder.txBillRef.setText("Acc / Ref: " + tx.billRefNumber.trim());
        } else {
            holder.txBillRef.setVisibility(View.GONE);
        }

        if (tx.businessShortcode != null && !tx.businessShortcode.trim().isEmpty()) {
            holder.txShortcode.setVisibility(View.VISIBLE);
            holder.txShortcode.setText("Shortcode: " + tx.businessShortcode.trim());
        } else {
            holder.txShortcode.setVisibility(View.GONE);
        }

        if (tx.extraNote != null && !tx.extraNote.trim().isEmpty()) {
            holder.txExtraNote.setVisibility(View.VISIBLE);
            holder.txExtraNote.setText("Linked Note: " + tx.extraNote.trim());
        } else {
            holder.txExtraNote.setVisibility(View.GONE);
        }

        String rawDateStr = tx.transTimeFormatted != null && !tx.transTimeFormatted.isEmpty() ? tx.transTimeFormatted : (tx.transTimeEat != null ? tx.transTimeEat : tx.transTime);
        String kenyaDateStr = KenyanTimeFormatter.formatToKenyanTime(rawDateStr);
        if (kenyaDateStr != null && !kenyaDateStr.trim().isEmpty()) {
            holder.txDateFormatted.setVisibility(View.VISIBLE);
            holder.txDateFormatted.setText(kenyaDateStr.trim());
        } else {
            holder.txDateFormatted.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateData(List<C2BTransactionModel> newList) {
        if (newList != this.transactionList) {
            this.transactionList.clear();
            if (newList != null) {
                this.transactionList.addAll(newList);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txTransId, txAmount, txCustomerName, txMsisdn, txBillRef, txShortcode, txExtraNote, txDateFormatted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txTransId = itemView.findViewById(R.id.txTransId);
            txAmount = itemView.findViewById(R.id.txAmount);
            txCustomerName = itemView.findViewById(R.id.txCustomerName);
            txMsisdn = itemView.findViewById(R.id.txMsisdn);
            txBillRef = itemView.findViewById(R.id.txBillRef);
            txShortcode = itemView.findViewById(R.id.txShortcode);
            txExtraNote = itemView.findViewById(R.id.txExtraNote);
            txDateFormatted = itemView.findViewById(R.id.txDateFormatted);
        }
    }
}