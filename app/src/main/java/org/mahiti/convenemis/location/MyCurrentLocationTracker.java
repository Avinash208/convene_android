package org.mahiti.convenemis.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;


public class MyCurrentLocationTracker implements LocationListener {
	Context ctx;
	LocationManager locationManager;
	Location locations = null;
	String provider = null;
	String latLong = "";
	TextView lat, lang;

	/**
	 *
	 * @param ctx
	 * @param lat
	 * @param lang
	 */
	public MyCurrentLocationTracker(Activity ctx, TextView lat, TextView lang) {
		if (null != ctx) {
			this.ctx = ctx;
			this.lat = lat;
			this.lang = lang;

			callCriteria();
		}
	}

	public void callCriteria() {
		try {
			locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
			if (null != locationManager) {
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
				criteria.setCostAllowed(true);
				provider = locationManager.getBestProvider(criteria, true);
				getLocation(lat, lang);
			}
		} catch (Exception e) {
			Logger.logE("MyCurrentLocation", "callCriteria exception ", e);
		}
	}

	/**
	 *
	 * @param lat
	 * @param lang
	 */
	public Location getLocation(TextView lat, TextView lang) {
		locations = null;
		if (locationManager != null) {
			if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				 //                                         int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.

			}
			locations = locationManager.getLastKnownLocation(provider);
			 if(locations!=null)
			 {
				 latLong = getLocationLatLong(locations,lat,lang);
				 Logger.logV("MyCurrentLocationTracker","getLastKnownLocation : "+latLong);
			 }
		}
		return locations;
	}

	/**
	 *
	 * @param location
	 */
	@Override
	public void onLocationChanged(Location location) {
		if(location!=null){
			latLong=getLocationLatLong(location,lat,lang);
			locations = location;
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// not used
	}

	@Override
	public void onProviderEnabled(String provider) {
		// not used
	}

	@Override
	public void onProviderDisabled(String provider) {
		// not used
	}

	/**
	 *
	 * @param location
	 * @param lat
	 * @param lang
	 * @return
	 */
	private String getLocationLatLong(Location location, TextView lat, TextView lang)
	{
		if(location!=null){
			if(lat!=null && lang!=null){
				lat.setText(String.valueOf(locations.getLatitude()));
				lang.setText(String.valueOf(locations.getLongitude()));
				Constants.lat_i=String.valueOf(locations.getLatitude());
				Constants.lang_i=String.valueOf(locations.getLongitude());
			}
			return locations.getLatitude() + "#" + locations.getLongitude();
		}
		else
		{
			return "";
		}
	}
}
