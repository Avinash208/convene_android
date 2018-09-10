package org.fwwb.convene.convenecode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.PermissionClass;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Aviansh Raj  on 22/03/17.
 */

public class Splash extends Activity {


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splashscreen);
        /*
         * Asking permission if the decive as higher version
         */
        if (PermissionClass.checkPermission(Splash.this)) {
            callLogin();
        }
         else {
            PermissionClass.requestPermission(Splash.this);
            Logger.logD("Permission Granted","Permission Granted");
        }
    }

    /**
     * On Accepting or denied the Permissions
     * @param requestCode requestCode
     * @param permissions permissions
     * @param grantResults grantResults true/false
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        callLogin();

    }

    private void callLogin() {
        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Log.d("Permission","onRequestPermissionsResult"+"");
                startActivity(new Intent(Splash.this,LoginParentActivity.class));
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}