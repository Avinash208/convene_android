package org.assistindia.convene.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import org.assistindia.convene.BeenClass.AppBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Operator{
	Context context;

	public String SignalStrength="";
	public String Lac="";
	public String Mcc="";
	public String Mnc="";
	public String La="";
	public String Cell_id="";
	public String NetworkType="";
	public String Phoneno="";
	public String chargepercentage="";
	public String CarrierName="";
	public String simSerialNumber="";
	public String deviceId="";
	public String mostUsedapps="";

	private MyPhoneStateListener myListener;
	List<AppBean> list;

	public Operator(Context context) {
		this.context=context;
		myListener  = new MyPhoneStateListener();
	}


	public float getBatteryLevel(Context ctx)
	{
		try
		{
			int level = 0;
			int scale = 0;
			Intent batteryIntent = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			if(batteryIntent!=null) {
				level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

				// Error checking that probably isn't needed but I added just in case.
				if (level == -1 || scale == -1) {
					return 50.0f;
				}
			}
			float result;
			if (scale!=0)
				result = ((float) level / (float) scale) * 100.0f;
			else
				result = 0.0f;
			return result;
		}
		catch(Exception e)
		{
			Logger.logE(Operator.class.getSimpleName(), "Exception in getBatteryLevel method", e);
		}
		return 0;
	}

	private class MyPhoneStateListener extends PhoneStateListener {


        @Override
        public void onSignalStrengthsChanged(android.telephony.SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                try {
					int singalStenths = signalStrength.getGsmSignalStrength();
					System.out.println("Signal Strength:  is  : " + singalStenths);
					SignalStrength = String.valueOf(singalStenths);
				} catch (Exception e) {
					Logger.logE(Operator.class.getSimpleName(), "Exception in MyPhoneStateListener Class", e);

				}

        }


	}

	public void checkOperatorStatus(int check)
	{
		if(check==1){
			MostUsedApps mostusedApps = new MostUsedApps(context);
			mostusedApps.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
		}
			TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if(tel!=null){
				try {
					if(tel.getDeviceId()!=null)
						deviceId = tel.getDeviceId();
					if(tel.getSimSerialNumber()!=null)
				    	simSerialNumber = tel.getSimSerialNumber();
					if(tel.getNetworkOperator()!=null)
					  CarrierName=tel.getNetworkOperator();
					if(tel.getLine1Number()!=null)
						Phoneno=tel.getLine1Number();
					chargepercentage = String.valueOf(getBatteryLevel(context));
					switch(tel.getNetworkType())
					{
					  case TelephonyManager.NETWORK_TYPE_1xRTT:
						  NetworkType = "CDMA - 2000";
					    break;
					  case TelephonyManager.NETWORK_TYPE_CDMA:
						  NetworkType = "CDMA";
					    break;
					  case TelephonyManager.NETWORK_TYPE_EDGE:
						  NetworkType = "GSM - EDGE";
					    break;
					  case TelephonyManager.NETWORK_TYPE_EVDO_0:
						  NetworkType = "CDMA - EVDO A";
					    break;
					  case TelephonyManager.NETWORK_TYPE_EVDO_A:
						  NetworkType = "CDMA - EVDO A";
					    break;
					  case TelephonyManager.NETWORK_TYPE_GPRS:
						  NetworkType = "GSM - GPRS";
					    break;
					  case TelephonyManager.NETWORK_TYPE_UMTS:
						  NetworkType = "UMTS";
					    break;
					  case 8:
						  NetworkType = "UMTS - HSDPA ";
					    break;
					  case 9:
						  NetworkType = "UMTS - HSUPA ";
					    break;
					  case 10:
						  NetworkType = "UMTS - HSPA ";
					    break;
					  case TelephonyManager.NETWORK_TYPE_UNKNOWN:
						  break;
					  default:
						  NetworkType = "Unknown";
						  break;
					}
					if (tel.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM || tel.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
						final GsmCellLocation locationC = (GsmCellLocation) tel.getCellLocation();
						if (locationC != null) {
							int cellId 	= locationC.getCid();
							cellId = cellId & 0xffff;
							Cell_id= String.valueOf(cellId);
							Lac= String.valueOf(locationC.getLac());
						}
					 }
					Mcc=  tel.getNetworkOperator().substring(0,3);
					Mnc = tel.getNetworkOperator().substring(3);
					tel.listen(myListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
					La = "";
				}
				catch (Exception e)
				{
					Logger.logE(Operator.class.getSimpleName(), "Exception in checkOperatorStatus method", e);
				}  
			}
	}

	private class MostUsedApps extends AsyncTask<Context, Integer, String> {
		Context mcontext;
		SharedPreferences preferences;
		public MostUsedApps(Context context){
			mcontext = context;
			list = new ArrayList<>();
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
		
		@Override
		protected String doInBackground(Context... arg0) {
				list = new ArrayList<>();
				final PackageManager pm = mcontext.getPackageManager();
				// get a list of installed apps.
				List<ApplicationInfo> packages = pm.getInstalledApplications(0);
				double totalReceived = (double) TrafficStats.getTotalRxBytes() / (1024 * 1024);
				double totalSent = (double) TrafficStats.getTotalTxBytes() / (1024 * 1024);
				// loop through the list of installed packages and see if the selected
				// app is in the list
				try
				{
					for (ApplicationInfo packageInfo : packages)
					{
						// get uId for the selected app
						int uId = packageInfo.uid;
						String packageName = packageInfo.packageName;
						double received = (double) TrafficStats.getUidRxBytes(uId) / (1024 * 1024);
						double send = (double) TrafficStats.getUidTxBytes(uId) / (1024 * 1024);
						double total = received + send;
						setDataFunction(total,packageInfo,pm,(totalReceived + totalSent),uId,packageName);
					}
					Collections.sort(list, new AppComp());
				}
				catch (Exception e)
				{
					Logger.logE(Operator.class.getSimpleName(), "Exception in doInBackground method outside loop", e);
				}

			return null;
		}
		public void setDataFunction(double total, ApplicationInfo packageInfo, PackageManager pm, double totalTraffic, int uId, String packageName ){
			try{
				if (total > 0)
				{
					AppBean appbean = new AppBean();
					String name = packageInfo.loadLabel(pm).toString();
					if (!name.contains(".") && (packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
						appbean.setAppname(name);
						appbean.setAppuid(uId);
						appbean.setTotaltraffic(total);
						double percentage = (total / totalTraffic) * 100;
						appbean.setPercentage((int) percentage);
						appbean.setPackagename(packageName);
						list.add(appbean);
					}
				}
			}
			catch (Exception e)
			{
				Logger.logE(Operator.class.getSimpleName(), "Exception in doInBackground method outside loop", e);
			}
		}
		@Override
		protected void onPostExecute(String result) {
			// Proxy the call to the Activity
			String messageContent="";
			for(int i = 0;i<list.size()&& i<=5;i++){
				String app = list.get(i).getAppname();
				if(app.contains(" ")){
					app = app.replace(" ", "");
				}
				if(list.size()!=1){
					if(i==0){
						messageContent = messageContent+app;
					}else{
						messageContent = messageContent+"-"+app;
					}
				}else{
					messageContent = app;
				}
			}
			if("".equals(messageContent)){
				messageContent = CommonForAllClasses.UNAVAILABLE;
			}
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("mostusedApps", messageContent);
			editor.apply();
			mostUsedapps=messageContent;
			
		}
		
	}
	
}
