package com.example.dotpesa_new_dec_2022;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.IncomingMessagesAdapter;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.Constants;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.SMSModelSMSdetails;
import com.example.dotpesa_new_dec_2022.utilities.InternetConnection;
import com.example.dotpesa_new_dec_2022.utilities.ProcessReceivedSMSMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import dotpesa_new_dec_2022.R;

public class UnsynchedInboxFragment extends Fragment {
    Context c;
    Constants dbConstants = new Constants();
    public static UnsynchedInboxFragment inst;

    public UnsynchedInboxFragment() {
    }

    public static UnsynchedInboxFragment instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    public ArrayList<SMSModelSMSdetails> smsModelSMSdetails = new ArrayList<>();
    public RecyclerView rvMessages;
    public IncomingMessagesAdapter adapter;
    public static View v;
    public static View listItemsView;
    public static View emptyInboxView;
    TextView emptyInboxPageText;
    ImageView emptyInboxPageImage;
    TextView titleView;
    TextView body;
    TextView dateTime;
    FloatingActionButton fab;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.unsynchronized_inbox_fragment, container, false);
        listItemsView = inflater.inflate(R.layout.each_unsynced_message, container, false);
        emptyInboxView = inflater.inflate(R.layout.empty_inbox_page, container, false);
        emptyInboxPageText = (TextView) v.findViewById(R.id.emptyInboxPageText);
        emptyInboxPageImage = (ImageView) v.findViewById(R.id.emptyInboxImage);



        titleView = (TextView) listItemsView.findViewById(R.id.messageAddress);
        body = (TextView) listItemsView.findViewById(R.id.messageContent);
        dateTime = (TextView) listItemsView.findViewById(R.id.messageDateTime);

        rvMessages = (RecyclerView) v.findViewById(R.id.messagesRecyclerView);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        rvMessages.setItemAnimator(new DefaultItemAnimator());
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        adapter = new IncomingMessagesAdapter(this.getContext(), smsModelSMSdetails);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (InternetConnection.FabCheckConnection(UnsynchedInboxFragment.this)){
//                    new FabProcessReceivedSMSMessages().messageReceived(getActivity(), titleView);
//                    Snackbar.make(view, "Let's send messages to POS, sending...", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                } else {
//                    new FabProcessReceivedSMSMessages().messageReceived(getActivity(), titleView);
//                    Snackbar.make(view, "This device doesn't have an active connection...", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
                if (InternetConnection.FabCheckConnection(UnsynchedInboxFragment.this)){
                     MainActivity.DB_HELPER.FabRefreshInboxes();
                } else {
                    refreshSmsMessagesInbox();
                    Snackbar.make(view, "This device can't connect to server, check connection...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


            }
        });

        //refreshing the recycler view
        refreshSmsMessagesInbox();


        return v;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refreshSmsMessagesInbox(){
        smsModelSMSdetails.clear();

        DBHelper db = new DBHelper(this.getContext());
        db.openDB();

        //RETRIEVE
        Cursor c = db.getAllMessagesNotSynched();

        //LOOP AND ADD TO ARRAYLIST
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            SMSModelSMSdetails sms = new SMSModelSMSdetails();
            String checkAddress = "Safaricom";

            sms.smsMessageSerial = c.getInt(c.getColumnIndexOrThrow(dbConstants.SMS_MESSAGE_SERIAL));
            sms.timeStamp  =  c.getString(c.getColumnIndexOrThrow(dbConstants.SMS_TIMESTAMP));
            sms.messageBody = c.getString(c.getColumnIndexOrThrow(dbConstants.SMS_MESSAGE_BODY));
            sms.originationAddress = c.getString(c.getColumnIndexOrThrow(dbConstants.SMS_ORIGINATING_ADDRESS));
            sms.messageStatusOnSim = c.getInt(c.getColumnIndexOrThrow(dbConstants.SMS_MESSAGE_STATUS_ON_SIM));
            sms.messagePDU = c.getString(c.getColumnIndexOrThrow(dbConstants.SMS_MESSAGE_PDU));
            sms.protocolIdentifier = c.getInt(c.getColumnIndexOrThrow(dbConstants.SMS_PROTOCOL_IDENTIFIER));
            sms.messageServiceCenter = c.getString(c.getColumnIndexOrThrow(dbConstants.SMS_MESSAGE_SERVICE_CENTER));
            sms.messageUserData = c.getString(c.getColumnIndexOrThrow(dbConstants.SMS_MESSAGE_USER_DATA));
            sms.isStatusReport = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(dbConstants.SMS_IS_STATUS_REPORT)));
            sms.isMWIMessage  = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(dbConstants.SMS_IS_MWI_MESSAGE)));
            sms.messageReadDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(c.getLong(c.getColumnIndexOrThrow(dbConstants.SMS_MESSAGE_READ_DATE))), TimeZone.getDefault().toZoneId());

//            if (sms.originationAddress.equals(checkAddress)){
//                smsModelSMSdetails.add(sms);
//            }
            smsModelSMSdetails.add(sms);

        }

        //CHECKING IF ARRAY LIST ISN'T EMPTY

        if (!(smsModelSMSdetails.size() < 1)) {
            rvMessages.setVisibility(View.VISIBLE);
            emptyInboxPageImage.setVisibility(View.GONE);
            emptyInboxPageText.setVisibility(View.GONE);
            rvMessages.setAdapter(adapter);
        } else {
            rvMessages.setVisibility(View.GONE);
            emptyInboxPageImage.setVisibility(View.VISIBLE);
            emptyInboxPageText.setVisibility(View.VISIBLE);
        }

        db.closeDB();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadSMSAndPushToPOS(){
        new ProcessReceivedSMSMessage().messageReceived(getActivity(), titleView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void autoLoadSMSAndPushToPOS(){
        new ProcessReceivedSMSMessage().messageReceived(getActivity(), titleView);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}