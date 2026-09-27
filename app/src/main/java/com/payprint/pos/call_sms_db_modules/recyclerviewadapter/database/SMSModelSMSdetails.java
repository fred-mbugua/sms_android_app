package com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database;

import com.payprint.pos.call_sms_db_modules.recyclerviewadapter.MessagesModel;

import java.time.LocalDateTime;

public class SMSModelSMSdetails {
    public Integer smsMessageSerial;
    public String timeStamp;
    public String messageBody = "";
    public String messageDisplayBody = "";
    public String originationAddress="";
    public String originatingDisplayAddress = "";
    public String accountName = "";
    public Integer messageStatus;
    public Integer messageStatusOnSim ;
    public String messagePDU ;
    public Integer protocolIdentifier ;
    public String messageServiceCenter;
    public String messageUserData ;
    public Boolean isStatusReport = Boolean.FALSE;
    public Boolean isMWIMessage  = Boolean.FALSE;
    public Boolean isMessageSynchronised  = Boolean.FALSE;
    public LocalDateTime messageSynchronisedDate;
    public LocalDateTime messageReadDate;

    public Boolean isSMSIsRead = Boolean.FALSE;

    public SMSModelSMSdetails(){

    }

    public SMSModelSMSdetails(MessagesModel sms){
        smsMessageSerial = null;
        timeStamp  =  sms.getSenderSendTime();
        messageBody = sms.getReceivedMessageBody();
        originationAddress = sms.getSenderAddress();
        accountName = C2BTransactionModel.extractAccountNameFromSms(sms.getReceivedMessageBody());
        messageStatus = sms.getStatusOnIcc();
        messageStatusOnSim = sms.getStatusOnIcc();
        messagePDU = sms.getpduDataString();
        protocolIdentifier = sms.getProtocolIdentifier();
        messageServiceCenter = sms.getServiceCenterAddress();
        isStatusReport = sms.getIsStatusReportMessage();
        isMWIMessage  = sms.getIsCphsMwiMessage();
        messageReadDate = sms.getSmsReceivedDate();
        isMessageSynchronised=sms.getIsMessageSynchronised();
        messageSynchronisedDate = sms.getMessageSynchronisedDate();

    }
}
