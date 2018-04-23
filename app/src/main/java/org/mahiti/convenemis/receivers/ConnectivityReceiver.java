package org.mahiti.convenemis.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import net.sqlcipher.database.SQLiteDatabase;

import org.mahiti.convenemis.AutoSyncActivity;
import org.mahiti.convenemis.R;
import org.mahiti.convenemis.api.BeneficiaryApis.SaveBeneficiary;
import org.mahiti.convenemis.api.BeneficiaryApis.SaveFacility;
import org.mahiti.convenemis.api.BeneficiaryApis.UpdateBeneficiary;
import org.mahiti.convenemis.api.BeneficiaryApis.UpdateFacility;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String TAG = "ConnectivityReceiver";
    Context context;
    SharedPreferences preferences;
    ExternalDbOpenHelper externaldbhelper;
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
        Logger.logD(TAG, "ConnectivityReceiver  ");
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        Logger.logD(TAG, "onReceive ");
        this.context = context;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }

        if (!isConnected) {
            Logger.logD("Connectivity", "No internet connection (receiver)");
            return;
        }
        try {
            Logger.logD(TAG, " net check ");
            SQLiteDatabase.loadLibs(context);
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            externaldbhelper = ExternalDbOpenHelper.getInstance(context, preferences.getString(Constants.DBNAME, ""), preferences.getString("UID", ""));
            getBeneFacilitySyncCount(context);
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }


    private void getBeneFacilitySyncCount(Context context) {
        try {
            if (!externaldbhelper.getSyncBeneficiaryOfflineRecord().isEmpty()) {
                new SaveBeneficiary(context, context.getString(R.string.main_Url) + "beneficiary/addhousehold/").execute();
            }
            if (!externaldbhelper.getSyncFacilityOfflineRecord().isEmpty()) {
                new SaveFacility(context, context.getString(R.string.main_Url) + "facilities/facilityadd/").execute();
            }
            if (!externaldbhelper.getBeneficiaryDetails().isEmpty()) {
                new UpdateBeneficiary(context, context.getString(R.string.main_Url) + "beneficiary/householdupdate/").execute();
            } else {
                Logger.logD("", "no updated Beneficiary records to sync to backend");
            }
            if (!externaldbhelper.getFacilityDetails().isEmpty()) {
                new UpdateFacility(context, context.getString(R.string.main_Url) + "facilities/facilityupdate/").execute();
            } else {
                Logger.logD("", "no updated datumFacilityList records to sync to backend");
            }
            AutoSyncActivity autoSyncObj = new AutoSyncActivity(context);
            autoSyncObj.callingAutoSync(1);
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}


