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
import org.fwwb.convene.convenecode.beansClassSetQuestion.LanguageLabelsBeen;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.utils.CheckNetwork;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.ProgressUtils;
import org.fwwb.convene.convenecode.utils.RestUrl;
import org.fwwb.convene.convenecode.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 25/1/17.
 */


public class LanguageLabelsAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences preferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ConveneDatabaseHelper dbhelper;
    String result = "";
    android.app.ProgressDialog loginDialog;
    private CallApis langLabelApi;
    private String globalModifiedDate ="";
    private static final String TABLE_NAME="LanguageLabels";
    private static final String UPDATE_TIME_KEY="updated_time";

    /**
     * LanguageLabelsAsyncTask constructor
     * @param context param
     * @param activity param
     * @param langApi param
     */
    /*
     * Calling categories task constructor
     */
    public LanguageLabelsAsyncTask(Context context, Activity activity,CallApis langApi) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        langLabelApi=langApi;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB,""),preferences.getString("UID",""));
    }

    /**
     * onPreExecute method
     *
     */
    protected void onPreExecute() {
        super.onPreExecute();
        loginDialog = ProgressUtils.showProgress(activity, false, "Loading LanguageLabels data" + "...");
        loginDialog.show();
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            globalModifiedDate =dbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            List<NameValuePair> paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID","")));
            paramsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            paramsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
            result = resturl.restUrlServerCall(activity,"language-labels-list" + "/", "post", paramsL, "");
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
                ProgressUtils.CancelProgress(loginDialog);
                langLabelApi.languageLabelsApi(10,false);
            }else {
                ProgressUtils.CancelProgress(loginDialog);
                langLabelApi.languageLabelsApi(10,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            ProgressUtils.CancelProgress(loginDialog);
            langLabelApi.languageLabelsApi(10,false);
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
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
                methodToFillDB(jsonObject);
                methodToCallPagination(jsonObject);

            }
        } catch (Exception e) {
            Logger.logE("","",e);
        }
    }

    /**
     * methodToCallPagination method
     * @param jsonObject param
     */
    private void methodToCallPagination(JSONObject jsonObject) {
        //Modify by guru
        String tempModifyDate = dbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            if(globalModifiedDate.equalsIgnoreCase(tempModifyDate)) {
                try {
                    if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                        globalModifiedDate = tempModifyDate;
                        List<NameValuePair> paramsL = new ArrayList<>();
                        paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID", "")));
                        paramsL.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                        paramsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
                        result = resturl.restUrlServerCall(activity, "language-labels-list" + "/", "post", paramsL, "");
                        Logger.logV("the parameters are", "the params in the second time is" + paramsL);
                        parseResponse(result);
                    }
                } catch (JSONException e) {
                    Logger.logE("","",e);
                }
            }else{
                Logger.logD("LanguageLabelAsyncTask","Exception on calling lanuagelabel");
                resturl.writeToTextFile("Conflict on LanguageLabel date", tempModifyDate,"getLangaugeLabellist");
            }

    }

    /**
     * methodToFillDB method
     * @param jsonObject param
     */
    private void methodToFillDB(JSONObject jsonObject) {
        try {
            JSONArray langArray = jsonObject.getJSONArray(TABLE_NAME);
            if (langArray.length() > 0) {
                Gson gson = new Gson();
                LanguageLabelsBeen level1List = gson.fromJson(result, LanguageLabelsBeen.class);
                dbhelper.updatelanguageLabels(level1List);
            }
            else
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("LanguageLabelsDbUpdated", false);
                editor.apply();
                ToastUtils.displayToastUi("Updated successfully", context);

            }
        } catch (Exception e) {
            Logger.logE("","",e);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("LanguageLabelsDbUpdated", false);
            editor.apply();
        }
    }
}
