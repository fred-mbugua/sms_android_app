package com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database;

public class Constants {

    // COLUMNS - SMS
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
    public static final String SMS_ACCOUNT_NAME = "account_name";

    // COLUMNS - C2B TRANSACTIONS
    public static final String C2B_ID = "id";
    public static final String C2B_TRANS_ID = "trans_id";
    public static final String C2B_TRANS_TIME = "trans_time";
    public static final String C2B_AMOUNT = "amount";
    public static final String C2B_BUSINESS_SHORTCODE = "business_shortcode";
    public static final String C2B_BILL_REF_NUMBER = "bill_ref_number";
    public static final String C2B_MSISDN = "msisdn";
    public static final String C2B_FIRST_NAME = "first_name";
    public static final String C2B_MIDDLE_NAME = "middle_name";
    public static final String C2B_LAST_NAME = "last_name";
    public static final String C2B_ACCOUNT_NAME = "account_name";
    public static final String C2B_CREATED_AT = "created_at";
    public static final String C2B_TRANS_TIME_EAT = "trans_time_eat";
    public static final String C2B_TRANS_TIME_FORMATTED = "trans_time_formatted";
    public static final String C2B_EXTRA_NOTE = "extra_note";
    public static final String C2B_EXTRA_CATEGORY = "extra_category";

    // COLUMNS - PUSHED SMS TRANSACTIONS
    public static final String PUSHED_SMS_ID = "id";
    public static final String PUSHED_SMS_SERIAL = "sms_serial";
    public static final String PUSHED_SMS_SENDER = "sender";
    public static final String PUSHED_SMS_MESSAGE = "message";
    public static final String PUSHED_SMS_TIMESTAMP = "timestamp";
    public static final String PUSHED_SMS_SERVICE_CENTER = "service_center";
    public static final String PUSHED_SMS_CREATED_AT = "created_at";
    public static final String PUSHED_SMS_STATUS = "sms_message_status";
    public static final String PUSHED_SMS_STATUS_ON_SIM = "sms_message_status_on_sim";
    public static final String PUSHED_SMS_PDU = "sms_message_pdu";
    public static final String PUSHED_SMS_PROTOCOL_IDENTIFIER = "sms_protocol_identifier";
    public static final String PUSHED_SMS_USER_DATA = "sms_message_user_data";
    public static final String PUSHED_SMS_IS_STATUS_REPORT = "sms_is_status_report";
    public static final String PUSHED_SMS_IS_MWI_MESSAGE = "sms_is_mwi_message";
    public static final String PUSHED_SMS_READ_DATE = "sms_message_read_date";
    public static final String PUSHED_SMS_IS_SYNCHRONIZED = "sms_is_message_synchronized";
    public static final String PUSHED_SMS_SYNCHRONIZED_DATE = "sms_message_synchronized_date";
    public static final String PUSHED_SMS_MODEM_NAME = "modem_name";
    public static final String PUSHED_SMS_ACCOUNT_NAME = "account_name";

    // COLUMNS - EXCEL IMPORTED TRANSACTIONS
    public static final String EXCEL_ID = "id";
    public static final String EXCEL_SHEET_NAME = "sheet_name";
    public static final String EXCEL_SN = "sn";
    public static final String EXCEL_ACCOUNT_NUMBER = "account_number";
    public static final String EXCEL_ACCOUNT_NAME = "account_name";
    public static final String EXCEL_CONTACT = "contact";
    public static final String EXCEL_OP_BAL = "op_bal";
    public static final String EXCEL_PP_BAL = "pp_bal";
    public static final String EXCEL_TOTAL_BAL = "total_bal";
    public static final String EXCEL_AMOUNT = "amount";
    public static final String EXCEL_TERM1 = "term1";
    public static final String EXCEL_RAW_JSON = "raw_json";
    public static final String EXCEL_CREATED_AT = "created_at";

    // COLUMNS - PENDING EXCEL UPLOADS
    public static final String PENDING_EXCEL_ID = "id";
    public static final String PENDING_EXCEL_FILE_NAME = "file_name";
    public static final String PENDING_EXCEL_TOTAL_ROWS = "total_rows";
    public static final String PENDING_EXCEL_PAYLOAD_JSON = "payload_json";
    public static final String PENDING_EXCEL_CREATED_AT = "created_at";
    public static final String PENDING_EXCEL_STATUS = "status";

    // DATABASE PROPERTIES
    public static final String DATABASE_NAME = "dotpesa_sms_managemnt";
    public static final String INCOMING_TABLE_NAME = "dotpesa_sms_incoming_messages";
    public static final String C2B_TABLE_NAME = "c2b_transactions";
    public static final String PUSHED_SMS_TABLE_NAME = "pushed_sms_transactions";
    public static final String EXCEL_TABLE_NAME = "excel_imported_transactions";
    public static final String PENDING_EXCEL_UPLOADS_TABLE_NAME = "pending_excel_uploads";
    public static final int DATABASE_VERSION = 6;

    public static final String CREATE_TABLE_INCOMING =
            "CREATE TABLE " + INCOMING_TABLE_NAME + "("
                    + SMS_MESSAGE_SERIAL + " integer primary key AUTOINCREMENT,"
                    + SMS_TIMESTAMP + " TEXT,"
                    + SMS_MESSAGE_BODY + " TEXT,"
                    + SMS_ORIGINATING_ADDRESS + " TEXT,"
                    + SMS_ACCOUNT_NAME + " TEXT,"
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

    public static final String CREATE_TABLE_C2B =
            "CREATE TABLE IF NOT EXISTS " + C2B_TABLE_NAME + "("
                    + C2B_ID + " TEXT PRIMARY KEY,"
                    + C2B_TRANS_ID + " TEXT,"
                    + C2B_TRANS_TIME + " TEXT,"
                    + C2B_AMOUNT + " REAL,"
                    + C2B_BUSINESS_SHORTCODE + " TEXT,"
                    + C2B_BILL_REF_NUMBER + " TEXT,"
                    + C2B_MSISDN + " TEXT,"
                    + C2B_FIRST_NAME + " TEXT,"
                    + C2B_MIDDLE_NAME + " TEXT,"
                    + C2B_LAST_NAME + " TEXT,"
                    + C2B_ACCOUNT_NAME + " TEXT,"
                    + C2B_EXTRA_NOTE + " TEXT,"
                    + C2B_EXTRA_CATEGORY + " TEXT,"
                    + C2B_CREATED_AT + " TEXT,"
                    + C2B_TRANS_TIME_EAT + " TEXT,"
                    + C2B_TRANS_TIME_FORMATTED + " TEXT"
                    + ")";

    public static final String CREATE_TABLE_PUSHED_SMS =
            "CREATE TABLE IF NOT EXISTS " + PUSHED_SMS_TABLE_NAME + "("
                    + PUSHED_SMS_ID + " TEXT PRIMARY KEY,"
                    + PUSHED_SMS_SERIAL + " INTEGER,"
                    + PUSHED_SMS_SENDER + " TEXT,"
                    + PUSHED_SMS_MESSAGE + " TEXT,"
                    + PUSHED_SMS_TIMESTAMP + " TEXT,"
                    + PUSHED_SMS_SERVICE_CENTER + " TEXT,"
                    + PUSHED_SMS_ACCOUNT_NAME + " TEXT,"
                    + PUSHED_SMS_CREATED_AT + " TEXT,"
                    + PUSHED_SMS_STATUS + " TEXT,"
                    + PUSHED_SMS_STATUS_ON_SIM + " INTEGER,"
                    + PUSHED_SMS_PDU + " TEXT,"
                    + PUSHED_SMS_PROTOCOL_IDENTIFIER + " INTEGER,"
                    + PUSHED_SMS_USER_DATA + " TEXT,"
                    + PUSHED_SMS_IS_STATUS_REPORT + " BOOLEAN,"
                    + PUSHED_SMS_IS_MWI_MESSAGE + " BOOLEAN,"
                    + PUSHED_SMS_READ_DATE + " TEXT,"
                    + PUSHED_SMS_IS_SYNCHRONIZED + " BOOLEAN,"
                    + PUSHED_SMS_SYNCHRONIZED_DATE + " TEXT,"
                    + PUSHED_SMS_MODEM_NAME + " TEXT"
                    + ")";

    public static final String CREATE_TABLE_EXCEL =
            "CREATE TABLE IF NOT EXISTS " + EXCEL_TABLE_NAME + "("
                    + EXCEL_ID + " TEXT PRIMARY KEY,"
                    + EXCEL_SHEET_NAME + " TEXT,"
                    + EXCEL_SN + " TEXT,"
                    + EXCEL_ACCOUNT_NUMBER + " TEXT,"
                    + EXCEL_ACCOUNT_NAME + " TEXT,"
                    + EXCEL_CONTACT + " TEXT,"
                    + EXCEL_OP_BAL + " TEXT,"
                    + EXCEL_PP_BAL + " TEXT,"
                    + EXCEL_TOTAL_BAL + " TEXT,"
                    + EXCEL_AMOUNT + " REAL,"
                    + EXCEL_TERM1 + " TEXT,"
                    + EXCEL_RAW_JSON + " TEXT,"
                    + EXCEL_CREATED_AT + " TEXT"
                    + ")";

    public static final String CREATE_TABLE_PENDING_EXCEL_UPLOADS =
            "CREATE TABLE IF NOT EXISTS " + PENDING_EXCEL_UPLOADS_TABLE_NAME + "("
                    + PENDING_EXCEL_ID + " TEXT PRIMARY KEY,"
                    + PENDING_EXCEL_FILE_NAME + " TEXT,"
                    + PENDING_EXCEL_TOTAL_ROWS + " INTEGER,"
                    + PENDING_EXCEL_PAYLOAD_JSON + " TEXT,"
                    + PENDING_EXCEL_CREATED_AT + " TEXT,"
                    + PENDING_EXCEL_STATUS + " TEXT"
                    + ")";
}
