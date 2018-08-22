package org.yale.convene;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import org.yale.convene.receivers.ConnectivityReceiver;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.ToastUtils;

import java.util.Locale;


public class BaseActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    SharedPreferences defaultPreferences;
    public static final String Theme_Current = "AppliedTheme";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            defaultPreferences = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
            setAppTheme();
            setContentView(R.layout.activity_base);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);

        }catch (Exception e){
            Logger.logE("BaseActivity ","onCreate --- ",e);
        }
    }


    public static void  setLocality(int selectedLangValue,Activity activity){
        Logger.logV("BaseActivity","selectedLangValue --- "+selectedLangValue);
        setLocalityLanguage(activity,"en");
    }

    public static void setLocalityLanguage(Activity activity,String language){
        Locale myLocale = new Locale(language);
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    private void setAppTheme() {
        if (!"".equals(defaultPreferences.getString(Theme_Current, ""))) {

            if ("Blue".equals(defaultPreferences.getString(Theme_Current, ""))) {
                setTheme(R.style.AppTheme);

            } else if ("Yellow".equals(defaultPreferences.getString(Theme_Current, ""))) {
                setTheme(R.style.AppTheme_Yellow);

            } else if ("Green_Dark".equals(defaultPreferences.getString(Theme_Current, ""))) {
                setTheme(R.style.AppTheme_Green);
            } else if ("Green".equals(defaultPreferences.getString(Theme_Current, ""))) {
                setTheme(R.style.AppTheme_Green);
            }
        } else {
            setTheme(R.style.AppTheme_Green);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Logger.logD("ther", "Connected to Internet");
        }else {
            ToastUtils.displayToastUi("No Internet Available!... Data stored in Offline", this);
            Logger.logD("ther", "No Internet Connection!, Data stored in Offline");
        }
    }
}
