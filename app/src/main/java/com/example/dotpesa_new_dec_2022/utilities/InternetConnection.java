package com.example.dotpesa_new_dec_2022.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.dotpesa_new_dec_2022.Login;
import com.example.dotpesa_new_dec_2022.UnsynchedInboxFragment;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InternetConnection {

    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean internetIsConnected(Context context) {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * CHECK WHETHER SERVER CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean serverIsConnected(Context context) {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkConnection(Login context) {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager;

            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        }catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }
        return connected;
    }

    public static boolean FabCheckConnection(UnsynchedInboxFragment context) {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager;

            connectivityManager = (ConnectivityManager) context.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        }catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }
        return connected;
    }


    public static boolean isInternetAvailable(Context context) {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public static boolean isOnline(Context context) {
        try {
            InetAddress.getByName("google.com").isReachable(3);

            return true;
        } catch (UnknownHostException e){
            return false;
        } catch (IOException e){
            return false;
        }
    }

}
