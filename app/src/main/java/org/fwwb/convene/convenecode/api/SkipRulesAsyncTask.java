package org.fwwb.convene.convenecode.api;

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
import org.fwwb.convene.convenecode.beansClassSetQuestion.CallApis;
import org.fwwb.convene.convenecode.beansClassSetQuestion.SkipRulesBeen;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.utils.CheckNetwork;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.ProgressUtils;
import org.fwwb.convene.convenecode.utils.RestUrl;
import org.fwwb.convene.convenecode.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
public class SkipRulesAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences skipRulePreferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ConveneDatabaseHelper skipRuleDbhelper;
    String result = "";
    android.app.ProgressDialog skipRuleLoginDialog;
    private CallApis skipRulesApi;
    private String globalModifiedDate ="";
    private static final String TABLE_NAME="SkipRules";

    /**
     * SkipRulesAsyncTask constructor
     * @param context param
     * @param activity param
     * @param skipRules param
     *
     */
    /*
     * Calling categories task constructor
     */
    public SkipRulesAsyncTask(Context context, Activity activity,CallApis skipRules) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        skipRulesApi=skipRules;
        skipRulePreferences = PreferenceManager.getDefaultSharedPreferences(context);
        skipRuleDbhelper = ConveneDatabaseHelper.getInstance(context, skipRulePreferences.getString(Constants.CONVENE_DB,""), skipRulePreferences.getString("UID",""));
    }

    /**
     *onPreExecute method
     *
     */
    protected void onPreExecute() {
        super.onPreExecute();
        skipRuleLoginDialog = ProgressUtils.showProgress(activity, false, "Loading SkipRules data" + "...");
        skipRuleLoginDialog.show();
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            List<NameValuePair> paramsL = new ArrayList<>();
            globalModifiedDate = skipRuleDbhelper.getLastUpDate(TABLE_NAME, Constants.UPDATED_TIME);
            paramsL.add(new BasicNameValuePair("uId", skipRulePreferences.getString("UID","")));
            paramsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            paramsL.add(new BasicNameValuePair("count", String.valueOf(skipRuleDbhelper.getCursorCount(TABLE_NAME, Constants.UPDATED_TIME))));
            result = resturl.restUrlServerCall(activity,"skip-rules-list" + "/", "post", paramsL, "");
            Logger.logV("the parameters are", "the params" + paramsL);
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
                ProgressUtils.CancelProgress(skipRuleLoginDialog);
                skipRulesApi.skipRulesApi(8,false);
            }else {
                ProgressUtils.CancelProgress(skipRuleLoginDialog);
                skipRulesApi.skipRulesApi(8,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            ProgressUtils.CancelProgress(skipRuleLoginDialog);
            skipRulesApi.skipRulesApi(8,false);
        }



    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }


    /**
     * parseResponsemethod
     * @param result param
     */
    public void parseResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
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
        String tempModifyDate = skipRuleDbhelper.getLastUpDate(TABLE_NAME, Constants.UPDATED_TIME);
        if(globalModifiedDate.equalsIgnoreCase(tempModifyDate)) {
            try {
                if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                    globalModifiedDate = tempModifyDate;
                    List<NameValuePair> paramsL = new ArrayList<>();
                    paramsL.add(new BasicNameValuePair("uId", skipRulePreferences.getString("UID", "")));
                    paramsL.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                    paramsL.add(new BasicNameValuePair("count", String.valueOf(skipRuleDbhelper.getCursorCount(TABLE_NAME, Constants.UPDATED_TIME))));
                    result = resturl.restUrlServerCall(activity, "skip-rules-list" + "/", "post", paramsL, "");
                    Logger.logV("the parameters are", "the params in the second time is" + paramsL);
                    parseResponse(result);
                }
            } catch (JSONException e) {
                Logger.logE("","",e);
            }
        }else{
            Logger.logD("SkipRulesAsyncTask","Exception on calling SkipRules");
            resturl.writeToTextFile("Conflict on Skiprules date", tempModifyDate,"getSkipRulelist");
        }
    }

    /**
     * methodToFillDb method
     * @param jsonObject param
     */
    private void methodToFillDb(JSONObject jsonObject) {
        try {
            JSONArray langArray = jsonObject.getJSONArray(TABLE_NAME);
            if (langArray.length() > 0) {
                Gson gson = new Gson();
                SkipRulesBeen level1List = gson.fromJson(result, SkipRulesBeen.class);
                skipRuleDbhelper.updateSkipRules(level1List);
            }
            else
            {
                SharedPreferences.Editor editor = skipRulePreferences.edit();
                editor.putBoolean("SkipRulesDbUpdated", false);
                editor.apply();
                ToastUtils.displayToastUi("Updated successfully", context);
            }
        } catch (Exception e) {
            Logger.logE("","",e);
            SharedPreferences.Editor editor = skipRulePreferences.edit();
            editor.putBoolean("SkipRulesDbUpdated", false);
            editor.apply();
        }
    }
}
