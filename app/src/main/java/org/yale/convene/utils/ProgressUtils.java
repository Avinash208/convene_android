package org.yale.convene.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by mahiti on 28/12/15.
 */
public class ProgressUtils {

    private ProgressUtils() {
    }

    public static ProgressDialog showProgress(Activity activity, boolean cancelable, String title){
        ProgressDialog progDialog = new ProgressDialog(activity);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(cancelable);
        progDialog.setTitle(title);
        return progDialog;
    }
    public static ProgressDialog showProgresss(Context activity, boolean cancelable, String title){
        ProgressDialog progDialog = new ProgressDialog(activity);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(cancelable);
        progDialog.setTitle(title);
        return progDialog;
    }

    public static void CancelProgress(ProgressDialog dialog){
        if(dialog!=null && dialog.isShowing())
            dialog.cancel();
    }


}
