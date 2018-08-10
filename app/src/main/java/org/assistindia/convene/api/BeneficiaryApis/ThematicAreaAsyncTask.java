package org.assistindia.convene.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.assistindia.convene.BeenClass.facilitiesBeen.FacilitiesAreaInterface;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.DirectUrlCall;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 18/8/17.
 */

public class ThematicAreaAsyncTask extends AsyncTask<Context, Integer, String> {
    private Context context;
    private DirectUrlCall resturl;
    private Activity activity;
    private SharedPreferences thematicPreferences;
    private FacilitiesAreaInterface facilitiesAreaInterface;

    /**
     * ThematicAreaAsyncTask constructor
     * @param context param
     * @param activity param
     * @param typeInterface param
     *
     */
    public ThematicAreaAsyncTask(Context context, Activity activity, FacilitiesAreaInterface typeInterface) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        this.facilitiesAreaInterface=typeInterface;
        thematicPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        List<NameValuePair> thematicParamsL = new ArrayList<>();
        String thematicResult = resturl.restUrlServerCall("facilities/thematicarealisting/", "post", thematicParamsL);
        Logger.logV("the parameters are", "the params for facilitytypeapi" + thematicParamsL);
        parseResponse(thematicResult);
        return thematicResult;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if((result).contains(Constants.HTMLSTRING)){
                facilitiesAreaInterface.getThematicAreaResponse(false);
            }else {
                facilitiesAreaInterface.getThematicAreaResponse(true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            facilitiesAreaInterface.getThematicAreaResponse(false);
        }

    }

    /**
     * parseResponse method
     * @param result param
     *
     */
    private void parseResponse(String result) {
        try {
            JSONArray jsonArray=new JSONArray(result);
            SharedPreferences.Editor editor= thematicPreferences.edit();
            editor.putString("THEMATIC_AREA_UID",jsonArray.toString());
            editor.apply();
        } catch (Exception e) {
            Logger.logE("Exception","Exception in facility async task",e);
        }
    }
}

