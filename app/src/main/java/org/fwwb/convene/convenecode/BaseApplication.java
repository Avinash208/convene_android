package org.fwwb.convene.convenecode;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.fwwb.convene.convenecode.receivers.ConnectivityReceiver;


public class BaseApplication extends Application {


    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
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
