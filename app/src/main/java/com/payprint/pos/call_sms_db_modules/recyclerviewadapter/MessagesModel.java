package com.payprint.pos.call_sms_db_modules.recyclerviewadapter;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

public class MessagesModel {

    private String  senderAddress;
    private String receivedMessageBody;
    private LocalDateTime smsReceivedDate;
    private String senderSendTime;
    private String serviceCenterAddress;
    private Integer receivedSMSSerial, protocolIdentifier, statusOnIcc;
    private Boolean isStatusReportMessage, isCphsMwiMessage;
    private Boolean isMessageSynchronised = Boolean.FALSE;
    private Object pduData;
    LocalDateTime messageSynchronisedDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MessagesModel(String senderAddress, LocalDateTime smsReceivedDate, String senderSendTime, Object[] pduData, String smsMessageContent, String serviceCenterAddress, Boolean isStatusReportMessage, Integer protocolIdentifier, Integer statusOnIcc, Boolean isCphsMwiMessage, Boolean isMessageSynchronized, LocalDateTime messageSynchronisedDate){
//    public MessagesModel(Integer receivedSMSSerial, String senderAddress, String receivedMessageBody, String senderSendTime, String smsReceivedDate,
//                         String serviceCenterAddress, Integer protocolIdentifier, Integer statusOnIcc, Boolean isStatusReportMessage,
//                         Boolean isMessageSynchronised, Object [] pduData, LocalDateTime messageSynchronisedDate){
//        this.receivedSMSSerial = receivedSMSSerial;
        this.senderAddress = senderAddress;
        this.receivedMessageBody = smsMessageContent;
        this.senderSendTime = senderSendTime;
        this.smsReceivedDate = smsReceivedDate;
        this.serviceCenterAddress = serviceCenterAddress;
        this.protocolIdentifier = protocolIdentifier;
        this.statusOnIcc = statusOnIcc;
        this.isStatusReportMessage = isStatusReportMessage;
        this.isMessageSynchronised = isMessageSynchronized;
        this.pduData = pduData;
        this.messageSynchronisedDate = messageSynchronisedDate;
        this.isCphsMwiMessage = isCphsMwiMessage;
    }

    public Integer getReceivedSMSSerial(){
        return receivedSMSSerial;
    }
    public void setReceivedSMSSerial(Integer receivedSMSSerial){
        this.receivedSMSSerial = receivedSMSSerial;
    }

    public String getSenderAddress(){
        return senderAddress;
    }
    public void setSenderAddress(String senderAddress){
        this.senderAddress = senderAddress;
    }

    public String getReceivedMessageBody(){
        return receivedMessageBody;
    }
    public void setReceivedMessageBody(String receivedMessageBody){
        this.receivedMessageBody = receivedMessageBody;
    }

    public String getSenderSendTime(){
        return senderSendTime;
    }
    public void setSenderSendTime(String senderSendTime){
        this.senderSendTime = senderSendTime;
    }

    public LocalDateTime getSmsReceivedDate(){
        return smsReceivedDate;
    }
    public void setSmsReceivedDate(LocalDateTime smsReceivedDate){
        this.smsReceivedDate = smsReceivedDate;
    }

    public String getServiceCenterAddress(){
        return serviceCenterAddress;
    }
    public void setServiceCenterAddress(String serviceCenterAddress){
        this.serviceCenterAddress = serviceCenterAddress;
    }

    public Integer getProtocolIdentifier(){
        return protocolIdentifier;
    }
    public void setProtocolIdentifier(Integer protocolIdentifier){
        this.protocolIdentifier = protocolIdentifier;
    }

    public Integer getStatusOnIcc(){
        return statusOnIcc;
    }
    public void setStatusOnIcc(Integer statusOnIcc){
        this.statusOnIcc = statusOnIcc;
    }

    public Boolean getIsStatusReportMessage(){
        return isStatusReportMessage;
    }
    public void setIsStatusReportMessage(Boolean isStatusReportMessage){
        this.isStatusReportMessage = isStatusReportMessage;
    }

    public Boolean getIsCphsMwiMessage(){
        return isCphsMwiMessage;
    }
    public void setIsCphsMwiMessage(Boolean isCphsMwiMessage){
        this.isCphsMwiMessage = isCphsMwiMessage;
    }

    public Boolean getIsMessageSynchronised(){
        return isMessageSynchronised;
    }
    public void setIsMessageSynchronised(Boolean isMessageSynchronised){
        this.isMessageSynchronised = isMessageSynchronised;
    }

    public String getpduDataString(){
        String pduDataString = String.valueOf(pduData);
        return pduDataString;
    }
    public void setPduData(String pduDataString){
        this.pduData = pduDataString;
    }

    public LocalDateTime getMessageSynchronisedDate(){
        return messageSynchronisedDate;
    }
    public void setMessageSynchronisedDate(LocalDateTime messageSynchronisedDate){
        this.messageSynchronisedDate = messageSynchronisedDate;
    }

}
