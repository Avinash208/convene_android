package org.mahiti.convenemis.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 19/01/17.
 */

public class PermissionClass {


    private PermissionClass() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static boolean checkPermission(Activity activity) {

        int externalRead = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int externalWrite = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int gpsAccessFineLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int gpsAccessCoarseLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int readPhoneState = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        return externalRead == PackageManager.PERMISSION_GRANTED && externalWrite == PackageManager.PERMISSION_GRANTED && gpsAccessFineLocation == PackageManager.PERMISSION_GRANTED && gpsAccessCoarseLocation == PackageManager.PERMISSION_GRANTED && readPhoneState == PackageManager.PERMISSION_GRANTED;

    }

    public static void requestPermission(Activity activity) {
        List<String> list = new ArrayList<>();
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE))
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION))
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION))
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE))
            list.add(Manifest.permission.READ_PHONE_STATE);

        String[] stockArr = new String[list.size()];
        stockArr = list.toArray(stockArr);
        if (stockArr.length != 0) {
            ActivityCompat.requestPermissions(activity, stockArr, 1);
        }
    }
}
