package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database;

public class CompareTransactionModel {

    public enum MatchStatus {
        MATCHED,
        AMOUNT_MISMATCH,
        C2B_ONLY,
        SMS_ONLY
    }

    public String transCode = "";
    public MatchStatus status = MatchStatus.C2B_ONLY;

    // C2B Fields
    public C2BTransactionModel c2bRecord;

    // SMS Fields
    public NodeSmsModel smsRecord;

    public CompareTransactionModel() {
    }

    public CompareTransactionModel(String transCode, MatchStatus status, C2BTransactionModel c2bRecord, NodeSmsModel smsRecord) {
        this.transCode = transCode;
        this.status = status;
        this.c2bRecord = c2bRecord;
        this.smsRecord = smsRecord;
    }
}
