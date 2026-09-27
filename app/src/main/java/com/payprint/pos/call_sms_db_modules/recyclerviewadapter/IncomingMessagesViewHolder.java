package com.payprint.pos.call_sms_db_modules.recyclerviewadapter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.payprint.pos.R;

public class IncomingMessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView incomingMessageSenderAddress, incomingMessageBody, incomingMessageSenderSendTime;
    public Button btnPrintReceipt;
    ItemClickListener itemClickListener;

    public IncomingMessagesViewHolder(@NonNull View itemView) {
        super(itemView);

        incomingMessageSenderAddress = itemView.findViewById(R.id.messageAddress);
        incomingMessageBody = itemView.findViewById(R.id.messageContent);
        incomingMessageSenderSendTime = itemView.findViewById(R.id.messageDateTime);
        btnPrintReceipt = itemView.findViewById(R.id.btn_print_sms);
    }

    @Override
    public void onClick(View view) {
        if (this.itemClickListener != null) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }
    }

    public void setItemClickListener(ItemClickListener ic) {
        this.itemClickListener = ic;
    }
}
