package org.yale.convene.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.DirectUrlCall;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 18/8/17.
 */

public class GetLocationLevelAsyncTask extends AsyncTask<Context, Integer, String> {
    Context context;
    DirectUrlCall resturl;
    Activity activity;
    SharedPreferences preferences;
    GetLocationLevelInterface facilitiesAreaInterface;

    /**
     * GetLocationLevelAsyncTask constructor
     * @param context param
     * @param activity param
     * @param typeInterface param
     */
    public GetLocationLevelAsyncTask(Context context, Activity activity, GetLocationLevelInterface typeInterface) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        this.facilitiesAreaInterface=typeInterface;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        List<NameValuePair> paramsL = new ArrayList<>();
        paramsL.add(new BasicNameValuePair("slug", "location-type"));
        paramsL.add(new BasicNameValuePair("key", "0"));
        String result = resturl.restUrlServerCall("masterdata/master/lookup/location-type/listing/", "post", paramsL);
        Logger.logV("the parameters are", "the params for location-type api" + paramsL);
        parseResponse(result);
        return result;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if((result).contains(Constants.HTMLSTRING)){
                facilitiesAreaInterface.onSuccessLocationLevelResponse(false);
            }else {
                facilitiesAreaInterface.onSuccessLocationLevelResponse(true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            facilitiesAreaInterface.onSuccessLocationLevelResponse(false);
        }

    }

    /**
     * parseResponse method
     * @param result param
     */
    private void parseResponse(String result) {
        try {
            JSONObject jsonObject=new JSONObject(result);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("LOCATION_LEVELTYPE_UID",jsonObject.toString());
            editor.apply();
        } catch (Exception e) {
            Logger.logE("Exception","Exception in facility async task",e);
        }
    }
}

