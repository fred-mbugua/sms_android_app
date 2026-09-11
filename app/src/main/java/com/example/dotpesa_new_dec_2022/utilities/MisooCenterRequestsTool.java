package com.example.dotpesa_new_dec_2022.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dotpesa_new_dec_2022.Appfunctionality;
import com.example.dotpesa_new_dec_2022.FragmentLicenseSettings;
import com.example.dotpesa_new_dec_2022.FragmentNeConfig;
import com.example.dotpesa_new_dec_2022.UnsynchedInboxFragment;
import com.example.dotpesa_new_dec_2022.Login;
import com.example.dotpesa_new_dec_2022.MainActivity;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.SMSModelSMSdetails;
import com.google.android.material.snackbar.Snackbar;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MisooCenterRequestsTool extends AppCompatActivity {
    Context loginContext;
    public static Login login;
    public static FragmentLicenseSettings fragmentLicenseSettings;


    public void onAttach(Context context){
        loginContext = context;
    }

    public MisooCenterRequestsTool(){

    }

    public  void loginAttempt(String urlPage,  HashMap<String, String> hashmap) {
        LoginToMisooCenter  login=  new LoginToMisooCenter(urlPage,  hashmap);
        Thread loginThread= new Thread(login);
        loginThread.start();
    }

    public  void getLicenceAttempt(String urlPage, HashMap<String, String> hashmap) {
        MisooCenterGetLicence  login=  new MisooCenterGetLicence(urlPage,  hashmap);
        Thread loginThread= new Thread(login);
        loginThread.start();
    }

    public  void getApplianceAttempt(String urlPage, HashMap<String, String> hashmap) {
        MisooCenterGetAppliance  login=  new MisooCenterGetAppliance(urlPage,  hashmap);
        Thread loginThread= new Thread(login);
        loginThread.start();
    }

    public  void pushSMSToERPAttempt(String urlPage, HashMap<String, String> hashmap, SMSModelSMSdetails sms) {
        LocalMisooPushSMSApplication  pushSMS =  new  LocalMisooPushSMSApplication(urlPage,  hashmap,  sms);
        Thread loginThread= new Thread(pushSMS);
        loginThread.start();
    }

    //pushing messages after clicking the fab button
    public  void fabPressPushSMSToERPAttempt(String urlPage, HashMap<String, String> hashmap, SMSModelSMSdetails sms) {
        FabButtonLocalMisooPushSMSApplication  pushSMS =  new  FabButtonLocalMisooPushSMSApplication(urlPage,  hashmap,  sms);
        Thread loginThread= new Thread(pushSMS);
        loginThread.start();
    }

    public  void getWorkstationAttempt(String urlPage, HashMap<String, String> hashmap, FragmentNeConfig nf) {
        LocalMisooGetWorkStations  workstationThread=  new LocalMisooGetWorkStations(urlPage,  hashmap, nf);
        Thread loginThread= new Thread(workstationThread);
        loginThread.start();
    }

    public  void getModemsAttempt(String urlPage, HashMap<String, String> hashmap, FragmentNeConfig nf) {
        LocalMisooGetModems  modemsThread=  new LocalMisooGetModems(urlPage,  hashmap,nf);
        Thread loginThread= new Thread(modemsThread);
        loginThread.start();
    }

//    public  void getOutgoingSMSListAttempt(String urlPage, HashMap<String, String> hashmap) {
//        LocalMisooGetOutgoingList  listSMSThread=  new LocalMisooGetOutgoingList(urlPage,  hashmap);
//        Thread loginThread= new Thread(listSMSThread);
//        loginThread.start();
//    }
//
//    public  void getOutgoingQueuedSMSAttempt(String urlPage, HashMap<String, String> hashmap) {
//        LocalMisooGetOutgoingQueued  queuedSMSThread=  new LocalMisooGetOutgoingQueued(urlPage,  hashmap);
//        Thread loginThread= new Thread(queuedSMSThread);
//        loginThread.start();
//    }





    private class LoginToMisooCenter implements Runnable {
        String threadUrlPage;
        HashMap<String, String> threadHashmap;
        CloseableHttpResponse response = null;


        public  LoginToMisooCenter(String urlPage, HashMap<String, String> hashmap){
            threadUrlPage=urlPage;
            threadHashmap =hashmap;
        }

        @Override
        public void run() {

            try {

                FrameworkResponse fr = new HttpPOSTClient().getMisooCenterPostResponse(threadUrlPage, threadHashmap);
                if (fr.getResponseSuccess()) {
                    System.out.println("resp");
                    response = (CloseableHttpResponse) fr.getResponseObject();
                    HttpEntity httpEntity = response.getEntity();

                    String responseStatus = "";
                    String responseStatusMessage = "";

                    if (httpEntity != null) {
                        try {
                            String jsonString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                            JSONObject myResponse = new JSONObject(jsonString);

                            Iterator key = myResponse.keys();
                            while (key.hasNext()) {
                                String k = key.next().toString();

                                if (k.equalsIgnoreCase("Response")) {
                                    responseStatus = myResponse.getString("Response");
                                } else if (k.equalsIgnoreCase("Message")) {
                                    responseStatusMessage = myResponse.getString("Message");
                                } else if (k.equalsIgnoreCase("online_customer_serial")) {
                                    MainActivity.LICENCE_CUSTOMER_SERIAL = myResponse.getString("online_customer_serial");
                                } else if (k.equalsIgnoreCase("login_user_name")) {
                                    MainActivity.LICENCE_CUSTOMER_USER_NAME = myResponse.getString("login_user_name");
                                } else if (k.equalsIgnoreCase("login_user_password")) {
                                    MainActivity.LICENCE_CUSTOMER_USER_PASSWORD = myResponse.getString("login_user_password");
                                } else if (k.equalsIgnoreCase("company_name")) {
                                    MainActivity.LICENCE_CUSTOMER_COMPANY_NAME = myResponse.getString("company_name");
                                } else if (k.equalsIgnoreCase("email_address")) {
                                    MainActivity.LICENCE_CUSTOMER_EMAIL_ADDRESS = myResponse.getString("email_address");
                                } else if (k.equalsIgnoreCase("mobile_office")) {
                                    MainActivity.LICENCE_CUSTOMER_MOBILE_NUMBER = myResponse.getString("mobile_office");
                                }
                            }

                            if (responseStatus.equalsIgnoreCase("Success")) {
                                SharedPreferences.Editor editor = MainActivity.SHAREDPREFERENCES.edit();

                                editor.putString("online_customer_serial", MainActivity.LICENCE_CUSTOMER_SERIAL);
                                editor.putString("login_user_name", MainActivity.LICENCE_CUSTOMER_USER_NAME);
                                editor.putString("login_user_password", MainActivity.LICENCE_CUSTOMER_USER_PASSWORD);
                                editor.putString("company_name", MainActivity.LICENCE_CUSTOMER_COMPANY_NAME);
                                editor.putString("email_address", MainActivity.LICENCE_CUSTOMER_EMAIL_ADDRESS);
                                editor.putString("mobile_office", MainActivity.LICENCE_CUSTOMER_MOBILE_NUMBER);


                                if (Login.remememberMe.isChecked()) {
                                    editor.putString("login_remember_me", "true");
                                    editor.commit();
                                } else {
                                    editor.putString("login_remember_me", "false");
                                    editor.commit();
                                }


                                Intent intent = new Intent(loginContext, Appfunctionality.class);
                                loginContext.startActivity(intent);
                                finish();

                            } else {
                                Snackbar.make(login.parentLayout, "Email Or Password is Incorrect. " + responseStatusMessage, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                System.out.println("Failed for not known ");
                                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                            }
                        } catch (JSONException | IOException je) {
                            je.printStackTrace();
                            System.out.println("Success JSON ");
                            Snackbar.make(login.parentLayout, "Response from authentication server unknown.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        System.out.println("Success for else");
                        Snackbar.make(login.parentLayout, "Connection Error, please try again.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                } else {
                    System.out.println("Maneneo");
                    Snackbar.make(login.parentLayout, "Login Error. "+fr.getResponseMessages(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }catch(Exception ioe) {
                System.out.println("Maneneo");
                Snackbar.make(login.parentLayout, "General Error, please try again."+ioe.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }

        public CloseableHttpResponse getResponse(){
            return response;
        }
    }

    //Licence
    private class MisooCenterGetLicence implements Runnable {
        String threadUrlPage;
        HashMap<String, String> threadHashmap;
        CloseableHttpResponse response = null;


        public  MisooCenterGetLicence(String urlPage, HashMap<String, String> hashmap){
            threadUrlPage=urlPage;
            threadHashmap =hashmap;
        }

        @Override
        public void run() {

            try {

                FrameworkResponse fr  = new HttpPOSTClient().getMisooCenterPostResponse(threadUrlPage, threadHashmap);
                if(fr.getResponseSuccess()) {
                    response = (CloseableHttpResponse)fr.getResponseObject();
                    HttpEntity httpEntity = response.getEntity();

                    String responseStatus = "";
                    String responseStatusMessage = "";

                    if (httpEntity != null) {
                        try {
                            String jsonString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                            JSONObject myResponse = new JSONObject(jsonString);

                            String licenceSerial = "";
                            String licenceKey = "";
                            String licenceStartDate = "";
                            String licenceDndDate = "";

                            Iterator key = myResponse.keys();
                            while (key.hasNext()) {
                                String k = key.next().toString();
                                if (k.equalsIgnoreCase("Response")) {
                                    responseStatus = myResponse.getString("Response");
                                } else if (k.equalsIgnoreCase("Message")) {
                                    responseStatusMessage = myResponse.getString("Message");
                                } else if (k.equalsIgnoreCase("licence_serial")) {
                                    MainActivity.LICENCE_SERIAL = myResponse.getString("licence_serial");
                                } else if (k.equalsIgnoreCase("licence_key")) {
                                    MainActivity.LICENCE_KEY = myResponse.getString("licence_key");
                                } else if (k.equalsIgnoreCase("licence_start_date")) {
                                    MainActivity.LICENCE_START_DATE = myResponse.getString("licence_start_date");
                                } else if (k.equalsIgnoreCase("licence_end_date")) {
                                    MainActivity.LICENCE_END_DATE = myResponse.getString("licence_end_date");
                                }
                            }

                            System.out.println(responseStatus + "=" + responseStatusMessage + "LK=>" + licenceKey + "<=");

                            if (responseStatus.equalsIgnoreCase("Success")) {
                                SharedPreferences.Editor editor = MainActivity.SHAREDPREFERENCES.edit();

                                editor.putString("licence_serial", MainActivity.LICENCE_SERIAL);
                                editor.putString("licence_key", MainActivity.LICENCE_KEY);
                                editor.putString("licence_start_date", MainActivity.LICENCE_START_DATE);
                                editor.putString("licence_end_date", MainActivity.LICENCE_END_DATE);

                                editor.apply();
                                if (editor.commit()) {
                                    System.out.println("Saved prefs");
                                } else {
                                    System.out.println("Failed Saving prefs");
                                }

                                Intent intent = new Intent(loginContext, Appfunctionality.class);
                                loginContext.startActivity(intent);

                            } else {
                                System.out.println("Failed for not known ");
                                System.out.println(responseStatus + "::" + responseStatusMessage);
                                Snackbar.make(fragmentLicenseSettings.v, responseStatus + " with " + responseStatusMessage, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } catch (JSONException | IOException je) {
                            je.printStackTrace();
                            System.out.println("Success JSON ");
                        }
                    } else {
                        System.out.println("Success for else");
                    }
                }else{
//                    Snackbar.make(fragmentLicenseSettings.v, "Error Obtaining Licence. "+fr.getResponseMessages(), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    System.out.println("Error Obtaining Licence. "+fr.getResponseMessages());
                }
            }catch (Exception ioe) {
//                Snackbar.make(fragmentLicenseSettings.v, "Licence Error. ", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                System.out.println("Licence Error. ");
            }
        }

        public CloseableHttpResponse getResponse(){
            return response;
        }
    }

    //Appliance number
    private class MisooCenterGetAppliance implements Runnable {
        String threadUrlPage;
        HashMap<String, String> threadHashmap;
        CloseableHttpResponse response = null;


        public  MisooCenterGetAppliance(String urlPage, HashMap<String, String> hashmap){
            threadUrlPage=urlPage;
            threadHashmap =hashmap;
        }

        @Override
        public void run() {

            try {

                FrameworkResponse fr = new HttpPOSTClient().getMisooCenterPostResponse(threadUrlPage, threadHashmap);
                if (fr.getResponseSuccess()) {
                    response = (CloseableHttpResponse) fr.getResponseObject();
                    HttpEntity httpEntity = response.getEntity();

                    String responseStatus = "";
                    String responseStatusMessage = "";

                    if (httpEntity != null) {
                        try {
                            String jsonString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                            JSONObject myResponse = new JSONObject(jsonString);

                            Iterator key = myResponse.keys();
                            while (key.hasNext()) {
                                String k = key.next().toString();
                                if (k.equalsIgnoreCase("Response")) {
                                    responseStatus = myResponse.getString("Response");
                                } else if (k.equalsIgnoreCase("Message")) {
                                    responseStatusMessage = myResponse.getString("Message");
                                } else if (k.equalsIgnoreCase("appliance_number")) {
                                    MainActivity.DEVICE_APPLIANCE_NUMBER = myResponse.getString("appliance_number");
                                } else if (k.equalsIgnoreCase("appliance_api_key")) {
                                    MainActivity.DEVICE_APPLIANCE_API_KEY = myResponse.getString("appliance_api_key");
                                }
                                else if (k.equalsIgnoreCase("appliance_api_key")) {
                                    MainActivity.DEVICE_APPLIANCE_API_KEY = myResponse.getString("appliance_api_key");
                                }
                            }

                            if (responseStatus.equalsIgnoreCase("Success")) {
                                SharedPreferences.Editor editor = MainActivity.SHAREDPREFERENCES.edit();

                                editor.putString("device_appliance_number", MainActivity.DEVICE_APPLIANCE_NUMBER);
                                editor.putString("device_appliance_api_key", MainActivity.DEVICE_APPLIANCE_API_KEY);
                                editor.commit();

                                Intent intent = new Intent(loginContext, Appfunctionality.class);
                                loginContext.startActivity(intent);

                            } else {
                                System.out.println("Failed for not known ");
//                                Snackbar.make(fragmentLicenseSettings.v, responseStatus + " with " + responseStatusMessage, Snackbar.LENGTH_LONG)
//                                        .setAction("Action", null).show();
//                                Toast.makeText(getApplicationContext(), responseStatus + " with " + responseStatusMessage, Toast.LENGTH_SHORT).show();
                                    System.out.println(responseStatus + " with " + responseStatusMessage);
                            }
                        } catch (JSONException | IOException je) {
                            je.printStackTrace();
                            System.out.println("Success JSON ");
                        }
                    } else {
                        System.out.println("Success for else");
                    }
                }else {
//                    Snackbar.make(fragmentLicenseSettings.v, "licence Error. "+fr.getResponseMessages(), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                    Toast.makeText(getApplicationContext(), "licence Error. "+fr.getResponseMessages(), Toast.LENGTH_SHORT).show();
                        System.out.println("licence Error. "+fr.getResponseMessages());
                }
            }catch (Exception ioe) {
//                Snackbar.make(fragmentLicenseSettings.v, "licence Error. General ", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Toast.makeText(getApplicationContext(), "licence Error. General.", Toast.LENGTH_SHORT).show();
                    System.out.println("licence Error. General.");
            }
        }

        public CloseableHttpResponse getResponse(){
            return response;
        }
    }

    private class LocalMisooGetWorkStations implements Runnable {
        String threadUrlPage;
        HashMap<String, String> threadHashmap;
        CloseableHttpResponse response = null;
        FragmentNeConfig networkFragment;


        public  LocalMisooGetWorkStations(String urlPage, HashMap<String, String> hashmap, FragmentNeConfig nf){
            threadUrlPage=urlPage;
            threadHashmap =hashmap;
            networkFragment=nf;
        }

        @Override
        public void run() {

            try {

                FrameworkResponse fr = new HttpPOSTClient().getLocalPostResponse(threadUrlPage, threadHashmap);
                if (fr.getResponseSuccess()) {
                    response = (CloseableHttpResponse) fr.getResponseObject();
                    HttpEntity httpEntity = response.getEntity();

                    String responseStatus = "";
                    String responseStatusMessage = "";
                    String dumpMessageSerial = "";

                    if (httpEntity != null) {
                        try {
                            String jsonString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                            JSONObject myResponse = new JSONObject(jsonString);

                            ArrayList<String> wsList=new ArrayList<String>();

                            Iterator key = myResponse.keys();
                            while (key.hasNext()) {
                                String k = key.next().toString();
                                if (k.equalsIgnoreCase("Response")) {
                                    responseStatus = myResponse.getString("Response");
                                } else if (k.equalsIgnoreCase("Message")) {
                                    responseStatusMessage = myResponse.getString("Message");
                                }else{
                                    wsList.add( myResponse.getString(k));
                                }
                            }

                            if (responseStatus.equalsIgnoreCase("Success")) {
                                System.out.println("Adding workstations ");
                                ArrayAdapter<String> wsAddapators = new ArrayAdapter<String>(FragmentNeConfig.workstations.getContext(), android.R.layout.simple_spinner_dropdown_item,wsList);
                                networkFragment.populateWorkStations(wsAddapators);
                            } else {
                                System.out.println("Failed for not known ");
                                Snackbar.make(FragmentNeConfig.v, responseStatus+"\n"+responseStatusMessage, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } catch (JSONException | IOException  je) {
                            je.printStackTrace();
                            System.out.println("Success JSON ");
                        }
                    } else {
                        System.out.println("Success for else");
                    }
                } else {
                    Snackbar.make(FragmentNeConfig.v, "Getting WorkStation Error. "+fr.getResponseMessages(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }catch (Exception ioe) {
//                Snackbar.make(FragmentNeConfig.v, "Getting WorkStation Error. General", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        }

        public CloseableHttpResponse getResponse(){
            return response;
        }
    }

    private class LocalMisooGetModems implements Runnable {
        String threadUrlPage;
        HashMap<String, String> threadHashmap;
        CloseableHttpResponse response = null;
        FragmentNeConfig networkFragment;

        public  LocalMisooGetModems(String urlPage, HashMap<String, String> hashmap, FragmentNeConfig nf){
            threadUrlPage=urlPage;
            threadHashmap =hashmap;
            networkFragment=nf;
        }

        @Override
        public void run() {

            try {

                FrameworkResponse fr = new HttpPOSTClient().getLocalPostResponse(threadUrlPage, threadHashmap);
                if (fr.getResponseSuccess()) {
                    response = (CloseableHttpResponse) fr.getResponseObject();
                    HttpEntity httpEntity = response.getEntity();

                    String responseStatus = "";
                    String responseStatusMessage = "";
                    String dumpMessageSerial = "";

                    if (httpEntity != null) {
                        try {
                            String jsonString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                            JSONObject myResponse = new JSONObject(jsonString);

                            ArrayList<String> modemsList = new ArrayList<>();

                            Iterator key = myResponse.keys();
                            while (key.hasNext()) {
                                String k = key.next().toString();
                                if (k.equalsIgnoreCase("Response")) {
                                    responseStatus = myResponse.getString("Response");
                                } else if (k.equalsIgnoreCase("Message")) {
                                    responseStatusMessage = myResponse.getString("Message");
                                }else{
                                    modemsList.add( myResponse.getString(k));
                                }
                            }

                            if (responseStatus.equalsIgnoreCase("Success")) {
                                System.out.println("Adding modems");
                                ArrayAdapter<String> modemsAddapator = new ArrayAdapter<String>(FragmentNeConfig.modems.getContext(), android.R.layout.simple_spinner_dropdown_item,modemsList);
                                networkFragment.populateModems(modemsAddapator);
                            } else {
                                System.out.println("Failed for not known ");
                                Snackbar.make(FragmentNeConfig.v, responseStatus+"\n"+responseStatusMessage, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } catch (JSONException | IOException  je) {
                            je.printStackTrace();
                            System.out.println("Success JSON ");
                        }
                    } else {
                        System.out.println("Success for else");
                    }
                } else {
                    System.out.println("Getting Modems Error. "+fr.getResponseMessages());
//                    Snackbar.make(FragmentNeConfig.v, "Getting Modems Error. "+fr.getResponseMessages(), Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                }

            }catch (Exception ioe) {
                System.out.println("Getting Modems Error. General");
//                Snackbar.make(FragmentNeConfig.v, "Getting Modems Error. General", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        }

        public CloseableHttpResponse getResponse(){
            return response;
        }
    }

    private class LocalMisooPushSMSApplication implements Runnable {

        String threadUrlPage;
        HashMap<String, String> threadHashmap;
        CloseableHttpResponse response = null;
        SMSModelSMSdetails activeSMS;

        public  LocalMisooPushSMSApplication(String urlPage, HashMap<String, String> hashmap, SMSModelSMSdetails sms){
            threadUrlPage=urlPage;
            threadHashmap =hashmap;
            activeSMS = sms;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {

            try {

                FrameworkResponse fr = new HttpPOSTClient().getLocalPostResponse(threadUrlPage, threadHashmap);
                if (fr.getResponseSuccess()) {
                    response = (CloseableHttpResponse) fr.getResponseObject();
                    HttpEntity httpEntity = response.getEntity();

                    String responseStatus = "";
                    String responseStatusMessage = "";
                    String dumpMessageSerial = "";

                    if (httpEntity != null) {

                        try {
                            String jsonString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                            JSONObject myResponse = new JSONObject(jsonString);

                            System.out.println(jsonString);
                            System.out.println("System.out.println(jsonString);");

                            Iterator key = myResponse.keys();
                            while (key.hasNext()) {
                                String k = key.next().toString();
                                if (k.equalsIgnoreCase("Response")) {
                                    responseStatus = myResponse.getString("Response");
                                } else if (k.equalsIgnoreCase("Message")) {
                                    responseStatusMessage = myResponse.getString("Message");
                                } else if (k.equalsIgnoreCase("dump_message_serial")) {
                                    dumpMessageSerial = myResponse.getString("dump_message_serial");
                                }
                            }

                            if (responseStatus.contains("Success")){

                                    for (int i = 0; i < MainActivity.INBOX_MESSAGE_LIST.size(); i++) {
                                        SMSModelSMSdetails sms = MainActivity.INBOX_MESSAGE_LIST.get(i);
                                        if (sms.originationAddress.equalsIgnoreCase(activeSMS.originationAddress) && sms.timeStamp == activeSMS.timeStamp) {
                                            MainActivity.updateSMSMessageIncomingInDisk(sms);
                                        }
                                    }

                                //Refreshing inbox after sending all the messages
                                //UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
                                System.out.println("Succeeded in sending to ERP");
//                                System.out.println(activeSMS.isMessageSynchronised);
//                                Toast.makeText(UnsynchedInboxFragment.instance().getContext(), "Succeeded in sending to ERP", Toast.LENGTH_LONG).show();
                            } else {
                                System.out.println("Did not succeeded in sending to ERP");
                                Toast.makeText(UnsynchedInboxFragment.instance().getContext(), "Did not succeeded in sending to ERP", Toast.LENGTH_LONG).show();

                            }



                        } catch (JSONException | IOException | IllegalArgumentException je) {
                            //Toast.makeText(getApplicationContext(), responseStatus + " = " + responseStatusMessage, Toast.LENGTH_SHORT).show();
                            System.out.println(responseStatus + " = " + responseStatusMessage);
                        }

                    }else {
                        //Toast.makeText(getApplicationContext(), "Did not get response from ERP/POS", Toast.LENGTH_SHORT).show();
                        System.out.println("Did not get response from ERP/POS");
                    }



                } else {
                    //Toast.makeText(getApplicationContext(), "Network Error. "+fr.getResponseMessages(), Toast.LENGTH_SHORT).show();
                    System.out.println("Network Error. "+fr.getResponseMessages());
                }
            }catch (NullPointerException ioe) {
                //Toast.makeText(MainActivity.instance(), "Invalid "+ioe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Invalid "+ioe.getLocalizedMessage());
            }catch (Exception ioe) {
                //Toast.makeText(MainActivity.instance(), "General "+ioe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("General "+ioe.getLocalizedMessage());
            }
        }

    }

    //on pressing fab button, send all the un-synchronized messages
    private class FabButtonLocalMisooPushSMSApplication implements Runnable {

        String threadUrlPage;
        HashMap<String, String> threadHashmap;
        CloseableHttpResponse response = null;
        SMSModelSMSdetails activeSMS;

        public  FabButtonLocalMisooPushSMSApplication(String urlPage, HashMap<String, String> hashmap, SMSModelSMSdetails sms){
            threadUrlPage=urlPage;
            threadHashmap =hashmap;
            activeSMS = sms;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run() {

            try {

                FrameworkResponse fr = new HttpPOSTClient().getLocalPostResponse(threadUrlPage, threadHashmap);
                if (fr.getResponseSuccess()) {
                    response = (CloseableHttpResponse) fr.getResponseObject();
                    HttpEntity httpEntity = response.getEntity();

                    String responseStatus = "";
                    String responseStatusMessage = "";
                    String dumpMessageSerial = "";

                    if (httpEntity != null) {

                        try {
                            String jsonString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
                            JSONObject myResponse = new JSONObject(jsonString);

                            System.out.println(jsonString);
                            System.out.println("System.out.println(jsonString);");

                            Iterator key = myResponse.keys();
                            while (key.hasNext()) {
                                String k = key.next().toString();
                                if (k.equalsIgnoreCase("Response")) {
                                    responseStatus = myResponse.getString("Response");
                                } else if (k.equalsIgnoreCase("Message")) {
                                    responseStatusMessage = myResponse.getString("Message");
                                } else if (k.equalsIgnoreCase("dump_message_serial")) {
                                    dumpMessageSerial = myResponse.getString("dump_message_serial");
                                }
                            }

                            if (responseStatus.contains("Success")){

                                for (int i = 0; i < MainActivity.INBOX_MESSAGE_LIST.size(); i++) {
                                    SMSModelSMSdetails sms = MainActivity.INBOX_MESSAGE_LIST.get(i);
                                    if (sms.originationAddress.equalsIgnoreCase(activeSMS.originationAddress) && sms.timeStamp == activeSMS.timeStamp) {
                                        MainActivity.updateSMSMessageIncomingInDisk(sms);
                                    }
                                }

                                //Refreshing inbox after sending all the messages
                                //UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
                                System.out.println("Succeeded in sending to ERP");
                                System.out.println(activeSMS.isMessageSynchronised);
                                Toast.makeText(UnsynchedInboxFragment.instance().getContext(), "Succeeded in sending to ERP", Toast.LENGTH_LONG).show();
                            } else {
                                System.out.println("Did not succeeded in sending to ERP");
                                Toast.makeText(UnsynchedInboxFragment.instance().getContext(), "Did not succeeded in sending to ERP", Toast.LENGTH_LONG).show();

                            }



                        } catch (JSONException | IOException | IllegalArgumentException je) {
                            //Toast.makeText(getApplicationContext(), responseStatus + " = " + responseStatusMessage, Toast.LENGTH_SHORT).show();
                            System.out.println(responseStatus + " = " + responseStatusMessage);
                        }

                    }else {
                        //Toast.makeText(getApplicationContext(), "Did not get response from ERP/POS", Toast.LENGTH_SHORT).show();
                        System.out.println("Did not get response from ERP/POS");
                    }



                } else {
                    //Toast.makeText(getApplicationContext(), "Network Error. "+fr.getResponseMessages(), Toast.LENGTH_SHORT).show();
                    System.out.println("Network Error. "+fr.getResponseMessages());
                }
            }catch (NullPointerException ioe) {
                //Toast.makeText(MainActivity.instance(), "Invalid "+ioe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Invalid "+ioe.getLocalizedMessage());
            }catch (Exception ioe) {
                //Toast.makeText(MainActivity.instance(), "General "+ioe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("General "+ioe.getLocalizedMessage());
            }
        }

    }

    ///push single message to pos
//    public static class LocalMisooPushSingleSMSApplication implements Runnable {
//
//        String threadUrlPage;
//        HashMap<String, String> threadHashmap;
//        CloseableHttpResponse response = null;
//        SMSModelSMSdetails activeSMS;

//        public  LocalMisooPushSingleSMSApplication(String urlPage, HashMap<String, String> hashmap, SMSModelSMSdetails sms){
//            threadUrlPage=urlPage;
//            threadHashmap =hashmap;
//            activeSMS = sms;
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.O)
//        public void run() {
//
//            try {
//
//                FrameworkResponse fr = new HttpPOSTClient().getLocalPostResponse(threadUrlPage, threadHashmap);
//                if (fr.getResponseSuccess()) {
//                    response = (CloseableHttpResponse) fr.getResponseObject();
//                    HttpEntity httpEntity = response.getEntity();
//
//                    String responseStatus = "";
//                    String responseStatusMessage = "";
//                    String dumpMessageSerial = "";
//
//                    if (httpEntity != null) {
//
//                        try {
//                            String jsonString = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
//                            JSONObject myResponse = new JSONObject(jsonString);
//
//                            System.out.println(jsonString);
//                            System.out.println("System.out.println(jsonString);");
//
//                            Iterator key = myResponse.keys();
//                            while (key.hasNext()) {
//                                String k = key.next().toString();
//                                if (k.equalsIgnoreCase("Response")) {
//                                    responseStatus = myResponse.getString("Response");
//                                } else if (k.equalsIgnoreCase("Message")) {
//                                    responseStatusMessage = myResponse.getString("Message");
//                                } else if (k.equalsIgnoreCase("dump_message_serial")) {
//                                    dumpMessageSerial = myResponse.getString("dump_message_serial");
//                                }
//                            }
//
//                            if (responseStatus.contains("Success")){
//
//
//                                    SMSModelSMSdetails sms;
//                                    if (sms.originationAddress.equalsIgnoreCase(activeSMS.originationAddress) && sms.timeStamp == activeSMS.timeStamp) {
//                                        MainActivity.updateSMSMessageIncomingInDisk(sms);
//                                    }
//
//
//                                //Refreshing inbox after sending all the messages
//                                //UnsynchedInboxFragment.instance().refreshSmsMessagesInbox();
//                                System.out.println("Succeeded in sending to ERP");
//                                System.out.println(activeSMS.isMessageSynchronised);
//                                Toast.makeText(UnsynchedInboxFragment.instance().getContext(), "Succeeded in sending to ERP", Toast.LENGTH_LONG).show();
//                            } else {
//                                System.out.println("Did not succeeded in sending to ERP");
//                                Toast.makeText(UnsynchedInboxFragment.instance().getContext(), "Did not succeeded in sending to ERP", Toast.LENGTH_LONG).show();
//
//                            }
//
//
//
//                        } catch (JSONException | IOException | IllegalArgumentException je) {
//                            //Toast.makeText(getApplicationContext(), responseStatus + " = " + responseStatusMessage, Toast.LENGTH_SHORT).show();
//                            System.out.println(responseStatus + " = " + responseStatusMessage);
//                        }
//
//                    }else {
//                        //Toast.makeText(getApplicationContext(), "Did not get response from ERP/POS", Toast.LENGTH_SHORT).show();
//                        System.out.println("Did not get response from ERP/POS");
//                    }
//
//
//
//                } else {
//                    //Toast.makeText(getApplicationContext(), "Network Error. "+fr.getResponseMessages(), Toast.LENGTH_SHORT).show();
//                    System.out.println("Network Error. "+fr.getResponseMessages());
//                }
//            }catch (NullPointerException ioe) {
//                //Toast.makeText(MainActivity.instance(), "Invalid "+ioe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                System.out.println("Invalid "+ioe.getLocalizedMessage());
//            }catch (Exception ioe) {
//                //Toast.makeText(MainActivity.instance(), "General "+ioe.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                System.out.println("General "+ioe.getLocalizedMessage());
//            }
//        }
//
//    }





    private class outgoingMessagesSent implements Runnable{

        @Override
        public void run() {

        }
    }

}



