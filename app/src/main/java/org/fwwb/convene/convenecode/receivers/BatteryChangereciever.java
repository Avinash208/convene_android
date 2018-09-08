package org.fwwb.convene.convenecode.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

import org.fwwb.convene.convenecode.utils.Logger;


public class BatteryChangereciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			 SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		     int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		     boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
		     editor.putBoolean("isCharging", isCharging);
		     editor.apply();
		}catch(Exception e)
		{
			Logger.logE(BatteryChangereciever.class.getSimpleName(), "Exception in BatteryChangereciever", e);
		}
	}
}
