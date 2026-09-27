package com.payprint.pos.call_sms_db_modules.sms;

/**
 * edited by Fred.
 */

public interface SmsSentListener {
    void onMessageSent(String number, String contactName, String messageText, String currentTime);
}
