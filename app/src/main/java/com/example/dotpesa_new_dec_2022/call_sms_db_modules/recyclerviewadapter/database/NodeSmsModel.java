package com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeSmsModel {

    public String id = "";
    public int smsSerial = 0;
    public String sender = "";
    public String message = "";
    public String timestamp = "";
    public String serviceCenter = "";
    public String createdAt = "";
    public String messageStatus = "";
    public int messageStatusOnSim = 0;
    public String messagePdu = "";
    public int protocolIdentifier = 0;
    public String messageUserData = "";
    public boolean isStatusReport = false;
    public boolean isMwiMessage = false;
    public String messageReadDate = "";
    public boolean isMessageSynchronized = false;
    public String messageSynchronizedDate = "";
    public String modemName = "";

    public NodeSmsModel() {
    }

    public NodeSmsModel(String id, int smsSerial, String sender, String message, String timestamp, String serviceCenter, String createdAt) {
        this.id = id;
        this.smsSerial = smsSerial;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.serviceCenter = serviceCenter;
        this.createdAt = createdAt;
    }

    public String getMpesaTransId() {
        if (message == null || message.trim().isEmpty()) return "";
        Pattern pattern = Pattern.compile("([A-Z0-9]{10})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public double getExtractedAmount() {
        if (message == null || message.trim().isEmpty()) return 0.0;
        try {
            Pattern pattern = Pattern.compile("Ksh\\.?\\s*([0-9,]+(\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String matchGroup = matcher.group(1);
                if (matchGroup != null) {
                    String amountStr = matchGroup.replace(",", "");
                    return Double.parseDouble(amountStr);
                }
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }
}
