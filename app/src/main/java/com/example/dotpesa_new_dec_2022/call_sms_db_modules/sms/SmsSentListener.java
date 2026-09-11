package com.example.dotpesa_new_dec_2022.call_sms_db_modules.sms;

/**
 * edited by Fred.
 */

public interface SmsSentListener {
    void onMessageSent(String number, String contactName, String messageText, String currentTime);
}
