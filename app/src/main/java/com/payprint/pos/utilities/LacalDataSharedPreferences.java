package com.payprint.pos.utilities;

import android.content.SharedPreferences;

public class LacalDataSharedPreferences {
    public static SharedPreferences sharedPreferences = null;

    public LacalDataSharedPreferences(SharedPreferences sp){
        sharedPreferences = sp;
    }

    public SharedPreferences getPreferences(){
        return sharedPreferences;
    }
}
