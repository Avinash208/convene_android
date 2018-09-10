package org.fwwb.convene.convenecode.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class CheckNetwork {

	private boolean haveConnectedWifi = false;
	private boolean haveConnectedMobile = false;
	private boolean haveConnectedActive = false;

	private Context mContext;


	public CheckNetwork(Context mContext) {
		Log.d("context", "**********"+this.mContext);
		this.mContext = mContext;

	}

	/*
	 * Checking the network is available or not
	 */
	public boolean checkNetwork() {
		ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connMgr ==null)
			return false;
		NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			haveConnectedActive = true;
		}

		if (wifi.isAvailable()) {
			haveConnectedWifi = true;
		}
		try {

			if (mobile.isAvailable()) {
				haveConnectedMobile = true;
			}
		}
		catch (Exception e)
		{
			Logger.logE(CheckNetwork.class.getSimpleName(), "Exception in checkNetwork method", e);
		}
		// if network is connected and either wifi or mobile available means
		// return true , else false
        return (haveConnectedWifi || haveConnectedMobile)
                && haveConnectedActive;
	}
}