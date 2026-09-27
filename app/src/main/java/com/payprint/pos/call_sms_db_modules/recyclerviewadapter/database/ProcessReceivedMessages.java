package com.payprint.pos.call_sms_db_modules.recyclerviewadapter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.payprint.pos.MainActivity;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ProcessReceivedMessages {
    String urlPage = MainActivity.APPLICATION_LOCAL_SERVER_PORTAL_PAGE + "?action=incoming&apiKey=" + MainActivity.DEVICE_APPLIANCE_API_KEY + "&applianceNumber=" + MainActivity.DEVICE_APPLIANCE_NUMBER.replaceAll(" ","+") + "";

    Context activeContext ;
    View activeTitleView;

    Boolean isPushProcessOngoing = Boolean.FALSE;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ProcessReceivedMessages() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void messageReceived(Context context, View titleView){
        activeContext = context;
        activeTitleView = titleView;
        Thread thread = new Thread(new PushSMSToLocalERPThread());
        thread.start();
        SQLiteDatabase m;
    }

    public class PushSMSToLocalERPThread implements  Runnable{

        public PushSMSToLocalERPThread(){

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            try {

                DateTimeFormatter dtF = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

//                for (int i = 0; i<MainActivity.INBOX_LAST_MESSAGE_LIST.size(); i++) {
//                    SMSMessageDetails sms = MainActivity.INBOX_LAST_MESSAGE_LIST.get(i);
                for (int i = 0; i<MainActivity.INBOX_MESSAGE_LIST.size(); i++) {
                    SMSModelSMSdetails sms = MainActivity.INBOX_MESSAGE_LIST.get(i);



                    HashMap<String, String> hashmap = new HashMap<String, String>();
                    if(!sms.isMessageSynchronised) {

                        if (!MainActivity.APPLICATION_LOCAL_SERVER_LIMIT_PUSH_RECEIVED ||
                                (MainActivity.APPLICATION_LOCAL_SERVER_LIMIT_PUSH_RECEIVED && MainActivity.APPLICATION_LOCAL_SERVER_LIMIT_PUSH_ONLY_RECEIVED_FROM.equalsIgnoreCase(sms.originationAddress))) {
                            System.out.println("testing 12345");
                            hashmap.put("SMSID", String.valueOf(sms.smsMessageSerial));

                            String messageReceivedDateTime = sms.timeStamp.format(String.valueOf(dtF));

                            String messageSentDateTime = sms.messageReadDate.format(dtF);

                            String senderMobilePhoneNumber = sms.originationAddress;
                            String SMSMessage = sms.messageBody;

                            hashmap.put("SMSMessage", SMSMessage);
                            hashmap.put("senderMobilePhoneNumber", senderMobilePhoneNumber);
                            hashmap.put("SMSSentDate", messageSentDateTime);
                            hashmap.put("SMSReceivedDate", messageReceivedDateTime);
                            hashmap.put("SMSCNumber", sms.messageServiceCenter);

                            hashmap.put("PDUUserData", sms.messagePDU);
                            hashmap.put("PDUUserHeader", sms.messageUserData);

                            String mesageType = sms.isStatusReport ? "Status" : "Normal";
                            hashmap.put("SMSMessageType", mesageType);

                            hashmap.put("SMSModemName", MainActivity.APPLICATION_LOCAL_SERVER_PORTAL_MODEM_NAME);

                            if (!MainActivity.APPLICATION_LOCAL_SERVER_PORTAL_MODEM_NAME.isEmpty()) {

                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // ServerCenterRequestsTool post = new ServerCenterRequestsTool();
                                        // post.onAttach(activeContext);
                                        // System.out.println("Getting ,,,");
                                        // post.pushSMSToERPAttempt(urlPage, hashmap, sms);

                                    }
                                });

                            } else {
                                //
                                Toast.makeText(activeContext, "Make sure the POS is reachable", Toast.LENGTH_SHORT).show();
                            }
                            //print the sms
                            if (MainActivity.APPLICATION_LOCAL_SERVER_PRINT_MESSAGE_ON_PUSH) {
                                if (MainActivity.APPLICATION_LOCAL_SERVER_LIMIT_PUSH_ONLY_RECEIVED_FROM.equalsIgnoreCase(senderMobilePhoneNumber)) {
                                    //SMSMessage, senderMobilePhoneNumber, messageSentDateTime, SMSReceivedDate

                                }
                            }
                            Thread.sleep(1500);
                        }


                    }
                }

//                Intent intent = new Intent (activeContext, Home_Page.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                activeContext.startActivity(intent);

            }catch(IllegalArgumentException cwe){
//               Toast.makeText(MainActivity.instance(), "Invalid request when sending SMS to server."+cwe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }catch(Exception cwe){
//               Toast.makeText(MainActivity.instance(), "General request error when sending SMS to server."+cwe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
