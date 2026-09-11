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

import java.util.ArrayList;

import dotpesa_new_dec_2022.R;

public class IncomingMessagesAdapter extends RecyclerView.Adapter<IncomingMessagesViewHolder> {

    Context context;
    ArrayList<SMSModelSMSdetails> messagesModel;

    public IncomingMessagesAdapter(Context context, ArrayList<SMSModelSMSdetails> messagesModel) {
        this.context = context;
        this.messagesModel = messagesModel;
    }

    //initialising viewholder
    @NonNull
    @Override
    public IncomingMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //View
        View messagesView = inflater.inflate(R.layout.each_unsynced_message, parent, false);

        //Holder
        IncomingMessagesViewHolder viewHolder = new IncomingMessagesViewHolder(messagesView);

        return viewHolder;
    }

    //Binding View Data

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull IncomingMessagesViewHolder holder, int position) {

        TextView messageSender = holder.incomingMessageSenderAddress;
        messageSender.setText(messagesModel.get(position).originationAddress);

        TextView messageBody = holder.incomingMessageBody;
        messageBody.setText(messagesModel.get(position).messageBody);

        TextView messageSenderSendTime = holder.incomingMessageSenderSendTime;
        messageSenderSendTime.setText(messagesModel.get(position).timeStamp);



        //Clicked:
//        holder.setItemClickListener(new ItemClickListener() {
//
//            @Override
//            public void onItemClick(View v, int pos) {
//                new AlertDialog.Builder(v.getContext())
//                        .setMessage("Send this message to POS?")
//                        .setCancelable(false)
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                //send this individual message to db
//
////                                String urlPage = MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_PAGE;
////                                HashMap<String, String> hashmap;
////                                SMSModelSMSdetails sms;
////
////                                MisooCenterRequestsTool.LocalMisooPushSingleSMSApplication(urlPage,  hashmap,  sms);
//
//                            }
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
////                Snackbar.make(v, messagesModel.get(pos).originationAddress +"\n"+  messagesModel.get(pos).messageBody + "\n" + messagesModel.get(pos).timeStamp,  Snackbar.LENGTH_LONG).show();
////                Snackbar snackbar =  Snackbar.make(v, messagesModel.get(pos).originationAddress +"\n"+  messagesModel.get(pos).messageBody + "\n" + messagesModel.get(pos).timeStamp,  Snackbar.LENGTH_LONG);
////                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout)snackbar.getView();
////                layout.setMinimumHeight(300);
////                snackbar.show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return messagesModel.size();
    }




}
