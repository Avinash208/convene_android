package org.assistindia.convene.location;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.assistindia.convene.R;
import org.assistindia.convene.utils.Logger;


public class GPSTracker extends Service implements LocationListener {
	private Context mContext;
	// flag for GPS Status
	public boolean isGPSEnabled = false;
	// flag for network status
	boolean isNetworkEnabled = false;

	public boolean canGetLocationFlag = false;
	public int gps_tracker = 0;

	Location location = null;
	public double latitude;
	public double longitude;

	// The minimum distance to change updates in metters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;
	SharedPreferences sp;
	private static GPSTracker gpsTracker;
	public final String TAG = "GPSTracker";

	public GPSTracker(Context context) {
		this.mContext = context;
		sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		getLocation();
	}

	/**
	 * @param context      Context for calling constructor
	 * @return
	 */
	public static GPSTracker getInstance(Context context) {
		if (gpsTracker == null) {
			gpsTracker = new GPSTracker(context);
		}
		return gpsTracker;
	}

	public GPSTracker() {
		// default constructor
	}

	public Location getLocation() {

		try {
			location = null;
			ConnectivityManager connManager = (ConnectivityManager) ((ContextWrapper) mContext).getBaseContext().getSystemService(CONNECTIVITY_SERVICE);

			State mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

			//wifi
			State wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			Logger.logV("the prefferenece value is", "the prefreence values are" + sp.getString("LocationSelectionMode", ""));
			if ("1".equals(sp.getString("LocationSelectionMode", ""))) {
				Logger.logV("GPS", "internet location capturing its going first if block");
				// getting network status
				isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				// if GPS Enabled get lat/long using GPS Services
				if (isNetworkEnabled && (mobile == State.CONNECTED || mobile == State.CONNECTING || wifi == State.CONNECTED || wifi == State.CONNECTING)) {
					Logger.logV("GPS", "internet location capturing");
					this.canGetLocationFlag = true;
					try {
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());
						getLocationUpdates();
					}catch(SecurityException e){
						Logger.logE("","getLocation catch - ",e);
					}

				} else {
					Logger.logV("GPS", "internet location capturing its going else  block");

					if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
							PackageManager.PERMISSION_GRANTED &&
							ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
									PackageManager.PERMISSION_GRANTED) {

					} else {
						callGpsEnabled();
					}
				}
			} else {
				Logger.logV("GPS", "internet location capturing its going else  block");
				callGpsEnabled();
			}

		} catch (Exception e) {
			Logger.logE(GPSTracker.class.getSimpleName(), "Exception in getLocation method ", e);

		}

		return location;
	}

	private void callGpsEnabled() {
	//	turnGPSOn();
		// getting GPS status
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		double lat = 0.0;
		// First get location from Network Provider
		if (location == null || (Double.compare(location.getLatitude(), lat) == 0 && Double.compare(location.getLongitude(), lat) == 0)) {
			try {
				if (isGPSEnabled) {
					Logger.logV("GPS", "gps location capturing");
					this.canGetLocationFlag = true;
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							gps_tracker = 0;
							updateGPSCoordinates(location);
							Logger.logV("GPS", "gps location captured" + location.getLatitude());
							Logger.logV("GPS", "gps location captured" + location.getLongitude());
						}
					}
				} else {
					location = null;
				}
			}catch (SecurityException e){
				Logger.logE("GPSTracker","callGPSEnabled",e);
			}
		}
	}


	private void turnGPSOn() {
		String provider = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		try {
			if (!"gps".equalsIgnoreCase(provider)) { //if gps is disabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				 mContext.sendBroadcast(poke);
			}
		} catch (Exception e) {
			Logger.logE("GpsTracker","Exception on turnGPSOn",e);
		}
	}

	public void getLocationUpdates() {
		if (locationManager != null) {
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				gps_tracker = 1;
				updateGPSCoordinates(location);
			}
		}
	}

	public void updateGPSCoordinates(Location location) {
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 */

	public void stopUsingGPS() {
		if (locationManager != null) {
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locationManager.removeUpdates(GPSTracker.this);
		}
	}


	/**
	 * Function to check GPS/wifi enabled
	 */
	public boolean canGetLocation() {
		return this.canGetLocationFlag;
	}

	/**
	 * Function to show settings alert dialog
	 */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle(mContext.getString(R.string.enableGps));

		// Setting Dialog Message
		alertDialog.setMessage(mContext.getString(R.string.gpsAlert));

		// On Pressing Setting button
		alertDialog.setPositiveButton(mContext.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
					}
				});

		// On pressing cancel button
		alertDialog.setNegativeButton(mContext.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		updateGPSCoordinates(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		/*Nothing to do in this method   */
	}

	@Override
	public void onProviderEnabled(String provider) {
				/*Nothing to do in this method   */

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
				/*Nothing to do in this method   */


	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
