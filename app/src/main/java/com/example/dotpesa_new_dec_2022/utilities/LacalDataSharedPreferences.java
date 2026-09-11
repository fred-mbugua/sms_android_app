package com.example.dotpesa_new_dec_2022.utilities;

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
