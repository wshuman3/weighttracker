package com.wshuman3.weighttracker;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        MyApplication.context=getApplicationContext();
    }

    /**
     * This Method will always return the application Context.
     * 
     * @return Context
     */
    public static Context getAppContext() {
    	return context;
    }
}
