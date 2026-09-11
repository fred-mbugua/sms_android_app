package com.example.dotpesa_new_dec_2022;

import static com.misoo.framework.ApplicationSessionBean.APPLICATION_MISOO_CENTER_HTTP_CLIENT_COOKIE;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.MessagesModel;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.DBHelper;
import com.example.dotpesa_new_dec_2022.call_sms_db_modules.recyclerviewadapter.database.SMSModelSMSdetails;
import com.example.dotpesa_new_dec_2022.core.App;
import com.example.dotpesa_new_dec_2022.core.SmsService;
import com.example.dotpesa_new_dec_2022.utilities.NodeSmsSyncQueue;
import com.example.dotpesa_new_dec_2022.utilities.PermissionManager;
import com.example.dotpesa_new_dec_2022.utilities.SmsInboxImporter;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;

import dotpesa_new_dec_2022.R;


public class MainActivity extends AppCompatActivity {

    //launcher animation variables
    Animation topAnim, bottomAnim;
    ImageView launcherImage;
    TextView launcherText;

    //moving to first screen after launcher in 5seconds
    private static int SPLASH_SCREEN = 2000;

    //shared preferences
    public static SharedPreferences SHAREDPREFERENCES;
    public static final String DOTPESA_SHARED_PREFERENCES = "DOTPESA_sharedPrefs";


    public static  ArrayList<SMSModelSMSdetails> INBOX_MESSAGE_LIST = new ArrayList<SMSModelSMSdetails>();
//    public static ArrayList<SMSMessageOutboxDetails> OUTBOX_SENDING_MESSAGE_LIST = new ArrayList<SMSMessageOutboxDetails>();
    public static ArrayList<String> OUTBOX_MESSAGE_LIST = new ArrayList<String>();
    //licences
    public static String LICENCE_CUSTOMER_SERIAL = "";
    public static String LICENCE_CUSTOMER_USER_NAME = "";
    public static String LICENCE_CUSTOMER_USER_PASSWORD = "";
    public static String LICENCE_CUSTOMER_COMPANY_NAME = "";
    public static String LICENCE_CUSTOMER_EMAIL_ADDRESS = "";
    public static String LICENCE_CUSTOMER_MOBILE_NUMBER = "";
    public static String LICENCE_SERIAL = "";
    public static String LICENCE_KEY = "";
    public static String LICENCE_START_DATE = "";
    public static String LICENCE_END_DATE = "";
    public static Boolean LICENCE_TERMS_CONDITIONS_READ = Boolean.FALSE;
    public static Boolean LICENCE_TERMS_CONDITIONS_ACCEPTED = Boolean.FALSE;

    public static String APPLICATION_LOCAL_MISOO_PORTAL_REFRESH_RATE = "";
    public static String APPLICATION_LOCAL_MISOO_LIMIT_PUSH_ONLY_RECEIVED_FROM="";
    public static String APPLICATION_LOCAL_MISOO_PORTAL_MODEM_NAME = "";
    public static String APPLICATION_LOCAL_MISOO_PORTAL_WORKSTATION = "";
    public static Boolean APPLICATION_LOCAL_MISOO_PORTAL_DELETE_ON_PUSH = Boolean.FALSE;
    public static Boolean APPLICATION_LOCAL_MISOO_PRINT_MESSAGE_ON_PUSH = Boolean.FALSE;
    public static Boolean APPLICATION_LOCAL_MISOO_LIMIT_PUSH_RECEIVED = Boolean.FALSE;
    public static Boolean APPLICATION_LOCAL_MISOO_AUTO_SENDING_SMS = Boolean.FALSE;
    public static String APPLICATION_LOCAL_MISOO_ONLY_RECIEVE_FROM = "";


    public static  String DEVICE_APPLIANCE_NUMBER = "";
    public static  String DEVICE_APPLIANCE_API_KEY = "";

    public static HttpContext HTTP_SESSION_CONTEXT = new BasicHttpContext();
    public static HttpContext HTTP_LOCAL_SESSION_CONTEXT = new BasicHttpContext();
    public static BasicCookieStore APPLICATION_LOCAL_MISOO_PORTAL_HTTP_CLIENT_COOKIE =  new BasicCookieStore();

    public static String APPLICATION_MISOO_CENTER_HTTP_PROTOCOL = "";
    public static String APPLICATION_MISOO_CENTER_HTTP_HOST_NAME = "";
    public static String APPLICATION_MISOO_CENTER_HTTP_HOST_PORT = "";


    public static String APPLICATION_LOCAL_MISOO_PORTAL_HTTP_PROTOCOL = "";
    public static String APPLICATION_LOCAL_MISOO_PORTAL_HTTP_HOST_NAME = "";
    public static String APPLICATION_LOCAL_MISOO_PORTAL_HTTP_HOST_PORT = "";


    public   static String APPLICATION_LOCAL_MISOO_PORTAL_PAGE = "";

    public static CloseableHttpClient APPLICATION_MISOO_CENTER_HTTP_CLIENT;

    public static Boolean LICENCE_CUSTOMER_LOGIN_REMEMBER_ME = Boolean.FALSE;
    public static DBHelper DB_HELPER;

    public static synchronized DBHelper getDbHelper(Context context) {
        if (DB_HELPER == null) {
            Context ctx = context != null ? context.getApplicationContext() : App.getInstance();
            DB_HELPER = new DBHelper(ctx);
        }
        return DB_HELPER;
    }


    public static CloseableHttpClient APPLICATION_LOCAL_MISOO_PORTAL_HTTP_CLIENT = getNewHttpClient();

    private static CloseableHttpClient getNewHttpClient() {
        try {

            CloseableHttpClient httpclient = HttpClients.custom()
                    .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                            .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                    .setSslContext(SSLContextBuilder.create()
                                            .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                                            .build())
                                    .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                    .build())
                            .build())
                    .build();
            return httpclient;
        } catch (Exception e) {
            return HttpClients.createDefault();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SHAREDPREFERENCES = getSharedPreferences(DOTPESA_SHARED_PREFERENCES, MODE_PRIVATE);

//        OUTBOX_SENDING_MESSAGE_LIST = new ArrayList<SMSMessageOutboxDetails>();
        OUTBOX_MESSAGE_LIST = new ArrayList<String>();
        INBOX_MESSAGE_LIST = new ArrayList<SMSModelSMSdetails>();


        APPLICATION_MISOO_CENTER_HTTP_CLIENT = getNewHttpClient();
        HTTP_SESSION_CONTEXT.setAttribute(HttpClientContext.COOKIE_STORE, APPLICATION_MISOO_CENTER_HTTP_CLIENT_COOKIE);
        HTTP_LOCAL_SESSION_CONTEXT.setAttribute(HttpClientContext.COOKIE_STORE, APPLICATION_LOCAL_MISOO_PORTAL_HTTP_CLIENT_COOKIE);

        APPLICATION_MISOO_CENTER_HTTP_PROTOCOL = SHAREDPREFERENCES.getString("misoo_center_http_protocol", "https");
        APPLICATION_MISOO_CENTER_HTTP_HOST_NAME = SHAREDPREFERENCES.getString("misoo_center_host_name", "102.176.180.134");
        APPLICATION_MISOO_CENTER_HTTP_HOST_PORT=SHAREDPREFERENCES.getString("misoo_center_host_port", "443");
        //http unsecured
        APPLICATION_LOCAL_MISOO_PORTAL_HTTP_PROTOCOL = SHAREDPREFERENCES.getString("local_misoo_portal_protocol", "http");
        APPLICATION_LOCAL_MISOO_PORTAL_HTTP_HOST_NAME = SHAREDPREFERENCES.getString("local_misoo_portal_host_IP", "localhost");
        APPLICATION_LOCAL_MISOO_PORTAL_HTTP_HOST_PORT = SHAREDPREFERENCES.getString("local_misoo_portal_port_number", "8080");

        LICENCE_CUSTOMER_SERIAL = SHAREDPREFERENCES.getString("online_customer_serial", "");
        LICENCE_CUSTOMER_USER_PASSWORD = SHAREDPREFERENCES.getString("login_user_password", "");
        LICENCE_CUSTOMER_EMAIL_ADDRESS = SHAREDPREFERENCES.getString("email_address", "");
        LICENCE_CUSTOMER_LOGIN_REMEMBER_ME = Boolean.valueOf(SHAREDPREFERENCES.getString("login_remember_me", "true"));

        APPLICATION_LOCAL_MISOO_PORTAL_PAGE = SHAREDPREFERENCES.getString("local_misoo_portal_page", "DotPESA/portal/mpesa/endpoint/safaricom_mpesa_endpoint.jsp");

        //licences
        LICENCE_START_DATE = SHAREDPREFERENCES.getString("licence_start_date", "");
        LICENCE_END_DATE = SHAREDPREFERENCES.getString("licence_end_date", "");

        DEVICE_APPLIANCE_API_KEY = SHAREDPREFERENCES.getString("device_appliance_api_key", "");
        DEVICE_APPLIANCE_NUMBER = SHAREDPREFERENCES.getString("device_appliance_number", "");
        LICENCE_CUSTOMER_SERIAL = SHAREDPREFERENCES.getString("online_customer_serial", "");
        LICENCE_CUSTOMER_USER_NAME = SHAREDPREFERENCES.getString("login_user_name", "");
        LICENCE_CUSTOMER_USER_PASSWORD = SHAREDPREFERENCES.getString("login_user_password", "");
        LICENCE_CUSTOMER_COMPANY_NAME = SHAREDPREFERENCES.getString("company_name", "");
        LICENCE_CUSTOMER_EMAIL_ADDRESS = SHAREDPREFERENCES.getString("email_address", "");
        LICENCE_CUSTOMER_MOBILE_NUMBER = SHAREDPREFERENCES.getString("mobile_number", "");

        APPLICATION_LOCAL_MISOO_PORTAL_REFRESH_RATE = SHAREDPREFERENCES.getString("local_misoo_portal_sleep_time", "");
        APPLICATION_LOCAL_MISOO_LIMIT_PUSH_ONLY_RECEIVED_FROM = SHAREDPREFERENCES.getString("local_misoo_limit_push_only_received_from", "");
        APPLICATION_LOCAL_MISOO_PORTAL_MODEM_NAME = SHAREDPREFERENCES.getString("local_misoo_portal_modem_name", "");
        APPLICATION_LOCAL_MISOO_PORTAL_WORKSTATION = SHAREDPREFERENCES.getString("local_misoo_portal_workstation", "");
        APPLICATION_LOCAL_MISOO_PORTAL_DELETE_ON_PUSH = Boolean.valueOf(SHAREDPREFERENCES.getString("local_misoo_portal_push_delete", ""));
        APPLICATION_LOCAL_MISOO_PRINT_MESSAGE_ON_PUSH = Boolean.valueOf(SHAREDPREFERENCES.getString("local_misoo_portal_print_message_on_push", "false"));
        APPLICATION_LOCAL_MISOO_LIMIT_PUSH_RECEIVED = Boolean.valueOf(SHAREDPREFERENCES.getString("local_misoo_limit_push_received", "false"));
        APPLICATION_LOCAL_MISOO_AUTO_SENDING_SMS = Boolean.valueOf(SHAREDPREFERENCES.getString("local_misoo_auto_send_sms", ""));
        APPLICATION_LOCAL_MISOO_ONLY_RECIEVE_FROM = SHAREDPREFERENCES.getString("local_misoo_only_recieve_from", "");

        DB_HELPER = getDbHelper(this);

        if (PermissionManager.hasAllPermissions(this)) {
            SmsInboxImporter.importDeviceSmsMessagesAsync(this);
            try {
                Intent serviceIntent = new Intent(this, SmsService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Launcher Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.launcher_animation_top);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.launcher_animation_bottom);

        //Launcher Animation Hooks
        launcherImage = findViewById(R.id.imageView);
        launcherText = findViewById(R.id.launcherTextView);

        launcherImage.setAnimation(topAnim);
        launcherText.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginpage = new Intent(MainActivity.this, Appfunctionality.class);
//                Intent loginpage = new Intent(MainActivity.this, Login.class);
                //removed to include animation
//                startActivity(loginpage);
//                finish();
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(launcherImage, "splashscrnimage");

                ActivityOptions options =ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(loginpage, options.toBundle());
                finish();
            }
        }, SPLASH_SCREEN);

        automatedMessagesSending();

    }

    public static String getMacAddress() {
        try {
            ArrayList<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String hex = Integer.toHexString(b & 0xFF);
                    if (hex.length() == 1)
                        hex = "0".concat(hex);
                    res1.append(hex.concat(":"));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void saveSMSMessagesToDisk(MessagesModel rawSms){
        SMSModelSMSdetails sms = new SMSModelSMSdetails(rawSms);
        try {
            getDbHelper(App.getInstance()).save(sms);
            if (App.getInstance() != null) {
                NodeSmsSyncQueue.dispatchUnsyncedMessages(App.getInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void listOfAllSMSMessagesInSQLiteDB(){
        try{
            INBOX_MESSAGE_LIST = getDbHelper(App.getInstance()).allDBUnsyncedMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void updateSMSMessageIncomingInDisk(SMSModelSMSdetails sms){
        try {
            INBOX_MESSAGE_LIST = getDbHelper(App.getInstance()).updateIncomingSMSisSynchronized(sms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   //function to send mesages after every set of seconds
    public static void automatedMessagesSending(){
        final Handler handler = new Handler();
        final int delay = 60000;

        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                //stuff
                UnsynchedInboxFragment unsynchedInboxFragment = new UnsynchedInboxFragment();
                System.out.println("Start of auto sending");
                unsynchedInboxFragment.autoLoadSMSAndPushToPOS();
                if (App.getInstance() != null) {
                    NodeSmsSyncQueue.dispatchUnsyncedMessages(App.getInstance());
                }
                handler.postDelayed(this, delay);

            }
        }, delay);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static void openSMSMessagesToDisk(){
//        try {
//            INBOX_MESSAGE_LIST = DB_HELPER.getSMSMessagesList();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}