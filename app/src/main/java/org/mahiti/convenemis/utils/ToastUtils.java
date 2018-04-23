package org.mahiti.convenemis.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Updated by mahiti on 24/02/16.
 */
public class ToastUtils {
    private ToastUtils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static void displayToast(String message, Context context){
        if(context!=null) {
            Toast t1 = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            LinearLayout toastLayout = (LinearLayout) t1.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            t1.setGravity(Gravity.CENTER, 0, 0);
            t1.show();
        }

    }

    public static void displayToastUi(final String message, final Context context)
    {
        try
        {
            if( context != null && null != message && !"".equals(message))
            {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast t1 = Toast.makeText(context, message, Toast.LENGTH_LONG);
                        LinearLayout toastLayout = (LinearLayout) t1.getView();
                        TextView toastTV = (TextView) toastLayout.getChildAt(0);
                        toastTV.setTextSize(20);
                        t1.setGravity(Gravity.CENTER, 0, 0);
                        t1.show();
                    }
                });
            }
        }
        catch (Exception e)
        {
            Logger.logE(ToastUtils.class.getSimpleName(),"Exception in displaying toast",e);
        }
    }
}
