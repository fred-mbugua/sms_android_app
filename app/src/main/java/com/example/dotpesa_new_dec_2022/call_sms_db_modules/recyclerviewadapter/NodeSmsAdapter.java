package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.NodeSmsModel;
import com.example.dotpesa_new_dec_2022.utilities.KenyanTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import dotpesa_new_dec_2022.R;

public class NodeSmsAdapter extends RecyclerView.Adapter<NodeSmsAdapter.ViewHolder> {

    private final Context context;
    private final List<NodeSmsModel> smsList;

    public NodeSmsAdapter(Context context, List<NodeSmsModel> smsList) {
        this.context = context;
        this.smsList = smsList != null ? smsList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_node_sms_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NodeSmsModel sms = smsList.get(position);

        holder.senderTxt.setText(sms.sender != null && !sms.sender.isEmpty() ? "Sender: " + sms.sender : "Unknown Sender");

        String mpesaCode = sms.getMpesaTransId();
        if (!mpesaCode.isEmpty()) {
            holder.codeTxt.setVisibility(View.VISIBLE);
            holder.codeTxt.setText(mpesaCode);
        } else {
            holder.codeTxt.setVisibility(View.GONE);
        }

        holder.messageBodyTxt.setText(sms.message != null ? sms.message : "");
        holder.serialTxt.setText("Serial: " + sms.smsSerial);
        String rawDateStr = sms.timestamp != null && !sms.timestamp.isEmpty() ? sms.timestamp : sms.createdAt;
        holder.dateTxt.setText(KenyanTimeFormatter.formatToKenyanTime(rawDateStr));
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public void updateData(List<NodeSmsModel> newList) {
        if (newList != this.smsList) {
            this.smsList.clear();
            if (newList != null) {
                this.smsList.addAll(newList);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderTxt, codeTxt, messageBodyTxt, serialTxt, dateTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTxt = itemView.findViewById(R.id.nodeSmsSender);
            codeTxt = itemView.findViewById(R.id.nodeSmsTransCode);
            messageBodyTxt = itemView.findViewById(R.id.nodeSmsMessageBody);
            serialTxt = itemView.findViewById(R.id.nodeSmsSerial);
            dateTxt = itemView.findViewById(R.id.nodeSmsDate);
        }
    }
}
