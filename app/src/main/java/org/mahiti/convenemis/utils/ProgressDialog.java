package org.mahiti.convenemis.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import org.mahiti.convenemis.R;

/**
 * Created by mihir on 17-08-2015.
 */
public class ProgressDialog extends Dialog {

    static ProgressDialog sDialog;

    public ProgressDialog(Context context) {
        super(context, R.style.ProgressDialog);
    }

    public static ProgressDialog show(Context context) {
        return show(context, "", "");
    }

    public static ProgressDialog show(Context context, boolean cancelable) {
        return show(context, "", "", false, true);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message) {
        return show(context, title, message, false);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message,
                                      boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message,
                                      boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static ProgressDialog show(Context context, CharSequence title, CharSequence message,
                                      boolean indeterminate, boolean cancelable, OnCancelListener
                                              cancelListener) {
        sDialog = new ProgressDialog(context);
        sDialog.setTitle(title);
        sDialog.setCancelable(cancelable);
        sDialog.setOnCancelListener(cancelListener);

        /* The next line will add the ProgressBar to the dialog. */
        sDialog.addContentView(new ProgressBar(context), new LayoutParams(LayoutParams
                .WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        sDialog.show();

        return sDialog;
    }

    public static boolean isShowingProgress() {
        return sDialog != null && sDialog.isShowing();
    }

    public static void cancelDialog() {
        try {
            if (sDialog != null && sDialog.isShowing()) {
                sDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
