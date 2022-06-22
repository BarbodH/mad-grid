package com.example.mad_grid;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MadGridApp extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MadGridApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MadGridApp.context;
    }
}
