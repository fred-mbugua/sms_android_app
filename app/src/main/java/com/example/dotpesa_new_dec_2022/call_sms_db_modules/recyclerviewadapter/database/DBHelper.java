package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.dotpesa_new_dec_2022.SynchedInboxFragment;
import com.example.dotpesa_new_dec_2022.UnsynchedInboxFragment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

public class DBHelper extends SQLiteOpenHelper {

    public  SQLiteDatabase db;
    public String addedToArrayListSmsId;
    Constants dbConstants = new Constants();
    public ArrayList<SMSModelSMSdetails> smsModelSMSdetails = new ArrayList<>();



    public DBHelper(Context context){
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }



    //TABLE CREATION
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(Constants.CREATE_TABLE_INCOMING);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //TABLE UPGRADE
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.INCOMING_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    //INSERTING MESSAGE TO SQLITE DATABASE
    public long add(SMSModelSMSdetails sms){

        try{
            ContentValues cv = new ContentValues();
            cv.put(Constants.SMS_MESSAGE_SERIAL, sms.smsMessageSerial);
            cv.put(Constants.SMS_ORIGINATING_ADDRESS, sms.originationAddress);
            cv.put(Constants.SMS_MESSAGE_BODY, sms.messageBody);
            cv.put(Constants.SMS_TIMESTAMP, sms.timeStamp);
            cv.put(Constants.SMS_MESSAGE_READ_DATE, String.valueOf(sms.messageReadDate));
            cv.put(Constants.SMS_MESSAGE_SERVICE_CENTER, sms.messageServiceCenter);
            cv.put(Constants.SMS_PROTOCOL_IDENTIFIER, sms.protocolIdentifier);
            cv.put(Constants.SMS_MESSAGE_STATUS_ON_SIM, sms.messageStatusOnSim);
            cv.put(Constants.SMS_IS_STATUS_REPORT, sms.isStatusReport);
            cv.put(Constants.SMS_IS_MESSAGE_SYNCHRONIZED, sms.isMessageSynchronised);
            cv.put(Constants.SMS_MESSAGE_PDU, sms.messagePDU);
            cv.put(Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, java.lang.String.valueOf(sms.messageSynchronisedDate));

            return db.insert(Constants.INCOMING_TABLE_NAME, Constants.SMS_MESSAGE_SERIAL, cv);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }



    //open database
    public void openDB() {
        try{
            db = getWritableDatabase();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //close database
    public void closeDB() {
        try{
            close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    //RETREIVE UN_SYNCED MESSAGES
    public Cursor getAllMessagesNotSynched(){

//        //Documentation on sqlite select//
//        String[] tableColumns = new String[] {
//                "column1",
//                "(SELECT max(column1) FROM table2) AS max"
//        };
//        String whereClause = "column1 = ? OR column1 = ?";
//        String[] whereArgs = new String[] {
//                "value1",
//                "value2"
//        };
//        String orderBy = "column1";
//        Cursor c = db.query("table1", tableColumns, whereClause, whereArgs,
//                null, null, orderBy);
//
//        // since we have a named column we can do
//        int idx = c.getColumnIndex("max");
//
//        //End of documentation above//
        String [] columns = {Constants.SMS_MESSAGE_SERIAL,Constants.SMS_ORIGINATING_ADDRESS,Constants.SMS_MESSAGE_BODY,Constants.SMS_TIMESTAMP,Constants.SMS_MESSAGE_READ_DATE,Constants.SMS_MESSAGE_SERVICE_CENTER,Constants.SMS_PROTOCOL_IDENTIFIER,
                Constants.SMS_MESSAGE_STATUS_ON_SIM,Constants.SMS_IS_STATUS_REPORT,Constants.SMS_IS_MESSAGE_SYNCHRONIZED,
                Constants.SMS_MESSAGE_PDU,Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, Constants.SMS_MESSAGE_USER_DATA, Constants.SMS_IS_MWI_MESSAGE};
        String whereClause = Constants.SMS_IS_MESSAGE_SYNCHRONIZED + "=?";
        String[] whereArgs = new String[] {"0"};
        return db.query(Constants.INCOMING_TABLE_NAME,columns,whereClause,whereArgs,null,null,Constants.SMS_TIMESTAMP+" DESC");
    }

    //RETREIVE SYNCED MESSAGES
    public Cursor getAllMessagesSynced(){

//        //Documentation on sqlite select//
//        String[] tableColumns = new String[] {
//                "column1",
//                "(SELECT max(column1) FROM table2) AS max"
//        };
//        String whereClause = "column1 = ? OR column1 = ?";
//        String[] whereArgs = new String[] {
//                "value1",
//                "value2"
//        };
//        String orderBy = "column1";
//        Cursor c = db.query("table1", tableColumns, whereClause, whereArgs,
//                null, null, orderBy);
//
//        // since we have a named column we can do
//        int idx = c.getColumnIndex("max");
//
//        //End of documentation above//
        String [] columns = {Constants.SMS_MESSAGE_SERIAL,Constants.SMS_ORIGINATING_ADDRESS,Constants.SMS_MESSAGE_BODY,Constants.SMS_TIMESTAMP,Constants.SMS_MESSAGE_READ_DATE,Constants.SMS_MESSAGE_SERVICE_CENTER,Constants.SMS_PROTOCOL_IDENTIFIER,
                Constants.SMS_MESSAGE_STATUS_ON_SIM,Constants.SMS_IS_STATUS_REPORT,Constants.SMS_IS_MESSAGE_SYNCHRONIZED,
                Constants.SMS_MESSAGE_PDU,Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, Constants.SMS_MESSAGE_USER_DATA, Constants.SMS_IS_MWI_MESSAGE};
        String whereClause = Constants.SMS_IS_MESSAGE_SYNCHRONIZED + "=?";
        String[] whereArgs = new String[] {"true"};
        return db.query(Constants.INCOMING_TABLE_NAME,columns,whereClause,whereArgs,null,null,Constants.SMS_TIMESTAMP+" DESC");
    }

    //RETREIVE
    public Cursor getAllMessages(){

        String [] columns = {Constants.SMS_MESSAGE_SERIAL,Constants.SMS_ORIGINATING_ADDRESS,Constants.SMS_MESSAGE_BODY,Constants.SMS_TIMESTAMP,Constants.SMS_MESSAGE_READ_DATE,Constants.SMS_MESSAGE_SERVICE_CENTER,Constants.SMS_PROTOCOL_IDENTIFIER,
                Constants.SMS_MESSAGE_STATUS_ON_SIM,Constants.SMS_IS_STATUS_REPORT,Constants.SMS_IS_MESSAGE_SYNCHRONIZED,
                Constants.SMS_MESSAGE_PDU,Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, Constants.SMS_MESSAGE_USER_DATA, Constants.SMS_IS_MWI_MESSAGE};

        return db.query(Constants.INCOMING_TABLE_NAME,columns,null,null,null,null,Constants.SMS_TIMESTAMP+" DESC");
    }

    //RETREIVE THE MESSAGES FOR SENDING TO ERP
    public Cursor getAllMessagesToSendToERP(){
        String [] columns = {Constants.SMS_MESSAGE_SERIAL,Constants.SMS_ORIGINATING_ADDRESS,Constants.SMS_MESSAGE_BODY,Constants.SMS_TIMESTAMP,Constants.SMS_MESSAGE_READ_DATE,Constants.SMS_MESSAGE_SERVICE_CENTER,Constants.SMS_PROTOCOL_IDENTIFIER,
                Constants.SMS_MESSAGE_STATUS_ON_SIM,Constants.SMS_IS_STATUS_REPORT,Constants.SMS_IS_MESSAGE_SYNCHRONIZED,
                Constants.SMS_MESSAGE_PDU,Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, Constants.SMS_MESSAGE_USER_DATA, Constants.SMS_IS_MWI_MESSAGE};
        String whereClause = Constants.SMS_IS_MESSAGE_SYNCHRONIZED + "=?";
        String[] whereArgs = new String[] {"0"};
        return db.query(Constants.INCOMING_TABLE_NAME,columns,whereClause,whereArgs,null,null,Constants.SMS_TIMESTAMP+" DESC");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save(SMSModelSMSdetails sms){

        //OPEN DB TO INSERT MESSAGE
        openDB();

        //COMMIT
        long result = add(sms);

        if(result > 0)
        {
            System.out.println("Message entered");
        }
        else{
            System.out.println("Message not entered");
        }

        //CLOSE DB AFTER INSERT
        closeDB();

        //REFRESH SMS MESSAGES INBOX
        UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
        SynchedInboxFragment.instance().refreshSmsMessagesInbox();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void FabRefreshInboxes(){
        //REFRESH SMS MESSAGES INBOX
        UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
        SynchedInboxFragment.instance().refreshSmsMessagesInbox();
    }


        @RequiresApi(api = Build.VERSION_CODES.O)
        public ArrayList<SMSModelSMSdetails> allDBUnsyncedMessages(){

            ArrayList<SMSModelSMSdetails> smsList = new ArrayList<SMSModelSMSdetails>();


            openDB();

            Cursor c = getAllMessagesToSendToERP();

            //LOOP AND ADD TO ARRAYLIST
            while (c.moveToNext()){
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

                if(!smsList.contains(sms)){
                    smsList.add(sms);
                }

            }
            closeDB();
            return smsList;

        }



//    private boolean smsChecker(String sms) {
//        boolean flagSMS = true;
//
//        if (sms.equals(addedToArrayListSmsId)) {
//            flagSMS = false;
//        } else {
//            addedToArrayListSmsId = sms;
//        }
//        //if flagSMS = true, those 2 messages are different
//        return flagSMS;
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<SMSModelSMSdetails> updateIncomingSMSisSynchronized(SMSModelSMSdetails sms){

        openDB();
        ContentValues cv = new ContentValues();
        cv.put(Constants.SMS_IS_MESSAGE_SYNCHRONIZED, "true");
        LocalDateTime ldt=LocalDateTime.now();
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        cv.put(Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, ldt.format(dtf));
        db.update(Constants.INCOMING_TABLE_NAME, cv, Constants.SMS_MESSAGE_SERIAL + "=?", new String[]{String.valueOf(sms.smsMessageSerial)});
        closeDB();
        return allDBUnsyncedMessages();
    }




}



