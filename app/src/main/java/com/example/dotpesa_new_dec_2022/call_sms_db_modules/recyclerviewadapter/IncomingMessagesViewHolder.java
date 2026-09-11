package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dotpesa_new_dec_2022.R;

public class IncomingMessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView incomingMessageSenderAddress, incomingMessageBody, incomingMessageSenderSendTime;
    ItemClickListener itemClickListener;

    public IncomingMessagesViewHolder(@NonNull View itemView) {
        super(itemView);

        incomingMessageSenderAddress = (TextView) itemView.findViewById(R.id.messageAddress);
        incomingMessageBody = (TextView) itemView.findViewById(R.id.messageContent);
        incomingMessageSenderSendTime = (TextView) itemView.findViewById(R.id.messageDateTime);

//        itemView.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        this.itemClickListener.onItemClick(view, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic){
        this.itemClickListener = ic;
    }
}
