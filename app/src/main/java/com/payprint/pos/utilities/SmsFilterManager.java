package com.payprint.pos.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.payprint.pos.MainActivity;

public class SmsFilterManager {

    public static final String MODE_ALL = "ALL";
    public static final String MODE_SELECTED = "SELECTED";

    public static boolean isMessageAllowed(Context context, String sender, String messageBody) {
        if (context == null) return true;

        SharedPreferences prefs = MainActivity.SHAREDPREFERENCES;
        if (prefs == null) {
            prefs = context.getSharedPreferences(MainActivity.PAYPRINT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        if (prefs == null) return true;

        String mode = prefs.getString("sms_filter_mode", MODE_ALL);

        if (MODE_ALL.equalsIgnoreCase(mode)) {
            return true;
        }

        boolean allowMpesa = prefs.getBoolean("sms_filter_mpesa", true);
        boolean allowAirtel = prefs.getBoolean("sms_filter_airtel", false);
        boolean allowTkash = prefs.getBoolean("sms_filter_tkash", false);
        boolean allowEquity = prefs.getBoolean("sms_filter_equity", false);
        boolean allowKcb = prefs.getBoolean("sms_filter_kcb", false);
        boolean allowCoop = prefs.getBoolean("sms_filter_coop", false);
        boolean allowNcba = prefs.getBoolean("sms_filter_ncba", false);
        boolean allowBanks = prefs.getBoolean("sms_filter_absa_banks", false);

        String snd = sender != null ? sender.toUpperCase().trim() : "";
        String body = messageBody != null ? messageBody.toUpperCase().trim() : "";

        if (allowMpesa) {
            if (snd.contains("MPESA") || snd.contains("M-PESA") || snd.contains("SAFARICOM")) {
                return true;
            }
            if (body.contains("KSH") && (body.contains("CONFIRMED") || body.contains("RECEIVED") || body.contains("SENT TO") || body.contains("PAYBILL") || body.contains("TILL"))) {
                return true;
            }
        }

        if (allowAirtel) {
            if (snd.contains("AIRTEL") || snd.contains("AMONEY")) {
                return true;
            }
            if (body.contains("AIRTEL MONEY") || body.contains("TXN ID")) {
                return true;
            }
        }

        if (allowTkash) {
            if (snd.contains("TKASH") || snd.contains("TELKOM")) {
                return true;
            }
        }

        if (allowEquity) {
            if (snd.contains("EQUITY") || snd.contains("EAZZY")) {
                return true;
            }
        }

        if (allowKcb) {
            if (snd.contains("KCB") || snd.contains("KCBBANK")) {
                return true;
            }
        }

        if (allowCoop) {
            if (snd.contains("COOP") || snd.contains("COOPBANK")) {
                return true;
            }
        }

        if (allowNcba) {
            if (snd.contains("NCBA") || snd.contains("LOOP")) {
                return true;
            }
        }

        if (allowBanks) {
            if (snd.contains("ABSA") || snd.contains("STANBIC") || snd.contains("FAMILYBANK") || snd.contains("DTB") || snd.contains("STANCHART") || snd.contains("IMBANK")) {
                return true;
            }
        }

        return false;
    }
}
