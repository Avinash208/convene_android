package org.assistindia.convene.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

import org.assistindia.convene.utils.Logger;

import java.util.Date;

public class PowerConnectionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor;

			if ("android.intent.action.ACTION_POWER_CONNECTED".equalsIgnoreCase(intent.getAction())
					|| ("android.intent.action.ACTION_POWER_DISCONNECTED".equalsIgnoreCase(intent.getAction()))) {
				IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
				Intent batteryStatus = context.registerReceiver(null, ifilter);
				int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
				                     status == BatteryManager.BATTERY_STATUS_FULL;
				int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

				String chargepercentage = String.valueOf(level) + "%";
				editor = preferences.edit();
				editor.putString("Time", new Date().toLocaleString());
				editor.putBoolean("isCharging", isCharging);
				editor.putString("chargePercent", chargepercentage);
				Logger.logV(PowerConnectionReceiver.class.getSimpleName(), "PowerConnectionReceiver current date " + new Date().toLocaleString());
				Logger.logV(PowerConnectionReceiver.class.getSimpleName(), "PowerConnectionReceiver isCharging " + isCharging);

				Logger.logV(PowerConnectionReceiver.class.getSimpleName(), "PowerConnectionReceiver chargePercent " + chargepercentage);


				editor.commit();
			}
		}
		catch (Exception e)
		{
			Logger.logE(PowerConnectionReceiver.class.getSimpleName(), "Exception in PowerConnectionReceiver class", e);
		}

	}

}
