package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.SMSModelSMSdetails;
import com.example.dotpesa_new_dec_2022.utilities.KenyanTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import dotpesa_new_dec_2022.R;

public class IncomingMessagesAdapter extends RecyclerView.Adapter<IncomingMessagesViewHolder> {

    Context context;
    ArrayList<SMSModelSMSdetails> messagesModel;

    public IncomingMessagesAdapter(Context context, ArrayList<SMSModelSMSdetails> messagesModel) {
        this.context = context;
        this.messagesModel = messagesModel != null ? messagesModel : new ArrayList<>();
    }

    @NonNull
    @Override
    public IncomingMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View messagesView = inflater.inflate(R.layout.each_unsynced_message, parent, false);
        return new IncomingMessagesViewHolder(messagesView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull IncomingMessagesViewHolder holder, int position) {
        SMSModelSMSdetails msg = messagesModel.get(position);

        TextView messageSender = holder.incomingMessageSenderAddress;
        messageSender.setText(msg.originationAddress);

        TextView messageBody = holder.incomingMessageBody;
        messageBody.setText(msg.messageBody);

        TextView messageSenderSendTime = holder.incomingMessageSenderSendTime;
        messageSenderSendTime.setText(KenyanTimeFormatter.formatToKenyanTime(msg.timeStamp));
    }

    @Override
    public int getItemCount() {
        return messagesModel.size();
    }

    public void updateData(List<SMSModelSMSdetails> newList) {
        messagesModel.clear();
        if (newList != null) {
            messagesModel.addAll(newList);
        }
        notifyDataSetChanged();
    }
}