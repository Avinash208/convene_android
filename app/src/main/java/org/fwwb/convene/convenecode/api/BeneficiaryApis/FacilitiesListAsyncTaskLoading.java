package org.fwwb.convene.convenecode.api.BeneficiaryApis;

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
import org.fwwb.convene.convenecode.utils.RestUrl;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class FacilitiesListAsyncTaskLoading extends AsyncTask<Context, Integer, String> {

    private DirectUrlCall resturl;
    private Context context;
    Activity activity;
    String result = "";
    private RestUrl facilityRestUrl;
    private FacilityListInterface facilityListInterface;
    private ExternalDbOpenHelper dbhelper;
    SharedPreferences preferences;
    private static final String TABLE_NAME="Facility";
    private  String tag ="FacilitiesListAsyncTaskLoading";
    private String globalModifiedDate ="";
    private static final String MODIFIED ="modified";
    private static final String PARTNER_ID ="partner_id";

    /**
     * {@link FacilitiesListAsyncTaskLoading constructor }
     * @param context param
     * @param activity param
     * @param houseHoldActivity param
     *
     */
    public FacilitiesListAsyncTaskLoading(Context context, Activity activity, FacilityListInterface houseHoldActivity) {
        this.context = context;
        resturl = new DirectUrlCall(context);
        this.activity = activity;
        facilityRestUrl=new RestUrl(context);
        facilityListInterface = houseHoldActivity;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper =  ExternalDbOpenHelper.getInstance(context, preferences.getString(Constants.DBNAME,""), preferences.getString("UID",""));

    }

    @Override
    protected String doInBackground(Context... params) {
        if (!Utils.haveNetworkConnection(context)) {
            return "";
        }
        try {
            dbhelper = new ExternalDbOpenHelper(this.context, preferences.getString(Constants.DBNAME, ""), this.preferences.getString("uId", ""));
            globalModifiedDate =dbhelper.getLastUpDate(TABLE_NAME, MODIFIED, dbhelper);
            List<NameValuePair> paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("modified_date", globalModifiedDate));
            paramsL.add(new BasicNameValuePair(PARTNER_ID,String.valueOf(preferences.getInt(PARTNER_ID,0))));

            result = resturl.restUrlServerCall("facilities/facilitydatewiselisting/", "post", paramsL);
            Logger.logV(tag, "the params" + paramsL);
            parseResponse(result);
        } catch (Exception e) {
            Logger.logE(tag,"Exception on Facility do in background ", e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        if((categoryList).contains(Constants.HTMLSTRING)){
            facilityListInterface.onSuccessFacilityUpdate(false);
        }else{
            try{
                JSONObject jsonObject = new JSONObject(categoryList);
                int status = jsonObject.getInt("status");
                if (status == 2) {
                    facilityListInterface.onSuccessFacilityUpdate(true);
                }else{
                    ToastUtils.displayToast("Something went Wrong",context);
                }
            }catch (Exception e){
                Logger.logE(tag,"Exception on Facility on post execute ", e);
                facilityListInterface.onSuccessFacilityUpdate(false);
            }
        }

    }


    /**
     * parseResponse method
     * @param result param
     */
    private void parseResponse(String result) {
        try {
            Logger.logD(tag, "FacilitiesListAsyncTask" + result);
            JSONObject jsonObject = new JSONObject(result);
            int status = jsonObject.getInt("status");
            if (status == 2) {
                Logger.logD(tag, "FacilitiesListAsyncTask");
                methodToCallPagination(result,jsonObject);
            }
        }
        catch (Exception e) {
            Logger.logE(tag,"Exception on Facility ", e);
        }
    }

    /**
     * methodToCallPaginationmethod
     * @param result param
     * @param jsonObject param
     */
    private void methodToCallPagination(String result, JSONObject jsonObject) {
        if (!("").equals(result)) {
            Gson gson = new Gson();
            FacilitiesList facilitiesList = gson.fromJson(result, FacilitiesList.class);
            Logger.logD(tag, facilitiesList.getMessage());
            dbhelper = new ExternalDbOpenHelper(this.context, preferences.getString(Constants.DBNAME, ""), preferences.getString("uId", ""));
            Logger.logD(tag, "Passing the Response bean to Interface");
            String tempModifyDate = dbhelper.getLastUpDate(TABLE_NAME, MODIFIED, dbhelper);
            if(!facilitiesList.getData().isEmpty()){
                dbhelper.updateFacilities(facilitiesList);
                if(!globalModifiedDate.equalsIgnoreCase(tempModifyDate)) {
                    globalModifiedDate=tempModifyDate;
                    callApiToGetNextRecords(jsonObject,tempModifyDate);
                }else{
                    Logger.logD("FacilityAsyncTask","Exception on calling Facility listing");
                    facilityRestUrl.writeToTextFile("Conflict on Facility date",dbhelper.getLastUpDate(TABLE_NAME, MODIFIED, dbhelper),"getFacility");
                }
            }

        }
    }


    /**
     *callApiToGetNextRecords method
     * @param jsonObject
     * @param tempModifyDate
     *
     */
    private void callApiToGetNextRecords(JSONObject jsonObject, String tempModifyDate) {
        try {
            if(!("Successfully Retrieved").equalsIgnoreCase(jsonObject.getString("message")))
            {
                List<NameValuePair> paramsL = new ArrayList<>();
                paramsL.add(new BasicNameValuePair("modified_date", tempModifyDate));
                paramsL.add(new BasicNameValuePair(PARTNER_ID,String.valueOf(preferences.getInt(PARTNER_ID,0))));
                String secondResult = resturl.restUrlServerCall("facilities/facilitydatewiselisting/", "post", paramsL);
                Logger.logV(tag, " facility the params" + paramsL);
                parseResponse(secondResult);
            }
        } catch (Exception e) {
            Logger.logE("","",e);
        }
    }
}

