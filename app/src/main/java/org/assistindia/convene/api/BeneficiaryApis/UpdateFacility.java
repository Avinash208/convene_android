package org.assistindia.convene.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.assistindia.convene.BeenClass.facilities.Facility;
import org.assistindia.convene.MyIntentServiceFacility;
import org.assistindia.convene.database.ExternalDbOpenHelper;
import org.assistindia.convene.network.DirectUrlCall;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.ToastUtils;
import org.assistindia.convene.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UpdateFacility extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private DirectUrlCall resturl;
    private Context context;
    Activity activity;
    String result = "";
    private String apiName;
    private ExternalDbOpenHelper dbOpenHelper;
    SharedPreferences preferences;
    private static final String TAG = "UpdateFacility";
    private static final String PREF_UPDATEFACILITY = "UpdateFacility";
    private String facilityType;

    /*
     * Calling categories task constructor
     */
    public UpdateFacility(Context context, String nameOfApi) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        apiName = nameOfApi;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbOpenHelper = ExternalDbOpenHelper.getInstance(context, preferences.getString(Constants.DBNAME, ""), preferences.getString("inv_id", ""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        try {
            List<NameValuePair> updateFacilityParamsL = new ArrayList<>();
            List<Facility> facilityDetailDatumList = dbOpenHelper.getFacilityDetails();
            for (int i = 0; i < facilityDetailDatumList.size(); i++) {
                updateFacilityParamsL.add(new BasicNameValuePair("partner_id", String.valueOf(preferences.getInt("partner_id", 0))));
                updateFacilityParamsL.add(new BasicNameValuePair("name", facilityDetailDatumList.get(i).getName()));
                facilityType = facilityDetailDatumList.get(i).getName();
                updateFacilityParamsL.add(new BasicNameValuePair("facility_type_id", String.valueOf(facilityDetailDatumList.get(i).getFacilityTypeId())));
                updateFacilityParamsL.add(new BasicNameValuePair("facility_subtype_id", String.valueOf(facilityDetailDatumList.get(i).getFacilitySubtypeId())));
                updateFacilityParamsL.add(new BasicNameValuePair("thematic_area_id", String.valueOf(facilityDetailDatumList.get(i).getThematicAreaId())));
                updateFacilityParamsL.add(new BasicNameValuePair("services", String.valueOf(facilityDetailDatumList.get(i).getServices())));
                updateFacilityParamsL.add(new BasicNameValuePair("address1", facilityDetailDatumList.get(i).getAddress1()));
                updateFacilityParamsL.add(new BasicNameValuePair("address2", facilityDetailDatumList.get(i).getAddress2()));
                updateFacilityParamsL.add(new BasicNameValuePair("boundary_id", String.valueOf(facilityDetailDatumList.get(i).getBoundaryId())));
                updateFacilityParamsL.add(new BasicNameValuePair("pincode", String.valueOf(facilityDetailDatumList.get(i).getPincode())));
                updateFacilityParamsL.add(new BasicNameValuePair("uuid", String.valueOf(facilityDetailDatumList.get(i).getUuid())));
                updateFacilityParamsL.add(new BasicNameValuePair("id",String.valueOf(facilityDetailDatumList.get(i).getId())));
                result = resturl.restUrlServerCall(context, apiName, "post", updateFacilityParamsL, "");
                Logger.logV(TAG, "the params" + updateFacilityParamsL);
                methodToCallUpdateView(result, facilityDetailDatumList, i);
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }

        return result;
    }

    private void methodToCallUpdateView(String result, List<Facility> facilityDetailDatumList, int i) {
        try {
            JSONObject object = new JSONObject(result);
            if (object.getInt("status") == 2) {
                Logger.logV(TAG, "CallIntentServeice to Update Beneficiary UI -->");
                int updateRecord = 0;
                updateRecord = dbOpenHelper.updateFacilityDetails(facilityDetailDatumList.get(i).getUuid());
                Logger.logV(TAG, "Facility table has been updated" + updateRecord);
                Intent updateFacilityIntent = new Intent(context, MyIntentServiceFacility.class);
                context.startService(updateFacilityIntent);
            }
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
    }


    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            JSONObject object = new JSONObject(result);
            if (object.getInt("status") == 2) {
                ToastUtils.displayToast(facilityType + object.getString("msg"), context);

            } else {
                ToastUtils.displayToast(object.getString("msg"), context);

            }
            SharedPreferences.Editor editor = preferences.edit();
            if ("FACILITY".equalsIgnoreCase(preferences.getString("SELECTED_PAGE", ""))) {
                editor.putBoolean("UpdateBeneficiary", false);
                editor.putBoolean("isEditBeneficiary", false);
                editor.putBoolean("UpdateUi", false);
                editor.putBoolean("isEditFacility", true);
                editor.putBoolean(PREF_UPDATEFACILITY, true);
                editor.putBoolean("UpdateFacilityUi", true);
            } else {
                editor.putBoolean("UpdateUi", true);
                editor.putBoolean("UpdateBeneficiary", true);
                editor.putBoolean("isEditBeneficiary", true);
                editor.putBoolean("isEditFacility", false);
                editor.putBoolean(PREF_UPDATEFACILITY, false);
                editor.putBoolean("UpdateFacilityUi", false);
            }
            editor.apply();
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }

    }

}
