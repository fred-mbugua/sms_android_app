package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database;

public class Constants {


    //COLUMNS
    public static final String SMS_MESSAGE_SERIAL = "sms_message_serial";
    public static final String SMS_TIMESTAMP = "sms_timestamp";
    public static final String SMS_MESSAGE_BODY = "sms_message_body";
    public static final String SMS_ORIGINATING_ADDRESS = "sms_origination_address";
    public static final String SMS_MESSAGE_STATUS = "sms_message_status";
    public static final String SMS_MESSAGE_STATUS_ON_SIM = "sms_message_status_on_sim";
    public static final String SMS_MESSAGE_PDU = "sms_message_pdu";
    public static final String SMS_PROTOCOL_IDENTIFIER = "sms_protocol_identifier";
    public static final String SMS_MESSAGE_SERVICE_CENTER = "sms_message_service_center";
    public static final String SMS_MESSAGE_USER_DATA = "sms_message_user_data";
    public static final String SMS_IS_STATUS_REPORT = "sms_is_status_report";
    public static final String SMS_IS_MWI_MESSAGE = "sms_is_mwi_message";
    public static final String SMS_MESSAGE_READ_DATE = "sms_message_read_date";
    public static final String SMS_IS_MESSAGE_SYNCHRONIZED = "sms_is_message_synchronized";
    public static final String SMS_MESSAGE_SYNCHRONIZED_DATE = "sms_message_synchronized_date";
    public static final String SMS_MESSAGE_IS_READ = "sms_message_is_read";

    // DATABASE PROPERTIES
    public static final String DATABASE_NAME = "dotpesa_sms_managemnt";
    public static final String INCOMING_TABLE_NAME = "dotpesa_sms_incoming_messages";
    public static final int DATABASE_VERSION = '1';

    public static final String CREATE_TABLE_INCOMING =
            "CREATE TABLE " + INCOMING_TABLE_NAME + "("
                    + SMS_MESSAGE_SERIAL + " integer primary key AUTOINCREMENT,"
                    + SMS_TIMESTAMP + " TEXT,"
                    + SMS_MESSAGE_BODY + " TEXT,"
                    + SMS_ORIGINATING_ADDRESS + " TEXT,"
                    + SMS_MESSAGE_STATUS + " TEXT,"
                    + SMS_MESSAGE_STATUS_ON_SIM + " INTEGER ,"
                    + SMS_MESSAGE_PDU + " TEXT,"
                    + SMS_PROTOCOL_IDENTIFIER + " INTEGER,"
                    + SMS_MESSAGE_SERVICE_CENTER + " TEXT,"
                    + SMS_MESSAGE_USER_DATA + " TEXT,"
                    + SMS_IS_STATUS_REPORT + " BOOLEAN default 'FALSE',"
                    + SMS_IS_MWI_MESSAGE + " BOOLEAN default 'FALSE' ,"
                    + SMS_MESSAGE_READ_DATE + " DATETIME default CURRENT_TIMESTAMP,"
                    + SMS_IS_MESSAGE_SYNCHRONIZED + " BOOLEAN default 'FALSE',"
                    + SMS_MESSAGE_SYNCHRONIZED_DATE + " DATETIME default null"
                    + ")";
}
