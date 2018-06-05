package org.mahiti.convenemis.receivers;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.AutoSyncActivity;
import org.mahiti.convenemis.R;
import org.mahiti.convenemis.api.BeneficiaryApis.SaveBeneficiary;
import org.mahiti.convenemis.api.BeneficiaryApis.SaveFacility;
import org.mahiti.convenemis.api.BeneficiaryApis.UpdateBeneficiary;
import org.mahiti.convenemis.api.BeneficiaryApis.UpdateFacility;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;

import java.util.HashMap;
import java.util.Map;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String TAG = "ConnectivityReceiver";
    Context context;
    SharedPreferences preferences;
    ExternalDbOpenHelper externaldbhelper;
    private DBHandler dbHandlershowMember;
    private static final int APIUPDATECODE = 200;
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
        Logger.logD(TAG, "ConnectivityReceiver  ");
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        Logger.logD(TAG, "onReceive ");
        this.context = context;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }

        if (!isConnected) {
            Logger.logD("Connectivity", "No internet connection (receiver)");
            return;
        }
        try {
            Logger.logD(TAG, " net check ");
            SQLiteDatabase.loadLibs(context);
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            externaldbhelper = ExternalDbOpenHelper.getInstance(context, preferences.getString(Constants.DBNAME, ""), preferences.getString("UID", ""));
            dbHandlershowMember = new DBHandler(context);
            getBeneFacilitySyncCount(context);
        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }


    private void getBeneFacilitySyncCount(Context context) {
        try {
            if (!externaldbhelper.getSyncBeneficiaryOfflineRecord().isEmpty()) {
                new SaveBeneficiary(context, context.getString(R.string.main_Url) + "beneficiary/addhousehold/").execute();
            }
            if (!externaldbhelper.getSyncFacilityOfflineRecord().isEmpty()) {
                new SaveFacility(context, context.getString(R.string.main_Url) + "facilities/facilityadd/").execute();
            }
            if (!externaldbhelper.getBeneficiaryDetails().isEmpty()) {
                new UpdateBeneficiary(context, context.getString(R.string.main_Url) + "beneficiary/householdupdate/").execute();
            } else {
                Logger.logD("", "no updated Beneficiary records to sync to backend");
            }
            if (!externaldbhelper.getFacilityDetails().isEmpty()) {
                new UpdateFacility(context, context.getString(R.string.main_Url) + "facilities/facilityupdate/").execute();
            } else {
                Logger.logD("", "no updated datumFacilityList records to sync to backend");
            }
            AutoSyncActivity autoSyncObj = new AutoSyncActivity(context);
            autoSyncObj.callingAutoSync(1);

            updateBeneficiaryLinkage();

        } catch (Exception e) {
            Logger.logE("", "", e);
        }
    }

    private void updateBeneficiaryLinkage() {
        JSONArray syncOfflineResponse= dbHandlershowMember.getOfflineRecord(dbHandlershowMember);
        Logger.logD("syncOfflineResponse", syncOfflineResponse.toString());
        if (syncOfflineResponse.length()>0){
            HashMap<String,String> beneficiaryLinkageParms= new HashMap<>();
            beneficiaryLinkageParms.put("URL","/api/beneficiary-link/");
            beneficiaryLinkageParms.put("user_id",preferences.getString("UID", ""));
            beneficiaryLinkageParms.put("linkages",syncOfflineResponse.toString());
            callServerApi(context,beneficiaryLinkageParms,null, APIUPDATECODE);

        }
    }

    public void callServerApi(final Context activity, final Map<String, String> params, final ProgressDialog progressDialog, final int code) {

        String URL = activity.getResources().getString(R.string.main_Url) + params.get("URL");
        Log.v("the result is", "the result is" + URL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.v("the result is", "the result is" + response);
                        updateSyncResponse(response);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Logger.logD("Tag", "Params" + params.toString());
                return params;
            }


           /* @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> paramsHeader = new HashMap<>();
                paramsHeader.put("un", "WYPO");
                paramsHeader.put("pw", "VD0+)&lrYlUiUcl^8%a~");
                return paramsHeader;
            }*/

        };
        try {
            postRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(activity).add(postRequest);
        } catch (Exception e) {
            Logger.logE("exception", "call server api ", e);


        }

    }

    private  void updateSyncResponse(String response) {
        try {
            JSONObject jsonObject= new JSONObject(response);
            if(jsonObject.getInt("status")==2){
                JSONArray jsonArray= jsonObject.getJSONArray("success_records");
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    String getUUID= jsonObject1.getString("uuid");
                    String created_on= jsonObject1.getString("created_on");
                    int updatedResponseKey=dbHandlershowMember.updateBeneficiaryLinkageStatus(getUUID,created_on,dbHandlershowMember);
                    Logger.logD("response Updated successfullty",updatedResponseKey+"");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}


