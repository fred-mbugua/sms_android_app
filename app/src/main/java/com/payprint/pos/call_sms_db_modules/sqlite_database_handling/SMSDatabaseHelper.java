//package com.payprint.pos.call_sms_db_modules.sqlite_database_handling;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.os.Build;
//
//import androidx.annotation.RequiresApi;
//
//import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.MessagesModel;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.TimeZone;
//
//public class SMSDatabaseHelper extends SQLiteOpenHelper {
//
//    public static SMSSender smsSender;
//
//
//    // Database Version
//    public static final int DATABASE_VERSION = 11;
//    public static final String INCOMING_TABLE_NAME = "dotpesa_sms_incoming_messages";
//    public static final String SMS_MESSAGE_SERIAL = "sms_message_serial";
//    public static final String SMS_TIMESTAMP = "sms_timestamp";
//    public static final String SMS_MESSAGE_BODY = "sms_message_body";
//    public static final String SMS_DISPLAY_MESSAGE_BODY = "sms_display_message_body";
//    public static final String SMS_ORIGINATING_ADDRESS = "sms_origination_address";
//    public static final String SMS_ORIGINATING_DISPLAY_ADDRESS = "sms_originating_display_address";
//    public static final String SMS_MESSAGE_STATUS = "sms_message_status";
//    public static final String SMS_MESSAGE_STATUS_ON_SIM = "sms_message_status_on_sim";
//    public static final String SMS_MESSAGE_PDU = "sms_message_pdu";
//    public static final String SMS_PROTOCOL_IDENTIFIER = "sms_protocol_identifier";
//    public static final String SMS_MESSAGE_SERVICE_CENTER = "sms_message_service_center";
//    public static final String SMS_MESSAGE_USER_DATA = "sms_message_user_data";
//    public static final String SMS_IS_STATUS_REPORT = "sms_is_status_report";
//    public static final String SMS_IS_MWI_MESSAGE = "sms_is_mwi_message";
//    public static final String SMS_MESSAGE_READ_DATE = "sms_message_read_date";
//    public static final String SMS_IS_MESSAGE_SYNCHRONIZED = "sms_is_message_synchronized";
//    public static final String SMS_MESSAGE_SYNCHRONIZED_DATE = "sms_message_synchronized_date";
//    public static final String SMS_MESSAGE_IS_READ = "sms_message_is_read";
//
//
//
//    public static final String OUTGOING_TABLE_NAME = "dotpesa_sms_outgoing_messages";
//
//    public static final String SMS_OUTGOING_MESSAGE_SERIAL = "sms_message_serial";
//    public static final String SMS_OUTGOING_TIMESTAMP = "sms_timestamp";
//    public static final String SMS_OUTGOING_LIST_SERIAL = "sms_list_serial";
//    public static final String SMS_OUTGOING_MESSAGE_BODY = "sms_message_body";
//    public static final String SMS_OUTGOING_PHONE_ADDRESS = "sms_phone_number";
//    public static final String SMS_OUTGOING_IS_SENT_ATTEMPT =  "sms_outgoing_is_sent_attempt";
//    public static final String SMS_OUTGOING_IS_SENT = "sms_outgoing_is_sent";
//    public static final String SMS_OUTGOING_IS_SENT_FAILED = "sms_outgoing_is_sent_failed";
//    public static final String SMS_OUTGOING_SENT_DATE = "sms_outgoing_sent_date";
//    public static final String SMS_OUTGOING_IS_DELIVERED = "sms_outgoing_is_delivered";
//    public static final String SMS_OUTGOING_DELIVERED_DATE = "sms_outgoing_delivered_date";
//
//    private int id;
//    private String note;
//    private String timestamp;
//
//    public static SQLiteDatabase smsDatabase;
//
//    // Database Name
//    public static final String DATABASE_NAME = "dotpesa_sms_managemnt";
//
//    public static final String CREATE_TABLE_INCOMING =
//            "CREATE TABLE " + INCOMING_TABLE_NAME + "("
//                    + SMS_MESSAGE_SERIAL + " integer primary key AUTOINCREMENT,"
//                    + SMS_TIMESTAMP + " DATETIME default CURRENT_TIMESTAMP,"
//                    + SMS_MESSAGE_BODY + " TEXT,"
//                    + SMS_DISPLAY_MESSAGE_BODY + " TEXT,"
//                    + SMS_ORIGINATING_ADDRESS + " TEXT,"
//                    + SMS_ORIGINATING_DISPLAY_ADDRESS + " TEXT,"
//                    + SMS_MESSAGE_STATUS + " TEXT,"
//                    + SMS_MESSAGE_STATUS_ON_SIM + " INTEGER ,"
//                    + SMS_MESSAGE_PDU + " TEXT,"
//                    + SMS_PROTOCOL_IDENTIFIER + " INTEGER,"
//                    + SMS_MESSAGE_SERVICE_CENTER + " TEXT,"
//                    + SMS_MESSAGE_USER_DATA + " TEXT,"
//                    + SMS_IS_STATUS_REPORT + " BOOLEAN default 'FALSE',"
//                    + SMS_IS_MWI_MESSAGE + " BOOLEAN default 'FALSE' ,"
//                    + SMS_MESSAGE_READ_DATE + " DATETIME default CURRENT_TIMESTAMP,"
//                    + SMS_IS_MESSAGE_SYNCHRONIZED + " BOOLEAN default 'FALSE',"
//                    + SMS_MESSAGE_SYNCHRONIZED_DATE + " DATETIME default null"
////                    + SMS_MESSAGE_IS_READ + " BOOLEAN default 'FALSE'"
//                    + ")";
//
//    public static final String CREATE_TABLE_OUTGOING =
//            "CREATE TABLE " + OUTGOING_TABLE_NAME + "("
//                    + SMS_OUTGOING_MESSAGE_SERIAL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + SMS_OUTGOING_LIST_SERIAL + " TEXT, "
//                    + SMS_OUTGOING_TIMESTAMP + " DATETIME default CURRENT_TIMESTAMP, "
//                    + SMS_OUTGOING_MESSAGE_BODY + " TEXT, "
//                    + SMS_OUTGOING_PHONE_ADDRESS + " TEXT,"
//                    + SMS_OUTGOING_IS_SENT_ATTEMPT + "BOOLEAN DEFAULT 'FALSE' ,"
//                    + SMS_OUTGOING_IS_SENT + " BOOLEAN DEFAULT  'FALSE' ,"
//                    + SMS_OUTGOING_IS_SENT_FAILED + " BOOLEAN DEFAULT 'FALSE',"
//                    + SMS_OUTGOING_SENT_DATE + " DATETIME,"
//                    + SMS_OUTGOING_IS_DELIVERED + " BOOLEAN DEFAULT 'FALSE',"
//                    + SMS_OUTGOING_DELIVERED_DATE + " DATETIME"
//                    + ")";
//
//
//    public SMSDatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        smsDatabase = getWritableDatabase();
//        smsSender = new SMSSender();
//    }
//
//    // Creating Tables
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // create notes table
//        db.execSQL(CREATE_TABLE_INCOMING);
//        db.execSQL(CREATE_TABLE_OUTGOING);
//    }
//
//
//    // Upgrading database
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + INCOMING_TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + OUTGOING_TABLE_NAME);
//        // Create tables again
//        onCreate(db);
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public long saveSMSMessageToDB(SMSMessageDetails sms) {
//
//        ContentValues cv = new ContentValues();
//
//        cv.put(SMS_TIMESTAMP ,sms.timeStamp);
//        cv.put(SMS_MESSAGE_BODY ,sms.messageBody);
//        cv.put(SMS_DISPLAY_MESSAGE_BODY ,sms.messageDisplayBody);
//        cv.put(SMS_ORIGINATING_ADDRESS ,sms.originationAddress);
//        cv.put(SMS_ORIGINATING_DISPLAY_ADDRESS ,sms.originatingDisplayAddress);
//        cv.put(SMS_MESSAGE_STATUS ,sms.messageStatus);
//        cv.put(SMS_MESSAGE_STATUS_ON_SIM ,sms.messageStatusOnSim);
//        cv.put(SMS_MESSAGE_PDU , sms.messagePDU);
//        cv.put(SMS_PROTOCOL_IDENTIFIER , sms.protocolIdentifier);
//        cv.put(SMS_MESSAGE_SERVICE_CENTER , sms.messageServiceCenter);
//        cv.put(SMS_MESSAGE_USER_DATA , sms.messageUserData);
//        cv.put(SMS_IS_STATUS_REPORT , sms.isStatusReport);
//        cv.put(SMS_IS_MWI_MESSAGE , sms.isMWIMessage);
//        cv.put(SMS_IS_MESSAGE_SYNCHRONIZED, sms.isMessageSynchronised);
////        cv.put(SMS_MESSAGE_IS_READ, sms.isSMSIsRead);
//        cv.put(SMS_MESSAGE_READ_DATE , sms.messageReadDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
//
//        return smsDatabase.insert(INCOMING_TABLE_NAME, null, cv);
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public long saveOutGoingSMSMessageOutboxToDB(SMSMessageOutboxDetails sms) {
//
//        ContentValues cvs = new ContentValues();
//        cvs.put(SMS_OUTGOING_TIMESTAMP ,sms.timeStamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
//        cvs.put(SMS_OUTGOING_MESSAGE_BODY ,sms.messageBody);
//        cvs.put(SMS_OUTGOING_LIST_SERIAL ,sms.smsMessageSerial);
//        cvs.put(SMS_OUTGOING_PHONE_ADDRESS ,sms.smsPhoneNumber);
//        return smsDatabase.insert(OUTGOING_TABLE_NAME, null, cvs);
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ArrayList<SMSMessageDetails> getSMSMessagesList(){
//
//        ArrayList<SMSMessageDetails> smsList = new ArrayList<SMSMessageDetails>();
//        ArrayList<MessagesModel> databaseMessagesDetails = new ArrayList<MessagesModel>();
//
//
//        String[] field = {SMS_MESSAGE_SERIAL , SMS_TIMESTAMP , SMS_MESSAGE_BODY , SMS_DISPLAY_MESSAGE_BODY , SMS_ORIGINATING_ADDRESS , SMS_ORIGINATING_DISPLAY_ADDRESS , SMS_MESSAGE_STATUS , SMS_MESSAGE_STATUS_ON_SIM ,SMS_MESSAGE_PDU , SMS_PROTOCOL_IDENTIFIER , SMS_MESSAGE_SERVICE_CENTER , SMS_MESSAGE_USER_DATA , SMS_IS_STATUS_REPORT , SMS_IS_MWI_MESSAGE , SMS_MESSAGE_READ_DATE, SMS_IS_MESSAGE_SYNCHRONIZED, SMS_MESSAGE_SYNCHRONIZED_DATE};
//        Cursor c = smsDatabase.query(INCOMING_TABLE_NAME, field, null, null, null, null, null);
//
//        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
//            SMSMessageDetails sms = new SMSMessageDetails();
//
//
//            sms.smsMessageSerial = c.getInt(c.getColumnIndexOrThrow(SMS_MESSAGE_SERIAL));
//            sms.timeStamp  =  c.getString(c.getColumnIndexOrThrow(SMS_TIMESTAMP));
//            sms.messageBody = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_BODY));
//            sms.messageDisplayBody = c.getString(c.getColumnIndexOrThrow(SMS_DISPLAY_MESSAGE_BODY));
//
//            sms.originationAddress = c.getString(c.getColumnIndexOrThrow(SMS_ORIGINATING_ADDRESS));
//            sms.originatingDisplayAddress =  c.getString(c.getColumnIndexOrThrow(SMS_ORIGINATING_DISPLAY_ADDRESS));
//
//            sms.messageStatus = c.getInt(c.getColumnIndexOrThrow(SMS_MESSAGE_STATUS));
//            sms.messageStatusOnSim = c.getInt(c.getColumnIndexOrThrow(SMS_MESSAGE_STATUS_ON_SIM));
//            sms.messagePDU = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_PDU));
//            sms.protocolIdentifier = c.getInt(c.getColumnIndexOrThrow(SMS_PROTOCOL_IDENTIFIER));
//            sms.messageServiceCenter = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_SERVICE_CENTER));
//            sms.messageUserData = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_USER_DATA));
//
//            sms.isStatusReport = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(SMS_IS_STATUS_REPORT)));
//            sms.isMWIMessage  = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(SMS_IS_MWI_MESSAGE)));
//
//            sms.messageReadDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(c.getLong(c.getColumnIndexOrThrow(SMS_MESSAGE_READ_DATE))),
//                    TimeZone.getDefault().toZoneId());
//
//
//
////            MessagesModel messagesModel = new MessagesModel(sms.originationAddress, sms.messageDisplayBody, sms.timeStamp);
////            MessagesModel messagesModel = new MessagesModel(sms.originationAddress, sms.messageDisplayBody, sms.timeStamp);
////            databaseMessagesDetails.add(messagesModel);
//            //Adding to arraylist
////            databaseMessagesDetails.add(messagesModel);
//            smsList.add(sms);
//
//        }
//        return smsList;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ArrayList<SMSMessageDetails> getSMSMessagesLastMessage(){
//
//        ArrayList<SMSMessageDetails> smsLastList = new ArrayList<SMSMessageDetails>();
//        String[] field = {SMS_MESSAGE_SERIAL , SMS_TIMESTAMP , SMS_MESSAGE_BODY , SMS_DISPLAY_MESSAGE_BODY , SMS_ORIGINATING_ADDRESS , SMS_ORIGINATING_DISPLAY_ADDRESS , SMS_MESSAGE_STATUS , SMS_MESSAGE_STATUS_ON_SIM ,SMS_MESSAGE_PDU , SMS_PROTOCOL_IDENTIFIER , SMS_MESSAGE_SERVICE_CENTER , SMS_MESSAGE_USER_DATA , SMS_IS_STATUS_REPORT , SMS_IS_MWI_MESSAGE , SMS_MESSAGE_READ_DATE, SMS_IS_MESSAGE_SYNCHRONIZED, SMS_MESSAGE_SYNCHRONIZED_DATE};
//        Cursor c = smsDatabase.query(INCOMING_TABLE_NAME, field, null, null, null, null, null);
//
//        for (c.moveToLast(); !c.isAfterLast(); c.moveToNext()){
//            SMSMessageDetails sms = new SMSMessageDetails();
//
//            sms.smsMessageSerial = c.getInt(c.getColumnIndexOrThrow(SMS_MESSAGE_SERIAL));
//            sms.timeStamp  =  c.getString(c.getColumnIndexOrThrow(SMS_TIMESTAMP));
//            sms.messageBody = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_BODY));
//            sms.messageDisplayBody = c.getString(c.getColumnIndexOrThrow(SMS_DISPLAY_MESSAGE_BODY));
//
//            sms.originationAddress = c.getString(c.getColumnIndexOrThrow(SMS_ORIGINATING_ADDRESS));
//            sms.originatingDisplayAddress =  c.getString(c.getColumnIndexOrThrow(SMS_ORIGINATING_DISPLAY_ADDRESS));
//
//            sms.messageStatus = c.getInt(c.getColumnIndexOrThrow(SMS_MESSAGE_STATUS));
//            sms.messageStatusOnSim = c.getInt(c.getColumnIndexOrThrow(SMS_MESSAGE_STATUS_ON_SIM));
//            sms.messagePDU = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_PDU));
//            sms.protocolIdentifier = c.getInt(c.getColumnIndexOrThrow(SMS_PROTOCOL_IDENTIFIER));
//            sms.messageServiceCenter = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_SERVICE_CENTER));
//            sms.messageUserData = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_USER_DATA));
//
//            sms.isStatusReport = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(SMS_IS_STATUS_REPORT)));
//            sms.isMWIMessage  = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(SMS_IS_MWI_MESSAGE)));
//
//            sms.messageReadDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(c.getLong(c.getColumnIndexOrThrow(SMS_MESSAGE_READ_DATE))),
//                    TimeZone.getDefault().toZoneId());
//
//            smsLastList.add(sms);
//        }
//        return smsLastList;
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ArrayList<SMSMessageOutboxDetails> getSMSOutboxMessagesList(){
//        ArrayList<SMSMessageOutboxDetails> OutboxsmsList = new ArrayList<SMSMessageOutboxDetails>();
//        String[] field = {SMS_OUTGOING_MESSAGE_SERIAL, SMS_OUTGOING_TIMESTAMP, SMS_OUTGOING_LIST_SERIAL , SMS_MESSAGE_BODY, SMS_OUTGOING_PHONE_ADDRESS};
//
//        Cursor c = smsDatabase.query(OUTGOING_TABLE_NAME, field, null, null, null, null, null);
//
//        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
//            SMSMessageOutboxDetails smsOutbox = new SMSMessageOutboxDetails();
//
//            smsOutbox.smsMessageSerial = c.getString(c.getColumnIndexOrThrow(SMS_MESSAGE_SERIAL));
//            smsOutbox.timeStamp  =  LocalDateTime.ofInstant(Instant.ofEpochMilli(c.getLong(c.getColumnIndexOrThrow(SMS_TIMESTAMP))),
//                    TimeZone.getDefault().toZoneId());
//            smsOutbox.messageBody = c.getString(c.getColumnIndexOrThrow(SMS_OUTGOING_MESSAGE_BODY));
//
//            smsOutbox.smsPhoneNumber = c.getString(c.getColumnIndexOrThrow(SMS_OUTGOING_PHONE_ADDRESS));
//
//            OutboxsmsList.add(smsOutbox);
//        }
//        return OutboxsmsList;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void deleteSMSMessageIncomingInDisk(SMSMessageDetails sms){
//        smsDatabase.delete(INCOMING_TABLE_NAME, SMS_MESSAGE_SERIAL+"=?",new String[]{String.valueOf(sms.smsMessageSerial)});
//
//    }
//
//    //
////    @RequiresApi(api = Build.VERSION_CODES.O)
////    public void markSMSMessageIncomingInDiskTRUE(SMSMessageDetails sms){
////        smsDatabase.delete(INCOMING_TABLE_NAME, SMS_MESSAGE_SERIAL+"=?",new String[]{String.valueOf(sms.smsMessageSerial)});
////
////    }
//
//
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ArrayList<SMSMessageDetails> updateOutgoingSMSisSynchronized(SMSMessageDetails sms){
//        ContentValues cv = new ContentValues();
//        cv.put(SMS_IS_MESSAGE_SYNCHRONIZED, "true");
//        LocalDateTime ldt=LocalDateTime.now();
//        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
//        cv.put(SMS_MESSAGE_SYNCHRONIZED_DATE, ldt.format(dtf));
//        smsDatabase.update(INCOMING_TABLE_NAME, cv, SMSDatabaseHelper.SMS_OUTGOING_MESSAGE_SERIAL + "=?", new String[]{String.valueOf(sms.smsMessageSerial)});
//        return getSMSMessagesList();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ArrayList<SMSMessageDetails> clearAllSMSFromList(){
//        smsDatabase.delete(INCOMING_TABLE_NAME, "",new String[]{});
//        return getSMSMessagesList();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ArrayList<SMSMessageOutboxDetails> clearAllOutgoingSMSFromList(){smsDatabase.delete(OUTGOING_TABLE_NAME, "",new String[]{});
//        return getSMSOutboxMessagesList();
//    }
//
//
//}
