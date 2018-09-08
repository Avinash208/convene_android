package org.fwwb.convene.convenecode;

import android.location.Location;

/**
 * Created by sandeep HR on 10/01/17.
 */

public class FetchLocation {

    private double currentLatitude;
    private double CurrentLongitude;

    private double selectedLatitude;
    private double selectedLongitude;

    Location location;
    private static FetchLocation fetchLocation=new FetchLocation();

    private FetchLocation() {

    }

    public static FetchLocation getInstance(){
        if(fetchLocation == null) {
            fetchLocation = new FetchLocation();
        }
        return fetchLocation;
    }


    public double getCurrentLatitude() {
        return currentLatitude;
    }


    /**
     *
     * @param currentLatitude
     */
    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }


    /**
     *
     * @return
     */
    public double getCurrentLongitude() {
        return CurrentLongitude;
    }


    /**
     *
     * @param currentLongitude
     */
    public void setCurrentLongitude(double currentLongitude) {
        CurrentLongitude = currentLongitude;
    }


    /**
     *
     * @return
     */
    public double getSelectedLatitude() {
        return selectedLatitude;
    }


    /**
     *
     * @param selectedLatitude
     */
    public void setSelectedLatitude(double selectedLatitude) {
        this.selectedLatitude = selectedLatitude;
    }

    public double getSelectedLongitude() {
        return selectedLongitude;
    }

    /**
     *
     * @param selectedLongitude
     */
    public void setSelectedLongitude(double selectedLongitude) {
        this.selectedLongitude = selectedLongitude;
    }
}
