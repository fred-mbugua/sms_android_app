package com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.payprint.pos.SynchedInboxFragment;
import com.payprint.pos.UnsynchedInboxFragment;
import com.payprint.pos.core.App;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class DBHelper extends SQLiteOpenHelper {

    public SQLiteDatabase db;
    public String addedToArrayListSmsId;
    Constants dbConstants = new Constants();
    public ArrayList<SMSModelSMSdetails> smsModelSMSdetails = new ArrayList<>();

    public static class PendingExcelUpload {
        public String id;
        public String fileName;
        public int totalRows;
        public String payloadJson;
        public String createdAt;
        public String status;
    }

    public DBHelper(Context context) {
        super(context != null ? context.getApplicationContext() : App.getInstance(), Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    // TABLE CREATION
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(Constants.CREATE_TABLE_INCOMING);
            sqLiteDatabase.execSQL(Constants.CREATE_TABLE_C2B);
            sqLiteDatabase.execSQL(Constants.CREATE_TABLE_PUSHED_SMS);
            sqLiteDatabase.execSQL(Constants.CREATE_TABLE_EXCEL);
            sqLiteDatabase.execSQL(Constants.CREATE_TABLE_PENDING_EXCEL_UPLOADS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // TABLE UPGRADE
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newVersion) {
        try {
            if (oldversion < 2) {
                sqLiteDatabase.execSQL(Constants.CREATE_TABLE_C2B);
            }
            if (oldversion < 3) {
                sqLiteDatabase.execSQL(Constants.CREATE_TABLE_PUSHED_SMS);
            }
            if (oldversion < 5) {
                sqLiteDatabase.execSQL(Constants.CREATE_TABLE_EXCEL);
                try {
                    sqLiteDatabase.execSQL("ALTER TABLE " + Constants.C2B_TABLE_NAME + " ADD COLUMN " + Constants.C2B_ACCOUNT_NAME + " TEXT");
                } catch (Exception ignored) {}
                try {
                    sqLiteDatabase.execSQL("ALTER TABLE " + Constants.PUSHED_SMS_TABLE_NAME + " ADD COLUMN " + Constants.PUSHED_SMS_ACCOUNT_NAME + " TEXT");
                } catch (Exception ignored) {}
                try {
                    sqLiteDatabase.execSQL("ALTER TABLE " + Constants.INCOMING_TABLE_NAME + " ADD COLUMN " + Constants.SMS_ACCOUNT_NAME + " TEXT");
                } catch (Exception ignored) {}
            }
            if (oldversion < 6) {
                sqLiteDatabase.execSQL(Constants.CREATE_TABLE_PENDING_EXCEL_UPLOADS);
            }
        } catch (Exception e) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.INCOMING_TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.C2B_TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.PUSHED_SMS_TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.EXCEL_TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.PENDING_EXCEL_UPLOADS_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    // open database
    public void openDB() {
        try {
            if (db == null || !db.isOpen()) {
                db = getWritableDatabase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // close database
    public void closeDB() {
        try {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // INSERTING MESSAGE TO SQLITE DATABASE
    public long add(SMSModelSMSdetails sms) {
        try {
            if (db == null || !db.isOpen()) {
                openDB();
            }
            if (db == null) return 0;
            ContentValues cv = new ContentValues();
            if (sms.smsMessageSerial != null && sms.smsMessageSerial > 0) {
                cv.put(Constants.SMS_MESSAGE_SERIAL, sms.smsMessageSerial);
            }
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
            cv.put(Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, String.valueOf(sms.messageSynchronisedDate));

            return db.insertWithOnConflict(Constants.INCOMING_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // RETRIEVE UN_SYNCED MESSAGES
    public Cursor getAllMessagesNotSynched() {
        if (db == null || !db.isOpen()) {
            openDB();
        }
        if (db == null) return null;
        String[] columns = {Constants.SMS_MESSAGE_SERIAL, Constants.SMS_ORIGINATING_ADDRESS, Constants.SMS_MESSAGE_BODY, Constants.SMS_TIMESTAMP, Constants.SMS_MESSAGE_READ_DATE, Constants.SMS_MESSAGE_SERVICE_CENTER, Constants.SMS_PROTOCOL_IDENTIFIER,
                Constants.SMS_MESSAGE_STATUS_ON_SIM, Constants.SMS_IS_STATUS_REPORT, Constants.SMS_IS_MESSAGE_SYNCHRONIZED,
                Constants.SMS_MESSAGE_PDU, Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, Constants.SMS_MESSAGE_USER_DATA, Constants.SMS_IS_MWI_MESSAGE};
        String whereClause = Constants.SMS_IS_MESSAGE_SYNCHRONIZED + "=?";
        String[] whereArgs = new String[]{"0"};
        return db.query(Constants.INCOMING_TABLE_NAME, columns, whereClause, whereArgs, null, null, Constants.SMS_MESSAGE_SERIAL + " DESC");
    }

    // RETRIEVE SYNCED MESSAGES
    public Cursor getAllMessagesSynced() {
        if (db == null || !db.isOpen()) {
            openDB();
        }
        if (db == null) return null;
        String[] columns = {Constants.SMS_MESSAGE_SERIAL, Constants.SMS_ORIGINATING_ADDRESS, Constants.SMS_MESSAGE_BODY, Constants.SMS_TIMESTAMP, Constants.SMS_MESSAGE_READ_DATE, Constants.SMS_MESSAGE_SERVICE_CENTER, Constants.SMS_PROTOCOL_IDENTIFIER,
                Constants.SMS_MESSAGE_STATUS_ON_SIM, Constants.SMS_IS_STATUS_REPORT, Constants.SMS_IS_MESSAGE_SYNCHRONIZED,
                Constants.SMS_MESSAGE_PDU, Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, Constants.SMS_MESSAGE_USER_DATA, Constants.SMS_IS_MWI_MESSAGE};
        String whereClause = Constants.SMS_IS_MESSAGE_SYNCHRONIZED + "=?";
        String[] whereArgs = new String[]{"true"};
        return db.query(Constants.INCOMING_TABLE_NAME, columns, whereClause, whereArgs, null, null, Constants.SMS_MESSAGE_SERIAL + " DESC");
    }

    public Cursor getAllMessages() {
        if (db == null || !db.isOpen()) {
            openDB();
        }
        if (db == null) return null;
        String[] columns = {Constants.SMS_MESSAGE_SERIAL, Constants.SMS_ORIGINATING_ADDRESS, Constants.SMS_MESSAGE_BODY, Constants.SMS_TIMESTAMP, Constants.SMS_MESSAGE_READ_DATE, Constants.SMS_MESSAGE_SERVICE_CENTER, Constants.SMS_PROTOCOL_IDENTIFIER,
                Constants.SMS_MESSAGE_STATUS_ON_SIM, Constants.SMS_IS_STATUS_REPORT, Constants.SMS_IS_MESSAGE_SYNCHRONIZED,
                Constants.SMS_MESSAGE_PDU, Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, Constants.SMS_MESSAGE_USER_DATA, Constants.SMS_IS_MWI_MESSAGE};

        return db.query(Constants.INCOMING_TABLE_NAME, columns, null, null, null, null, Constants.SMS_MESSAGE_SERIAL + " DESC");
    }

    public Cursor getAllMessagesToSendToERP() {
        if (db == null || !db.isOpen()) {
            openDB();
        }
        if (db == null) return null;
        String[] columns = {Constants.SMS_MESSAGE_SERIAL, Constants.SMS_ORIGINATING_ADDRESS, Constants.SMS_MESSAGE_BODY, Constants.SMS_TIMESTAMP, Constants.SMS_MESSAGE_READ_DATE, Constants.SMS_MESSAGE_SERVICE_CENTER, Constants.SMS_PROTOCOL_IDENTIFIER,
                Constants.SMS_MESSAGE_STATUS_ON_SIM, Constants.SMS_IS_STATUS_REPORT, Constants.SMS_IS_MESSAGE_SYNCHRONIZED,
                Constants.SMS_MESSAGE_PDU, Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, Constants.SMS_MESSAGE_USER_DATA, Constants.SMS_IS_MWI_MESSAGE};
        String whereClause = Constants.SMS_IS_MESSAGE_SYNCHRONIZED + "=?";
        String[] whereArgs = new String[]{"0"};
        return db.query(Constants.INCOMING_TABLE_NAME, columns, whereClause, whereArgs, null, null, Constants.SMS_MESSAGE_SERIAL + " DESC");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save(SMSModelSMSdetails sms) {
        openDB();
        long result = add(sms);
        if (result > 0) {
            System.out.println("Message entered");
        } else {
            System.out.println("Message not entered");
        }
        closeDB();

        try {
            if (UnsynchedInboxFragment.instance() != null) {
                UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
            }
            if (SynchedInboxFragment.instance() != null) {
                SynchedInboxFragment.instance().refreshSmsMessagesInbox();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void FabRefreshInboxes() {
        try {
            if (UnsynchedInboxFragment.instance() != null) {
                UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
            }
            if (SynchedInboxFragment.instance() != null) {
                SynchedInboxFragment.instance().refreshSmsMessagesInbox();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<SMSModelSMSdetails> allDBUnsyncedMessages() {
        ArrayList<SMSModelSMSdetails> smsList = new ArrayList<>();
        openDB();
        Cursor c = getAllMessagesToSendToERP();
        if (c != null) {
            while (c.moveToNext()) {
                SMSModelSMSdetails sms = new SMSModelSMSdetails();
                sms.smsMessageSerial = c.getInt(c.getColumnIndexOrThrow(Constants.SMS_MESSAGE_SERIAL));
                sms.timeStamp = c.getString(c.getColumnIndexOrThrow(Constants.SMS_TIMESTAMP));
                sms.messageBody = c.getString(c.getColumnIndexOrThrow(Constants.SMS_MESSAGE_BODY));
                sms.originationAddress = c.getString(c.getColumnIndexOrThrow(Constants.SMS_ORIGINATING_ADDRESS));
                sms.messageStatusOnSim = c.getInt(c.getColumnIndexOrThrow(Constants.SMS_MESSAGE_STATUS_ON_SIM));
                sms.messagePDU = c.getString(c.getColumnIndexOrThrow(Constants.SMS_MESSAGE_PDU));
                sms.protocolIdentifier = c.getInt(c.getColumnIndexOrThrow(Constants.SMS_PROTOCOL_IDENTIFIER));
                sms.messageServiceCenter = c.getString(c.getColumnIndexOrThrow(Constants.SMS_MESSAGE_SERVICE_CENTER));
                sms.messageUserData = c.getString(c.getColumnIndexOrThrow(Constants.SMS_MESSAGE_USER_DATA));
                sms.isStatusReport = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(Constants.SMS_IS_STATUS_REPORT)));
                sms.isMWIMessage = Boolean.valueOf(c.getString(c.getColumnIndexOrThrow(Constants.SMS_IS_MWI_MESSAGE)));
                sms.messageReadDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(c.getLong(c.getColumnIndexOrThrow(Constants.SMS_MESSAGE_READ_DATE))), TimeZone.getDefault().toZoneId());

                if (!smsList.contains(sms)) {
                    smsList.add(sms);
                }
            }
            c.close();
        }
        closeDB();
        return smsList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<SMSModelSMSdetails> updateIncomingSMSisSynchronized(SMSModelSMSdetails sms) {
        openDB();
        if (db != null && db.isOpen()) {
            ContentValues cv = new ContentValues();
            cv.put(Constants.SMS_IS_MESSAGE_SYNCHRONIZED, "true");
            LocalDateTime ldt = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
            cv.put(Constants.SMS_MESSAGE_SYNCHRONIZED_DATE, ldt.format(dtf));
            db.update(Constants.INCOMING_TABLE_NAME, cv, Constants.SMS_MESSAGE_SERIAL + "=?", new String[]{String.valueOf(sms.smsMessageSerial)});
        }
        closeDB();
        return allDBUnsyncedMessages();
    }

    // --- C2B TRANSACTIONS METHODS ---
    public void saveC2BTransactions(List<C2BTransactionModel> transactions) {
        if (transactions == null || transactions.isEmpty()) return;
        openDB();
        if (db == null || !db.isOpen()) return;
        try {
            db.execSQL(Constants.CREATE_TABLE_C2B);
        } catch (Exception ignored) {}
        db.beginTransaction();
        try {
            for (C2BTransactionModel tx : transactions) {
                if (tx.id == null || tx.id.trim().isEmpty() || tx.id.equalsIgnoreCase("null")) {
                    tx.id = (tx.transId != null && !tx.transId.trim().isEmpty()) ? tx.transId.trim() : UUID.randomUUID().toString();
                }
                ContentValues cv = new ContentValues();
                cv.put(Constants.C2B_ID, tx.id);
                cv.put(Constants.C2B_TRANS_ID, tx.transId);
                cv.put(Constants.C2B_TRANS_TIME, tx.transTime);
                cv.put(Constants.C2B_AMOUNT, tx.amount);
                cv.put(Constants.C2B_BUSINESS_SHORTCODE, tx.businessShortcode);
                cv.put(Constants.C2B_BILL_REF_NUMBER, tx.billRefNumber);
                cv.put(Constants.C2B_MSISDN, tx.msisdn);
                cv.put(Constants.C2B_FIRST_NAME, tx.firstName);
                cv.put(Constants.C2B_MIDDLE_NAME, tx.middleName);
                cv.put(Constants.C2B_LAST_NAME, tx.lastName);
                cv.put(Constants.C2B_ACCOUNT_NAME, tx.getFullName());
                cv.put(Constants.C2B_EXTRA_NOTE, tx.extraNote);
                cv.put(Constants.C2B_EXTRA_CATEGORY, tx.extraCategory);
                cv.put(Constants.C2B_CREATED_AT, tx.createdAt);
                cv.put(Constants.C2B_TRANS_TIME_EAT, tx.transTimeEat);
                cv.put(Constants.C2B_TRANS_TIME_FORMATTED, tx.transTimeFormatted);

                db.insertWithOnConflict(Constants.C2B_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen() && db.inTransaction()) {
                db.endTransaction();
            }
            closeDB();
        }
    }

    public int updateC2BExtraNoteByAccount(String accountNumber, String extraNote, String extraCategory) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) return 0;
        openDB();
        if (db == null || !db.isOpen()) return 0;
        int count = 0;
        try {
            db.execSQL(Constants.CREATE_TABLE_C2B);
            ContentValues cv = new ContentValues();
            if (extraNote != null) cv.put(Constants.C2B_EXTRA_NOTE, extraNote.trim());
            if (extraCategory != null) cv.put(Constants.C2B_EXTRA_CATEGORY, extraCategory.trim());

            count = db.update(Constants.C2B_TABLE_NAME, cv, Constants.C2B_BILL_REF_NUMBER + " LIKE ?", new String[]{accountNumber.trim()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB();
        }
        return count;
    }

    public List<C2BTransactionModel> getAllC2BTransactions(String searchQuery) {
        return getAllC2BTransactions(searchQuery, null, null);
    }

    public List<C2BTransactionModel> getAllC2BTransactions(String searchQuery, String fromDate, String toDate) {
        List<C2BTransactionModel> list = new ArrayList<>();
        openDB();
        if (db == null || !db.isOpen()) return list;
        try {
            db.execSQL(Constants.CREATE_TABLE_C2B);
        } catch (Exception ignored) {}

        Cursor c = null;
        try {
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = "%" + searchQuery.trim() + "%";
                String whereClause = Constants.C2B_TRANS_ID + " LIKE ? OR "
                        + Constants.C2B_MSISDN + " LIKE ? OR "
                        + Constants.C2B_BILL_REF_NUMBER + " LIKE ? OR "
                        + Constants.C2B_FIRST_NAME + " LIKE ? OR "
                        + Constants.C2B_LAST_NAME + " LIKE ? OR "
                        + Constants.C2B_EXTRA_NOTE + " LIKE ?";
                String[] whereArgs = new String[]{query, query, query, query, query, query};
                c = db.query(Constants.C2B_TABLE_NAME, null, whereClause, whereArgs, null, null, Constants.C2B_CREATED_AT + " DESC");
            } else {
                c = db.query(Constants.C2B_TABLE_NAME, null, null, null, null, null, Constants.C2B_CREATED_AT + " DESC");
            }

            if (c != null) {
                while (c.moveToNext()) {
                    C2BTransactionModel tx = new C2BTransactionModel();
                    tx.id = c.getString(c.getColumnIndexOrThrow(Constants.C2B_ID));
                    tx.transId = c.getString(c.getColumnIndexOrThrow(Constants.C2B_TRANS_ID));
                    tx.transTime = c.getString(c.getColumnIndexOrThrow(Constants.C2B_TRANS_TIME));
                    tx.amount = c.getDouble(c.getColumnIndexOrThrow(Constants.C2B_AMOUNT));
                    tx.businessShortcode = c.getString(c.getColumnIndexOrThrow(Constants.C2B_BUSINESS_SHORTCODE));
                    tx.billRefNumber = c.getString(c.getColumnIndexOrThrow(Constants.C2B_BILL_REF_NUMBER));
                    tx.msisdn = c.getString(c.getColumnIndexOrThrow(Constants.C2B_MSISDN));
                    tx.firstName = c.getString(c.getColumnIndexOrThrow(Constants.C2B_FIRST_NAME));
                    tx.middleName = c.getString(c.getColumnIndexOrThrow(Constants.C2B_MIDDLE_NAME));
                    tx.lastName = c.getString(c.getColumnIndexOrThrow(Constants.C2B_LAST_NAME));
                    int idxNote = c.getColumnIndex(Constants.C2B_EXTRA_NOTE);
                    if (idxNote != -1) tx.extraNote = c.getString(idxNote);
                    int idxCat = c.getColumnIndex(Constants.C2B_EXTRA_CATEGORY);
                    if (idxCat != -1) tx.extraCategory = c.getString(idxCat);
                    tx.createdAt = c.getString(c.getColumnIndexOrThrow(Constants.C2B_CREATED_AT));
                    tx.transTimeEat = c.getString(c.getColumnIndexOrThrow(Constants.C2B_TRANS_TIME_EAT));
                    tx.transTimeFormatted = c.getString(c.getColumnIndexOrThrow(Constants.C2B_TRANS_TIME_FORMATTED));

                    String dateForFilter = tx.transTime != null && !tx.transTime.isEmpty() ? tx.transTime : tx.createdAt;
                    if (isDateInRange(dateForFilter, fromDate, toDate)) {
                        list.add(tx);
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB();
        }

        return list;
    }

    private void ensurePushedSmsTable(SQLiteDatabase db) {
        if (db == null) return;
        try {
            db.execSQL(Constants.CREATE_TABLE_PUSHED_SMS);
        } catch (Exception ignored) {}

        String[] cols = new String[] {
            Constants.PUSHED_SMS_SERVICE_CENTER,
            Constants.PUSHED_SMS_ACCOUNT_NAME,
            Constants.PUSHED_SMS_CREATED_AT,
            Constants.PUSHED_SMS_STATUS,
            Constants.PUSHED_SMS_STATUS_ON_SIM,
            Constants.PUSHED_SMS_PDU,
            Constants.PUSHED_SMS_PROTOCOL_IDENTIFIER,
            Constants.PUSHED_SMS_USER_DATA,
            Constants.PUSHED_SMS_IS_STATUS_REPORT,
            Constants.PUSHED_SMS_IS_MWI_MESSAGE,
            Constants.PUSHED_SMS_READ_DATE,
            Constants.PUSHED_SMS_IS_SYNCHRONIZED,
            Constants.PUSHED_SMS_SYNCHRONIZED_DATE,
            Constants.PUSHED_SMS_MODEM_NAME
        };

        for (String col : cols) {
            try {
                db.execSQL("ALTER TABLE " + Constants.PUSHED_SMS_TABLE_NAME + " ADD COLUMN " + col + " TEXT");
            } catch (Exception ignored) {}
        }
    }

    // --- PUSHED SMS TRANSACTIONS METHODS ---
    public void savePushedSmsTransactions(List<NodeSmsModel> transactions) {
        if (transactions == null || transactions.isEmpty()) return;
        openDB();
        if (db == null || !db.isOpen()) return;

        ensurePushedSmsTable(db);

        db.beginTransaction();
        try {
            for (NodeSmsModel sms : transactions) {
                if (sms == null) continue;

                if (sms.id == null || sms.id.trim().isEmpty() || sms.id.equals("0") || sms.id.equalsIgnoreCase("null")) {
                    sms.id = UUID.randomUUID().toString();
                }

                ContentValues cv = new ContentValues();
                cv.put(Constants.PUSHED_SMS_ID, sms.id);
                cv.put(Constants.PUSHED_SMS_SERIAL, sms.smsSerial);
                cv.put(Constants.PUSHED_SMS_SENDER, sms.sender != null ? sms.sender : "");
                cv.put(Constants.PUSHED_SMS_MESSAGE, sms.message != null ? sms.message : "");
                cv.put(Constants.PUSHED_SMS_TIMESTAMP, sms.timestamp != null ? sms.timestamp : "");
                cv.put(Constants.PUSHED_SMS_SERVICE_CENTER, sms.serviceCenter != null ? sms.serviceCenter : "");
                cv.put(Constants.PUSHED_SMS_CREATED_AT, sms.createdAt != null ? sms.createdAt : "");
                cv.put(Constants.PUSHED_SMS_STATUS, sms.messageStatus != null ? sms.messageStatus : "");
                cv.put(Constants.PUSHED_SMS_STATUS_ON_SIM, sms.messageStatusOnSim);
                cv.put(Constants.PUSHED_SMS_PDU, sms.messagePdu != null ? sms.messagePdu : "");
                cv.put(Constants.PUSHED_SMS_PROTOCOL_IDENTIFIER, sms.protocolIdentifier);
                cv.put(Constants.PUSHED_SMS_USER_DATA, sms.messageUserData != null ? sms.messageUserData : "");
                cv.put(Constants.PUSHED_SMS_IS_STATUS_REPORT, sms.isStatusReport ? 1 : 0);
                cv.put(Constants.PUSHED_SMS_IS_MWI_MESSAGE, sms.isMwiMessage ? 1 : 0);
                cv.put(Constants.PUSHED_SMS_READ_DATE, sms.messageReadDate != null ? sms.messageReadDate : "");
                cv.put(Constants.PUSHED_SMS_IS_SYNCHRONIZED, sms.isMessageSynchronized ? 1 : 0);
                cv.put(Constants.PUSHED_SMS_SYNCHRONIZED_DATE, sms.messageSynchronizedDate != null ? sms.messageSynchronizedDate : "");
                cv.put(Constants.PUSHED_SMS_MODEM_NAME, sms.modemName != null ? sms.modemName : "");
                cv.put(Constants.PUSHED_SMS_ACCOUNT_NAME, sms.getMpesaTransId());

                try {
                    db.insertWithOnConflict(Constants.PUSHED_SMS_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                } catch (Exception e) {
                    Log.e("DBHelper", "Error inserting single SMS row: " + e.getLocalizedMessage());
                }
            }
            db.setTransactionSuccessful();
            Log.d("DBHelper", "Successfully saved " + transactions.size() + " SMS transactions into SQLite.");
        } catch (Exception e) {
            Log.e("DBHelper", "Error in savePushedSmsTransactions batch", e);
        } finally {
            if (db != null && db.isOpen() && db.inTransaction()) {
                db.endTransaction();
            }
            closeDB();
        }
    }

    public List<NodeSmsModel> getAllPushedSmsTransactions(String searchQuery) {
        return getAllPushedSmsTransactions(searchQuery, null, null);
    }

    public List<NodeSmsModel> getAllPushedSmsTransactions(String searchQuery, String fromDate, String toDate) {
        List<NodeSmsModel> list = new ArrayList<>();
        openDB();
        if (db == null || !db.isOpen()) return list;

        ensurePushedSmsTable(db);

        Cursor c = null;
        try {
            String orderBy = "ROWID DESC";
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = "%" + searchQuery.trim() + "%";
                String whereClause = Constants.PUSHED_SMS_SENDER + " LIKE ? OR "
                        + Constants.PUSHED_SMS_MESSAGE + " LIKE ? OR "
                        + Constants.PUSHED_SMS_TIMESTAMP + " LIKE ? OR "
                        + Constants.PUSHED_SMS_ACCOUNT_NAME + " LIKE ?";
                String[] whereArgs = new String[]{query, query, query, query};
                c = db.query(Constants.PUSHED_SMS_TABLE_NAME, null, whereClause, whereArgs, null, null, orderBy);
            } else {
                c = db.query(Constants.PUSHED_SMS_TABLE_NAME, null, null, null, null, null, orderBy);
            }

            if (c != null) {
                int idxId = c.getColumnIndex(Constants.PUSHED_SMS_ID);
                int idxSerial = c.getColumnIndex(Constants.PUSHED_SMS_SERIAL);
                int idxSender = c.getColumnIndex(Constants.PUSHED_SMS_SENDER);
                int idxMessage = c.getColumnIndex(Constants.PUSHED_SMS_MESSAGE);
                int idxTimestamp = c.getColumnIndex(Constants.PUSHED_SMS_TIMESTAMP);
                int idxCenter = c.getColumnIndex(Constants.PUSHED_SMS_SERVICE_CENTER);
                int idxCreatedAt = c.getColumnIndex(Constants.PUSHED_SMS_CREATED_AT);
                int idxStatus = c.getColumnIndex(Constants.PUSHED_SMS_STATUS);
                int idxSim = c.getColumnIndex(Constants.PUSHED_SMS_STATUS_ON_SIM);
                int idxPdu = c.getColumnIndex(Constants.PUSHED_SMS_PDU);
                int idxProto = c.getColumnIndex(Constants.PUSHED_SMS_PROTOCOL_IDENTIFIER);
                int idxUserData = c.getColumnIndex(Constants.PUSHED_SMS_USER_DATA);
                int idxReadDate = c.getColumnIndex(Constants.PUSHED_SMS_READ_DATE);
                int idxModem = c.getColumnIndex(Constants.PUSHED_SMS_MODEM_NAME);
                int idxAccount = c.getColumnIndex(Constants.PUSHED_SMS_ACCOUNT_NAME);

                while (c.moveToNext()) {
                    NodeSmsModel sms = new NodeSmsModel();
                    if (idxId != -1) sms.id = c.getString(idxId);
                    if (idxSerial != -1) sms.smsSerial = c.getInt(idxSerial);
                    if (idxSender != -1) sms.sender = c.getString(idxSender);
                    if (idxMessage != -1) sms.message = c.getString(idxMessage);
                    if (idxTimestamp != -1) sms.timestamp = c.getString(idxTimestamp);
                    if (idxCenter != -1) sms.serviceCenter = c.getString(idxCenter);
                    if (idxCreatedAt != -1) sms.createdAt = c.getString(idxCreatedAt);
                    if (idxStatus != -1) sms.messageStatus = c.getString(idxStatus);
                    if (idxSim != -1) sms.messageStatusOnSim = c.getInt(idxSim);
                    if (idxPdu != -1) sms.messagePdu = c.getString(idxPdu);
                    if (idxProto != -1) sms.protocolIdentifier = c.getInt(idxProto);
                    if (idxUserData != -1) sms.messageUserData = c.getString(idxUserData);
                    if (idxReadDate != -1) sms.messageReadDate = c.getString(idxReadDate);
                    if (idxModem != -1) sms.modemName = c.getString(idxModem);
                    if (idxAccount != -1) sms.accountName = c.getString(idxAccount);

                    String dateForFilter = sms.timestamp != null && !sms.timestamp.isEmpty() ? sms.timestamp : sms.createdAt;
                    if (isDateInRange(dateForFilter, fromDate, toDate)) {
                        list.add(sms);
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDB();
        }

        return list;
    }

    private boolean isDateInRange(String dateStr, String fromDate, String toDate) {
        if ((fromDate == null || fromDate.trim().isEmpty()) && (toDate == null || toDate.trim().isEmpty())) {
            return true;
        }
        if (dateStr == null || dateStr.trim().isEmpty()) return true;

        String cleaned = dateStr.trim();
        if (cleaned.length() >= 10 && cleaned.charAt(4) == '-' && cleaned.charAt(7) == '-') {
            cleaned = cleaned.substring(0, 10);
        }

        if (fromDate != null && !fromDate.trim().isEmpty()) {
            if (cleaned.compareTo(fromDate.trim()) < 0) {
                return false;
            }
        }

        if (toDate != null && !toDate.trim().isEmpty()) {
            if (cleaned.compareTo(toDate.trim()) > 0) {
                return false;
            }
        }

        return true;
    }
}
