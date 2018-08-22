package org.yale.convene.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yale.convene.beansClassSetQuestion.CallApis;
import org.yale.convene.beansClassSetQuestion.SkipMandatoryBeen;
import org.yale.convene.database.ConveneDatabaseHelper;
import org.yale.convene.utils.CheckNetwork;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.RestUrl;
import org.yale.convene.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;




public class SkipMandatoryAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences skipMandatoryPreferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ConveneDatabaseHelper skipMandatoryDbhelper;
    String result = "";
    android.app.ProgressDialog loginDialog;
    private CallApis skipMandatoryApil;
    private String globalModifiedDate ="";
    private static final String TABLE_NAME="SkipMandatory";
    private static final String UPDATE_TIME_KEY="updated_time";

    /**
     * SkipMandatoryAsyncTask constructor
     * @param context param
     * @param activity param
     * @param skipManApi param
     *
     */
    /*
     * Calling categories task constructor
     */
    public SkipMandatoryAsyncTask(Context context, Activity activity,CallApis skipManApi) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        skipMandatoryApil=skipManApi;
        skipMandatoryPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        skipMandatoryDbhelper = ConveneDatabaseHelper.getInstance(context, skipMandatoryPreferences.getString(Constants.CONVENE_DB,""), skipMandatoryPreferences.getString("UID",""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            List<NameValuePair> skipMandatoryParamsL = new ArrayList<>();
            globalModifiedDate = skipMandatoryDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            skipMandatoryParamsL.add(new BasicNameValuePair("uId", skipMandatoryPreferences.getString("UID","")));
            skipMandatoryParamsL.add(new BasicNameValuePair("updatedtime",globalModifiedDate));
            skipMandatoryParamsL.add(new BasicNameValuePair("count", String.valueOf(skipMandatoryDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
            result = resturl.restUrlServerCall(activity,"skip-mandatory" + "/", "post", skipMandatoryParamsL, "");
            Logger.logV("the parameters are", "the params" + skipMandatoryParamsL);
            parseResponse(result);

        } catch (Exception e) {
           Logger.logE("","",e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            if((categoryList).contains(Constants.HTMLSTRING)){
                skipMandatoryApil.skipMandatoryApi(7,false);
            }else {
                skipMandatoryApil.skipMandatoryApi(7,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            skipMandatoryApil.skipMandatoryApi(7,false);
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
    public void parseResponse(String skipMandatoryResult) {
        try {
            JSONObject jsonObject = new JSONObject(skipMandatoryResult);
            int status = jsonObject.getInt("status");
            if (status == 2) {
                methodToFillDb(jsonObject);
                    methodToCallPagination(jsonObject);
            }
        } catch (Exception e) {
            Logger.logE("","",e);
        }
    }

    /**
     * methodToCallPagination method
     * @param jsonObject param
     *
     */
    private void methodToCallPagination(JSONObject jsonObject) {
        //Modify by Guru
        String tempModifyDate = skipMandatoryDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
        if(!globalModifiedDate.equalsIgnoreCase(tempModifyDate)) {
            try {
                if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                    globalModifiedDate = tempModifyDate;
                    List<NameValuePair> skipMandatoryParamsvalues = new ArrayList<>();
                    skipMandatoryParamsvalues.add(new BasicNameValuePair("uId", skipMandatoryPreferences.getString("UID", "")));
                    skipMandatoryParamsvalues.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                    skipMandatoryParamsvalues.add(new BasicNameValuePair("count", String.valueOf(skipMandatoryDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
                    result = resturl.restUrlServerCall(activity, "skip-mandatory" + "/", "post", skipMandatoryParamsvalues, "");
                    Logger.logV("the parameters are", "the params in the second time is" + skipMandatoryParamsvalues);
                    parseResponse(result);
                }
            } catch (JSONException e) {
                Logger.logE("","",e);
            }
        }else{
            Logger.logD("SkipMandatoryAsyncTask","Exception on calling SkipMandatory Api");
            resturl.writeToTextFile("Conflict on SkipMandatory date", tempModifyDate,"getSkipMandatorylist");
        }
    }

    /**
     * methodToFillDb method
     * @param jsonObject param
     *
     */
    private void methodToFillDb(JSONObject jsonObject) {
        try {
            JSONArray langArray = jsonObject.getJSONArray(TABLE_NAME);
            if (langArray.length() > 0) {
                Gson gson = new Gson();
                SkipMandatoryBeen level1List = gson.fromJson(result, SkipMandatoryBeen.class);
                skipMandatoryDbhelper.updateSkipMandatory(level1List);
            }
            else
            {
                SharedPreferences.Editor skipManeditor = skipMandatoryPreferences.edit();
                skipManeditor.putBoolean("SkipMandatoryDbUpdated", false);
                skipManeditor.apply();
                ToastUtils.displayToastUi("Updated successfully", context);

            }
        } catch (Exception e) {
            Logger.logE("","",e);
            SharedPreferences.Editor skipMandatoryEditor = skipMandatoryPreferences.edit();
            skipMandatoryEditor.putBoolean("SkipMandatoryDbUpdated", false);
            skipMandatoryEditor.apply();
        }
    }
}
