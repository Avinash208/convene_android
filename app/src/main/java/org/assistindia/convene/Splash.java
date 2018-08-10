package org.assistindia.convene;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.PermissionClass;

/**
 * Created by Aviansh Raj  on 22/03/17.
 */

public class Splash extends Activity {


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
      /*  Fabric.with(this, new Crashlytics());*/
        setContentView(R.layout.splashscreen);
        /*
         * Asking permission if the decive as higher version
         */
        if (PermissionClass.checkPermission(Splash.this)) {
            startActivity(new Intent(Splash.this,LoginParentActivity.class));
            finish();
            Logger.logD("Permission denide","Permission denide");
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
        Log.d("Permission","onRequestPermissionsResult"+"");
        startActivity(new Intent(Splash.this,LoginParentActivity.class));
        Logger.logD("onRequestPermissionsResult","Intent to login parent activity");
        finish();

    }
}