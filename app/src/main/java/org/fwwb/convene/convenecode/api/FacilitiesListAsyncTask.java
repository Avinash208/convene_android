package org.fwwb.convene.convenecode.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.fwwb.convene.convenecode.BeenClass.facilities.FacilitiesList;
import org.fwwb.convene.convenecode.BeenClass.facilities.FacilityListInterface;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.DirectUrlCall;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.ProgressUtils;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.utils.Utils;

import java.util.ArrayList;
import java.util.List;



public class FacilitiesListAsyncTask extends AsyncTask<Context, Integer, String> {

    private static final String TAG = "FacilitiesListAsyncTask";
    private DirectUrlCall resturl;
    private Context context;
    Activity activity;
    String result = "";
    android.app.ProgressDialog loginDialog;
    private FacilityListInterface facilityListInterface;
    ExternalDbOpenHelper facilityDbhelper;
    SharedPreferences facilityPreferences;
    private static final String PARTNER_ID ="partner_id";

    public FacilitiesListAsyncTask(Context context, Activity activity, FacilityListInterface houseHoldActivity) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        facilityListInterface = houseHoldActivity;
        facilityPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        facilityDbhelper =  ExternalDbOpenHelper.getInstance(context, facilityPreferences.getString(Constants.DBNAME,""), facilityPreferences.getString("UID",""));

    }

    protected void onPreExecute() {
        super.onPreExecute();
        loginDialog = ProgressUtils.showProgress(activity, false, "Loading...");
        loginDialog.show();
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        try {
            facilityDbhelper = new ExternalDbOpenHelper(this.context, facilityPreferences.getString(Constants.DBNAME, ""), this.facilityPreferences.getString("uId", ""));
            List<NameValuePair> facilityListParamsL = new ArrayList<>();
            facilityListParamsL.add(new BasicNameValuePair("modified_date", facilityDbhelper.getLastUpDate("Facility", "modified", facilityDbhelper)));
            facilityListParamsL.add(new BasicNameValuePair(PARTNER_ID,String.valueOf(facilityPreferences.getInt(PARTNER_ID,0))));
            result = resturl.restUrlServerCall("facilities/facilitydatewiselisting/", "post", facilityListParamsL);
            Logger.logV("the parameters are", "the params" + facilityListParamsL);
            parseResponse(result);
        } catch (Exception e) {
            Logger.logE(TAG,e.getMessage(), e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            if((categoryList).contains(Constants.HTMLSTRING)){
                ProgressUtils.CancelProgress(loginDialog);
                facilityListInterface.onSuccessFacilityUpdate(false);
            }else {
                ProgressUtils.CancelProgress(loginDialog);
                try {
                    JSONObject jsonObject = new JSONObject(categoryList);
                    int status = jsonObject.getInt("status");
                    if (status == 2) {
                        if (facilityListInterface != null) {
                            facilityListInterface.onSuccessFacilityUpdate(true);
                        }
                    } else {
                        ToastUtils.displayToast("Something went Wrong", context);
                    }
                } catch (Exception e) {
                    Logger.logE(TAG, e.getMessage(), e);
                }
            }

        }catch (Exception e){
            Logger.logE("","",e);
            ProgressUtils.CancelProgress(loginDialog);
            facilityListInterface.onSuccessFacilityUpdate(false);
        }


    }



    private void parseResponse(String result) {
        try {
            Logger.logD("FacilitiesListAsyncTask->", TAG + result);
            JSONObject jsonObject = new JSONObject(result);
            int status = jsonObject.getInt("status");
            if (status == 2) {
                Logger.logD("FacilitiesListAsyncTask->", TAG);
                methodToCallPagination(result);
            }
        }
        catch (Exception e) {
            Logger.logE(TAG,e.getMessage(), e);
        }
    }

    private void methodToCallPagination(String result) {
        if (!"".equals(result)) {
            Gson gson = new Gson();
            FacilitiesList facilitiesList = gson.fromJson(result, FacilitiesList.class);
            Logger.logD("FacilitiesListAsyncTask->", facilitiesList.getMessage());
            facilityDbhelper = new ExternalDbOpenHelper(this.context, facilityPreferences.getString(Constants.DBNAME, ""), facilityPreferences.getString("uId", ""));
            Logger.logD("Passing->", "Passing the Response bean to Interface");
            facilityDbhelper.updateFacilities(facilitiesList);
            if(!facilitiesList.getData().isEmpty()){
                String modifiedDate = facilityDbhelper.getLastUpDate("Facility", "modified", facilityDbhelper);
                List<NameValuePair> paramsL = new ArrayList<>();
                paramsL.add(new BasicNameValuePair("modified_date", modifiedDate));
                paramsL.add(new BasicNameValuePair(PARTNER_ID,String.valueOf(facilityPreferences.getInt(PARTNER_ID,0))));
                String secondResult =resturl.restUrlServerCall("facilities/facilitydatewiselisting/", "post", paramsL);
                Logger.logV("the parameters are", " facility the params" + paramsL);
                parseResponse(secondResult);
            }
        }
    }
}

