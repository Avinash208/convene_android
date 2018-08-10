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


public class BeneficiaryTypeAsyncTask extends AsyncTask<Context, Integer, String> {
    private Context context;
    private DirectUrlCall resturl;
    private Activity activity;
    private SharedPreferences beneficiaryTypePreferences;
    private BeneficaryTypeInterface facilityTypeInterface;

    /**
     * BeneficiaryTypeAsyncTask constructor
     * @param context param
     * @param activity param
     * @param typeInterface PARAM
     */
    public BeneficiaryTypeAsyncTask(Context context, Activity activity, BeneficaryTypeInterface typeInterface) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        this.facilityTypeInterface=typeInterface;
        beneficiaryTypePreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";

        }
        List<NameValuePair> beneficiaryTypeParamsL = new ArrayList<>();
        String beneficiaryResult = resturl.restUrlServerCall("beneficiary/btype/listing/withoutpagination/", "post", beneficiaryTypeParamsL);
        Logger.logV("the parameters are", "the params for facilitytypeapi" + beneficiaryTypeParamsL);
        parseResponse(beneficiaryResult);
        return beneficiaryResult;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            if((result).contains(Constants.HTMLSTRING)){
                facilityTypeInterface.onSuccessBeneficiaryResponse(result,false);
            }else {
                facilityTypeInterface.onSuccessBeneficiaryResponse(result,true);
            }
        }catch (Exception e){
           Logger.logE("","",e);
            facilityTypeInterface.onSuccessBeneficiaryResponse(result,false);
        }

    }

    /**
     * parseResponse method
     * @param result param
     */
    private void parseResponse(String result) {
        try {
            JSONArray jsonArray=new JSONArray(result);
            SharedPreferences.Editor editor= beneficiaryTypePreferences.edit();
            editor.putString("BENEFICIARY_TYPES_UID",jsonArray.toString());
            editor.apply();
        } catch (Exception e) {
            Logger.logE("Exception","Exception in facility async task",e);
        }
    }
}

