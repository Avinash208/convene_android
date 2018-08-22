package org.yale.convene;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.multidex.MultiDex;

import org.yale.convene.receivers.ConnectivityReceiver;


public class BaseApplication extends Application {


    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityReceiver connectionReceiver= new ConnectivityReceiver();
            registerReceiver(connectionReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }
   }
