package org.mahiti.convenemis.api;

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
import org.mahiti.convenemis.BeenClass.regionallanguage.GetLanguageAssessment;
import org.mahiti.convenemis.beansClassSetQuestion.CallApis;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.RestUrl;
import org.mahiti.convenemis.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;



public class LanguageAssessmentAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
      * Declaring all the variables and views
      */
    private SharedPreferences langAssessmentPreferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    private Activity activity;
    private ConveneDatabaseHelper langaugeAssessmentDbhelper;
    String result = "";
    android.app.ProgressDialog loginDialog;
    private CallApis langAssessment;
    private String globalModifiedDate ="";
    private static final String TABLE_NAME="LanguageAssessment";
    private static final String UPDATE_TIME_KEY="updated_time";
    private String lannguageAssessmentDbUpdatedStr = "LannguageAssessmentDbUpdated";

    /**
     * LanguageAssessmentAsyncTask method
     * @param context param
     * @param activity param
     * @param assessmentLang param
     *
     */
    /*
     * Calling categories task constructor
     */
    public LanguageAssessmentAsyncTask(Context context, Activity activity,CallApis assessmentLang) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        langAssessment=assessmentLang;
        langAssessmentPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        langaugeAssessmentDbhelper = ConveneDatabaseHelper.getInstance(context, langAssessmentPreferences.getString(Constants.CONVENE_DB,""), langAssessmentPreferences.getString("UID",""));
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            globalModifiedDate = langaugeAssessmentDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            List<NameValuePair> langAssessmentParamsL = new ArrayList<>();
            langAssessmentParamsL.add(new BasicNameValuePair("uId", langAssessmentPreferences.getString("UID","")));
            langAssessmentParamsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            langAssessmentParamsL.add(new BasicNameValuePair("count", String.valueOf(langaugeAssessmentDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
            result = resturl.restUrlServerCall(activity,"language-assessment-list" + "/", "post", langAssessmentParamsL, "");
            Logger.logV("", "the params is" + langAssessmentParamsL);
            parseResponse(result);
        } catch (Exception e) {
            Logger.logE("","",e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String langAssessList) {
        super.onPostExecute(langAssessList);
        try {
            if((langAssessList).contains(Constants.HTMLSTRING)){
                langAssessment.languageAssessmentApi(6,false);
            }else {
                langAssessment.languageAssessmentApi(6,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            langAssessment.languageAssessmentApi(6,false);
        }


    }


    /**
     * parseResponse method
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
            else{
                SharedPreferences.Editor langEditor = langAssessmentPreferences.edit();
                langEditor.putBoolean(lannguageAssessmentDbUpdatedStr, false);
                langEditor.apply();
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
        String tempModifyDate = langaugeAssessmentDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            if (!globalModifiedDate.equalsIgnoreCase(tempModifyDate)){
                Logger.logD("SENDERROR_LOG", "NO languageQuestion table date conflict");
                try {
                    if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                        globalModifiedDate = tempModifyDate;
                        List<NameValuePair> langAssessmentParams = new ArrayList<>();
                        langAssessmentParams.add(new BasicNameValuePair("uId", langAssessmentPreferences.getString("UID","")));
                        langAssessmentParams.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                        langAssessmentParams.add(new BasicNameValuePair("count", String.valueOf(langaugeAssessmentDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
                        result = resturl.restUrlServerCall(activity, "language-assessment-list" + "/", "post", langAssessmentParams, "");
                        Logger.logV("the parameters are", "the params" + langAssessmentParams);
                        parseResponse(result);
                    }
                } catch (JSONException e) {
                    Logger.logE("","",e);
                }
            }else{
                Logger.logD("SENDERROR_LOG", "sendERRORLOG of Assessment table ");
                resturl.writeToTextFile("Conflict in the Langauge Assessment date", tempModifyDate , "getLanguageAssessmentlist");
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
                GetLanguageAssessment level1List = gson.fromJson(result, GetLanguageAssessment.class);
                langaugeAssessmentDbhelper.updatelanguageAssessments(level1List);
            }
            else
            {
                SharedPreferences.Editor editor = langAssessmentPreferences.edit();
                editor.putBoolean(lannguageAssessmentDbUpdatedStr, false);
                editor.apply();
                ToastUtils.displayToastUi("Updated successfully", context);

            }
        } catch (Exception e) {
            Logger.logE("","",e);
            SharedPreferences.Editor edit = langAssessmentPreferences.edit();
            edit.putBoolean(lannguageAssessmentDbUpdatedStr, false);
            edit.apply();
        }

    }

}
