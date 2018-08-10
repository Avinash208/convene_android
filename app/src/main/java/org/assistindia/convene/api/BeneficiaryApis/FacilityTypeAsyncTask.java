package org.assistindia.convene.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.DirectUrlCall;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.Utils;

import java.util.ArrayList;
import java.util.List;



public class FacilityTypeAsyncTask extends AsyncTask<Context, Integer, String> {
    private Context context;
    private DirectUrlCall resturl;
    private Activity activity;
    private SharedPreferences facilityPreferences;
    private FacilityTypeInterface facilityTypeInterface;
    private static final String TAG="FacilityTypeAsyncTask";

    /**
     * FacilityTypeAsyncTask constructor
     * @param context param
     * @param activity param
     * @param typeInterface param
     */
    public FacilityTypeAsyncTask(Context context, Activity activity, FacilityTypeInterface typeInterface) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        this.facilityTypeInterface=typeInterface;
        facilityPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";

        }
        List<NameValuePair> facilityParamsL = new ArrayList<>();
        String result = resturl.restUrlServerCall("facilities/facilitytype/", "post", facilityParamsL);
        Logger.logV(TAG, "the params for facilitytypeapi" + facilityParamsL);
        parseResponse(result);
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if((result).contains(Constants.HTMLSTRING)){
                facilityTypeInterface.onSuccessFaciltyResponse(false);
            }else {
                facilityTypeInterface.onSuccessFaciltyResponse(true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            facilityTypeInterface.onSuccessFaciltyResponse(false);
        }

    }

    /**
     * parseResponse method
     * @param result param
     */
    private void parseResponse(String result) {
        try {
            JSONArray jsonArray=new JSONArray(result);
            SharedPreferences.Editor facilityEditor= facilityPreferences.edit();
            facilityEditor.putString("FACILITY_TYPES_UID",jsonArray.toString());
            facilityEditor.apply();
        } catch (Exception e) {
            Logger.logE(TAG,"Exception in facility async task",e);
        }
    }
}

