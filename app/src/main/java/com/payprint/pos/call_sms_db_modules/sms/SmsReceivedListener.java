package com.payprint.pos.call_sms_db_modules.sms;

import java.time.LocalDateTime;

/**
 * edited by Fred.
 */

public interface SmsReceivedListener {
    void onMessageReceived(String senderNumber, LocalDateTime smsReceivedDate, String senderSendTime, Object[] pdus, String smsMessageContent, String srviceCenterAddress, Boolean isStatusReportMessage, Integer protocolIdentifier, Integer statusOnIcc, Boolean isCphsMwiMessage, Boolean isMessageSynchronized, LocalDateTime messageSynchronisedDate);

}
