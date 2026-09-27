package com.payprint.pos.call_sms_db_modules.recyclerviewadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.payprint.pos.bluetooth.BluetoothPrinterManager;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.SMSModelSMSdetails;
import com.payprint.pos.utilities.KenyanTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import com.payprint.pos.R;

public class IncomingMessagesAdapter extends RecyclerView.Adapter<IncomingMessagesViewHolder> {

    private final Context context;
    private final ArrayList<SMSModelSMSdetails> messagesModel;

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

        if (holder.btnPrintReceipt != null) {
            holder.btnPrintReceipt.setOnClickListener(v -> {
                Toast.makeText(context, "Printing receipt...", Toast.LENGTH_SHORT).show();
                BluetoothPrinterManager.getInstance(context).printSmsReceipt(
                        msg.originationAddress,
                        msg.messageBody,
                        msg.timeStamp,
                        (success, message) -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                );
            });
        }
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
