package org.yale.convene;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yale.convene.database.DBController;
import org.yale.convene.location.GPSTracker;
import org.yale.convene.utils.CheckNetwork;
import org.yale.convene.utils.ClusterInfo;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.Operator;
import org.yale.convene.utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AlarmService extends BroadcastReceiver {
	Context ctx;
	SharedPreferences preferences;
	double latitude;
	double longitude;
	String tagClassName = AlarmService.class.getSimpleName();
	Map<String, String> params = new HashMap<>();
	String cellId ="";
	String signalStrength ="";
	String chargePercentage ="";
	String lastChargeTime ="";
	String simSerialNum="";
	String deviceId="";
	String mostUsedApps ="";
	long totalSpace;
	DBController db;
	CheckNetwork chckNetwork ;
	String space =" ";

	@Override
	public void onReceive(Context context, Intent intent) {

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if(pm==null)
			return;
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		ctx = context;
		preferences= PreferenceManager.getDefaultSharedPreferences(ctx);
		db=new DBController(context);
   		new Operator(ctx).checkOperatorStatus(1);
   		wl.release();
		chckNetwork = new CheckNetwork(ctx);
		sendTableDetails(context);
	}
	
	public void SetAlarm(Context context) {
		try {
			Log.v(tagClassName, "Started alarm");
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(context, AlarmService.class);
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
			preferences= PreferenceManager.getDefaultSharedPreferences(context);
			int alaramValue=preferences.getInt("alaramFrequency",2);
			int alarmdurationmins = 1000*60*alaramValue;
			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
					alarmdurationmins, pi);
			Log.v(tagClassName, "Started alarmm.............................."+alarmdurationmins);
		}
		catch(Exception e){
			Logger.logE("","",e);
		}
	}
   public void insertRecordsToDB(Context ctx)
   {
	   StringBuilder message;
	   preferences= PreferenceManager.getDefaultSharedPreferences(ctx);
	   mostUsedApps = preferences.getString("mostusedApps", "");

	   message=new StringBuilder();
	   String subject="CRY ";
	   Operator optObject = new Operator(ctx);
	   SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ssaa",Locale.US);
	   String formattedDate = dateFormat.format(new Date());
	   if(null != optObject.deviceId && !"".equalsIgnoreCase(optObject.deviceId)){deviceId=optObject.deviceId;}
	   if(null != optObject.Cell_id && !"".equalsIgnoreCase(optObject.Cell_id)){
		   cellId =optObject.Cell_id;}
	   if(null != optObject.SignalStrength && !"".equalsIgnoreCase(optObject.SignalStrength)){
		   signalStrength =optObject.SignalStrength;}
	   if(null != optObject.chargepercentage && !"".equalsIgnoreCase(optObject.chargepercentage)){
		   chargePercentage =optObject.chargepercentage.replace("%","");}
	   if(!"".equals(preferences.getString("Time", "")))
	   {
		   lastChargeTime =preferences.getString("Time","");
	   }
	   else
	   {
		   lastChargeTime =formattedDate;
	   }
	   if(null != optObject.simSerialNumber && !"".equalsIgnoreCase(optObject.simSerialNumber)){ simSerialNum=optObject.simSerialNumber;}
	   long freeBytesInternal = new File(ctx.getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
	   long freeBytesExternal = new File(ctx.getExternalFilesDir(null).toString()).getFreeSpace();
	   double internalSpace = (double) freeBytesInternal / 1024;
	   internalSpace = Math.round(internalSpace / 1024);
	   double externalSpace = (double) freeBytesExternal / 1024;
	   externalSpace = Math.round(externalSpace / 1024);
	   double totalFreeSpace=internalSpace+externalSpace;
	   totalSpace = Math.round(totalFreeSpace);
	   if(totalSpace <0){
		   totalSpace = 0;
	   }
	   if(mostUsedApps ==null || "".equals(mostUsedApps)){
		   mostUsedApps ="";
	   }

	   GPSTracker tracker=new GPSTracker(ctx);
	   Location location=tracker.getLocation();
	   updateGPSCoordinates(location);
	   String timeStamp = formattedDate;
	   message.append(cellId).append(space)
			   .append(signalStrength).append(space)
			   .append(chargePercentage).append(space)
			   .append(lastChargeTime).append(space)
			   .append(simSerialNum).append(space)
			   .append(deviceId).append(space)
			   .append(totalSpace).append(space)
			   .append(mostUsedApps).append(space)
			   .append(timeStamp).append(space)
			   .append(latitude).append(space)
			   .append(longitude).append(space)
			   .append(preferences.getString("inv_id", "1"));
	   String messageBody=subject+message.toString();
	   Log.e("Message", "MessageBody : " + messageBody);

	   db.insertdeviceDetailsDB(cellId, signalStrength, chargePercentage, lastChargeTime, simSerialNum, deviceId,
			   String.valueOf(totalSpace), mostUsedApps, timeStamp,
			   String.valueOf(latitude), String.valueOf(longitude), preferences.getString("uId", ""));
   }

	public void sendTableDetails(Context context)
	{
		Logger.logV("sendTableDetails ...........","sendTableDetails.....................");

		insertRecordsToDB(context);
		if (chckNetwork.checkNetwork()) {
			String url = ctx.getString(R.string.main_url) + ctx.getString(R.string.captureTabInfoUrl);
			StringRequest postRequest = new StringRequest(Request.Method.POST, url,
					response -> parseResponse(response),
					error -> error.printStackTrace()
			) {
				@Override
				protected Map<String, String> getParams() {
					params = new HashMap<>();
					try {
						JSONArray json=db.getDeviceDetails();
						Logger.logV("the cellid is","DeviceDetails"+json);
						params.put("DeviceDetails", String.valueOf(json));
					}
					catch (Exception e)
					{
						Logger.logE("AlaramService","db insert alram",e);
					}
					return params;
				}
			};
			Volley.newRequestQueue(ctx).add(postRequest);
		}
	}

	public void updateGPSCoordinates(Location location) {
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
	}
	/**
	 *
	 * @param result
	 */
	public void parseResponse(String result) {
		try {
			if (result == null) {
				ToastUtils.displayToast(ctx.getString(R.string.checkNet), ctx);
				return;
			}
			if ("".equals(result)) {
				ToastUtils.displayToast(preferences.getString(ClusterInfo.emptyResponse, ""),ctx);
				return;
			}
			final JSONObject json = new JSONObject(result);
			Log.v("the result is", "the result is" + result);
			String res = json.getString("status");
			/*Response type 0 is for failure*/
			if ("true".equals(res)) {
				db.deleteTableDetails();
				return;
			}
			/* Response type 2 is for displaying the toast saying that the time is more than 10 minutes different*/
		}
		catch (Exception e)
		{
			Logger.logE("","",e);
		}
	}


}
