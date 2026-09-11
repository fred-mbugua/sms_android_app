package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C2BTransactionModel {

    public String id = "";
    public String transId = "";
    public String transTime = "";
    public double amount = 0.0;
    public String businessShortcode = "";
    public String billRefNumber = "";
    public String msisdn = "";
    public String firstName = "";
    public String middleName = "";
    public String lastName = "";
    public String extraNote = "";
    public String extraCategory = "";
    public String createdAt = "";
    public String transTimeEat = "";
    public String transTimeFormatted = "";

    public C2BTransactionModel() {
    }

    public C2BTransactionModel(String id, String transId, String transTime, double amount,
                               String businessShortcode, String billRefNumber, String msisdn,
                               String firstName, String middleName, String lastName,
                               String createdAt, String transTimeEat, String transTimeFormatted) {
        this.id = id;
        this.transId = transId;
        this.transTime = transTime;
        this.amount = amount;
        this.businessShortcode = businessShortcode;
        this.billRefNumber = billRefNumber;
        this.msisdn = msisdn;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.transTimeEat = transTimeEat;
        this.transTimeFormatted = transTimeFormatted;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty() && !firstName.equalsIgnoreCase("null")) {
            sb.append(firstName.trim()).append(" ");
        }
        if (middleName != null && !middleName.trim().isEmpty() && !middleName.equalsIgnoreCase("null")) {
            sb.append(middleName.trim()).append(" ");
        }
        if (lastName != null && !lastName.trim().isEmpty() && !lastName.equalsIgnoreCase("null")) {
            sb.append(lastName.trim());
        }
        String name = sb.toString().trim();
        return name.isEmpty() ? msisdn : name;
    }

    public static C2BTransactionModel fromIncomingSms(SMSModelSMSdetails sms) {
        if (sms == null || sms.messageBody == null || sms.messageBody.trim().isEmpty()) return null;
        String body = sms.messageBody.trim();

        if (!body.toLowerCase().contains("ksh") && !body.toLowerCase().contains("confirmed") && !body.toLowerCase().contains("received")) {
            return null;
        }

        C2BTransactionModel tx = new C2BTransactionModel();
        tx.id = String.valueOf(sms.smsMessageSerial);

        // Extract Trans ID (e.g. RBA1234567)
        Pattern codePattern = Pattern.compile("([A-Z0-9]{10})");
        Matcher codeMatcher = codePattern.matcher(body);
        if (codeMatcher.find()) {
            tx.transId = codeMatcher.group(1);
        } else {
            tx.transId = "SMS-" + sms.smsMessageSerial;
        }

        // Extract Amount (e.g. Ksh1,150.00 or Ksh 1150)
        Pattern amountPattern = Pattern.compile("Ksh\\.?\\s*([0-9,]+(\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE);
        Matcher amountMatcher = amountPattern.matcher(body);
        if (amountMatcher.find()) {
            try {
                String matchGroup = amountMatcher.group(1);
                if (matchGroup != null) {
                    tx.amount = Double.parseDouble(matchGroup.replace(",", ""));
                }
            } catch (Exception ignored) {
                tx.amount = 0.0;
            }
        }

        // Extract Phone Number
        Pattern phonePattern = Pattern.compile("(07\\d{8}|01\\d{8}|2547\\d{8}|2541\\d{8}|\\+2547\\d{8}|\\+2541\\d{8})");
        Matcher phoneMatcher = phonePattern.matcher(body);
        if (phoneMatcher.find()) {
            tx.msisdn = phoneMatcher.group(1);
        } else {
            tx.msisdn = sms.originationAddress != null ? sms.originationAddress : "";
        }

        // Extract Name (e.g. from JOHN DOE)
        Pattern namePattern = Pattern.compile("from\\s+([A-Za-z0-9\\s]+?)\\s+(07|01|254|\\+254|on|at|\\d{1,2}/)", Pattern.CASE_INSENSITIVE);
        Matcher nameMatcher = namePattern.matcher(body);
        if (nameMatcher.find()) {
            String nameGroup = nameMatcher.group(1);
            if (nameGroup != null) {
                tx.firstName = nameGroup.trim();
            }
        } else {
            tx.firstName = tx.msisdn;
        }

        // Extract Bill Ref / Account
        Pattern refPattern = Pattern.compile("(?:Acc|Account|Ref)\\.?(?:\\s*No\\.?)?\\s*([A-Za-z0-9_-]+)", Pattern.CASE_INSENSITIVE);
        Matcher refMatcher = refPattern.matcher(body);
        if (refMatcher.find()) {
            String refGroup = refMatcher.group(1);
            if (refGroup != null) {
                tx.billRefNumber = refGroup.trim();
            }
        }

        tx.transTime = sms.timeStamp != null ? sms.timeStamp : "";
        tx.transTimeFormatted = sms.timeStamp != null ? sms.timeStamp : "";
        tx.createdAt = String.valueOf(sms.messageReadDate);

        return tx;
    }
}
