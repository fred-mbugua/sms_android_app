package com.example.dotpesa_new_dec_2022.core;

import android.app.Application;

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}