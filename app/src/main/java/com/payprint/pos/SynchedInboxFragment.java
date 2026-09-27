package com.payprint.pos;

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

import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.IncomingMessagesAdapter;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.Constants;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database.SMSModelSMSdetails;
import com.payprint.pos.core.App;
import com.payprint.pos.utilities.SmsFilterManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import com.payprint.pos.R;

public class SynchedInboxFragment extends Fragment {
    Context c;
    Constants dbConstants = new Constants();
    public static SynchedInboxFragment inst;

    public SynchedInboxFragment() {
    }

    public static SynchedInboxFragment instance() {
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
        v = inflater.inflate(R.layout.fragment_synched_inbox, container, false);
        listItemsView = inflater.inflate(R.layout.each_synced_message, container, false);
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
//        rvMessages.setAdapter(adapter);



        //refreshing the recycler view
        refreshSmsMessagesInbox();


        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void refreshSmsMessagesInbox(){
        smsModelSMSdetails.clear();

        Context context = getContext() != null ? getContext() : App.getInstance();
        DBHelper db = new DBHelper(context);
        db.openDB();

        //RETRIEVE
        Cursor c = db.getAllMessagesSynced();

        if (c != null) {
            //LOOP AND ADD TO ARRAYLIST
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
                SMSModelSMSdetails sms = new SMSModelSMSdetails();

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

                if (SmsFilterManager.isMessageAllowed(context, sms.originationAddress, sms.messageBody)) {
                    smsModelSMSdetails.add(sms);
                }
            }
            c.close();
        }

        //CHECKING IF ARRAY LIST ISN'T EMPTY

        if (!(smsModelSMSdetails.size() < 1)) {
            if (rvMessages != null) rvMessages.setVisibility(View.VISIBLE);
            if (emptyInboxPageImage != null) emptyInboxPageImage.setVisibility(View.GONE);
            if (emptyInboxPageText != null) emptyInboxPageText.setVisibility(View.GONE);
            if (rvMessages != null) rvMessages.setAdapter(adapter);
        } else {
            if (rvMessages != null) rvMessages.setVisibility(View.GONE);
            if (emptyInboxPageImage != null) emptyInboxPageImage.setVisibility(View.VISIBLE);
            if (emptyInboxPageText != null) emptyInboxPageText.setVisibility(View.VISIBLE);
        }

        db.closeDB();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void filterMessages(String query) {
        if (query == null || query.trim().isEmpty()) {
            refreshSmsMessagesInbox();
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        ArrayList<SMSModelSMSdetails> filtered = new ArrayList<>();
        for (SMSModelSMSdetails sms : smsModelSMSdetails) {
            boolean matchesSender = sms.originationAddress != null && sms.originationAddress.toLowerCase().contains(lowerQuery);
            boolean matchesBody = sms.messageBody != null && sms.messageBody.toLowerCase().contains(lowerQuery);
            boolean matchesTime = sms.timeStamp != null && sms.timeStamp.toLowerCase().contains(lowerQuery);

            if (matchesSender || matchesBody || matchesTime) {
                filtered.add(sms);
            }
        }

        if (!filtered.isEmpty()) {
            rvMessages.setVisibility(View.VISIBLE);
            emptyInboxPageImage.setVisibility(View.GONE);
            emptyInboxPageText.setVisibility(View.GONE);
            adapter.updateData(filtered);
        } else {
            rvMessages.setVisibility(View.GONE);
            emptyInboxPageImage.setVisibility(View.VISIBLE);
            emptyInboxPageText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
