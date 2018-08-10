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

/**
 * Created by mahiti on 18/8/17.
 */

public class AddressProofAsyncTask extends AsyncTask<Context, Integer, String> {
    private Context context;
    private DirectUrlCall resturl;
    private Activity activity;
    private SharedPreferences addressPreference;
    private AddressProofsInterface addressProofsInterface;

    /**
     * {@link AddressProofAsyncTask constructor}
     * @param context param
     * @param activity param
     * @param addressProofsInterface param
     */
    public AddressProofAsyncTask(Context context, Activity activity, AddressProofsInterface addressProofsInterface) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        this.addressProofsInterface=addressProofsInterface;
        addressPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        List<NameValuePair> thematicParamsL = new ArrayList<>();
        String thematicResult = resturl.restUrlServerCall("beneficiary/addressproof/masterlisting/", "post", thematicParamsL);
        Logger.logV("the parameters are", "the params for address master listing" + thematicParamsL);
        parseResponse(thematicResult);
        return thematicResult;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {

            if((result).contains(Constants.HTMLSTRING)){
                addressProofsInterface.saveAddressToPreference(false);
            }else {
                addressProofsInterface.saveAddressToPreference(true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            addressProofsInterface.saveAddressToPreference(false);
        }

    }

    /**
     * parseResponse method
     * @param result param
     */
    private void parseResponse(String result) {
        try {
            JSONArray jsonArray=new JSONArray(result);
            SharedPreferences.Editor editor= addressPreference.edit();
            editor.putString("ADDRESS_PROOF_UID",jsonArray.toString());
            editor.apply();
        } catch (Exception e) {
            Logger.logE("Exception","Exception in facility async task",e);
        }
    }
}

