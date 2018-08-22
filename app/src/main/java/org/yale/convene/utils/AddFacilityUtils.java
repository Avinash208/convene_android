package org.yale.convene.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.yale.convene.BeenClass.boundarylevel.LocationType;
import org.yale.convene.BeenClass.facilitiesBeen.FacilitiesAreaBeen;
import org.yale.convene.BeenClass.service.Datum;
import org.yale.convene.database.ExternalDbOpenHelper;

import java.util.List;

/**
 * Created by mahiti on 22/2/18.
 */

public class AddFacilityUtils {

    private  Activity activity;
    private SharedPreferences sharedPreferences1;
    private ExternalDbOpenHelper dbhelper;


    public AddFacilityUtils(Activity activity) {
        this.activity=activity;
        sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        dbhelper = ExternalDbOpenHelper.getInstance(activity, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("inv_id", ""));
    }


    /**
     *
     * @param serviceList
     * @param face
     * @param dynamicCheckBox
     */
    public void setServiceToCheckBox(List<Datum> serviceList, Typeface face, LinearLayout dynamicCheckBox) {
        for (int j = 0; j < serviceList.size(); j++) {
            CheckBox dynamicCheck = new CheckBox(activity);
            dynamicCheck.setText(serviceList.get(j).getName());
            dynamicCheck.setTag(serviceList.get(j).getId());
            dynamicCheck.setChecked(false);
            dynamicCheck.setTextSize(14);
            dynamicCheck.setTypeface(face);
            dynamicCheckBox.addView(dynamicCheck);
        }
    }

    /**
     * method to set the selected services in checkbox
     * @param dynamicCheckBox
     * @param j
     * @param servicesEditedList
     * @param serviceList
     */
    public void setEdittedServiceList(CheckBox dynamicCheckBox, int j, List<Integer> servicesEditedList, List<Datum> serviceList) {
        for (int i = 0; i < servicesEditedList.size(); i++) {
            if (serviceList.get(j).getId().equals(servicesEditedList.get(i))) {
                dynamicCheckBox.setChecked(true);
            }
        }
    }

    /**
     * method to set the selected location type in spinner
     *  @param slugName
     * @param spLocationLevel
     * @param ruralLocationType
     * @param locationList
     */
    public void setLocationType(String slugName, Spinner spLocationLevel, String ruralLocationType, List<LocationType> locationList) {
        if ((ruralLocationType).equalsIgnoreCase(slugName)) {
            spLocationLevel.setSelection(locationList.indexOf(locationList.get(0)));
        } else {
            spLocationLevel.setSelection(locationList.indexOf(locationList.get(1)));
        }
    }

    /**
     * method to set the selected thematic area to spinner
     *
     * @param thematicAreaString
     * @param spinner
     * @param thematicAreaList
     */
    public void setEdittedThematicArea(String thematicAreaString, Spinner spinner, List<FacilitiesAreaBeen> thematicAreaList) {
        for (int i = 0; i < thematicAreaList.size(); i++) {
            if (thematicAreaString.equalsIgnoreCase(thematicAreaList.get(i).getName())) {
                spinner.setSelection(i);
            }
        }
    }
}
