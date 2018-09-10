package org.fwwb.convene.convenecode.utils;


public class EmailUtils

{
    private EmailUtils() {
        Logger.logV(Constants.DO_NOTHING,Constants.DO_NOTHING);
    }

    public static boolean isEmailValid(CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
