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
import org.yale.convene.BeenClass.regionallanguage.GetLanguageBlock;
import org.yale.convene.beansClassSetQuestion.CallApis;
import org.yale.convene.database.ConveneDatabaseHelper;
import org.yale.convene.utils.CheckNetwork;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.RestUrl;
import org.yale.convene.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;



public class LanguageBlockAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences languageBlockPreferences;
    private RestUrl resturl;
    CheckNetwork chNetwork;
    Activity activity;
    private ConveneDatabaseHelper languageBlockDbhelper;
    String languageBlockResult = "";
    android.app.ProgressDialog loginDialog;
    private CallApis langBlockApi;
    private static final String TABLE_NAME="LanguageBlock";
    private static final String UPDATE_TIME_KEY="updated_time";
    private String globalModifiedDate ="";
    Context context1;

    /**
     * LanguageBlockAsyncTask constructor
     * @param context param
     * @param activity param
     * @param blockApi param
     */
    /*
     * Calling categories task constructor
     */
    public LanguageBlockAsyncTask(Context context, Activity activity,CallApis blockApi) {
         context1= context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        langBlockApi=blockApi;
        languageBlockPreferences = PreferenceManager.getDefaultSharedPreferences(context1);
        languageBlockDbhelper = ConveneDatabaseHelper.getInstance(context1, languageBlockPreferences.getString(Constants.CONVENE_DB,""), languageBlockPreferences.getString("UID",""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            List<NameValuePair> languageBlockParamsL = new ArrayList<>();
            globalModifiedDate = languageBlockDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            languageBlockParamsL.add(new BasicNameValuePair("uId", languageBlockPreferences.getString("UID","")));
            languageBlockParamsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            languageBlockParamsL.add(new BasicNameValuePair("count", String.valueOf(languageBlockDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
            languageBlockResult = resturl.restUrlServerCall(activity,"language-block-list" + "/", "post", languageBlockParamsL, "");
            Logger.logV("the language block parameters are", "the params" + languageBlockParamsL);
            parseResponse(languageBlockResult);

        } catch (Exception e) {
            Logger.logE("","",e);
        }

        return languageBlockResult;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            if((categoryList).contains(Constants.HTMLSTRING)){
                langBlockApi.languageBlock(2,false);
            }else {
                langBlockApi.languageBlock(2,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            langBlockApi.languageBlock(2,false);
        }




    }

    /**
     * parseResponse method
     * @param result param
     *
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
     * methodToFillDB method
     * @param jsonObject param
     */
    private void methodToFillDB(JSONObject jsonObject) {
        try {
            JSONArray langArray = jsonObject.getJSONArray(TABLE_NAME);
            if (langArray.length() > 0) {
                Gson gson = new Gson();
                GetLanguageBlock level1List = gson.fromJson(languageBlockResult, GetLanguageBlock.class);
                languageBlockDbhelper.updatelanguageBlock(level1List);
            }
            else
            {
                SharedPreferences.Editor editor = languageBlockPreferences.edit();
                editor.putBoolean("LanguageBlockDbUpdated", false);
                editor.apply();
                ToastUtils.displayToastUi("Updated successfully", context1);

            }
        } catch (Exception e) {
            SharedPreferences.Editor editor = languageBlockPreferences.edit();
            editor.putBoolean("LanguageBlockDbUpdated", false);
            editor.apply();
        }
    }

    /**
     * methodToCallPagination method
     * @param jsonObject param
     */
    private void methodToCallPagination(JSONObject jsonObject) {
        //Modify by Guru
        String tempModifyDate = languageBlockDbhelper.getLastUpDate(TABLE_NAME, Constants.UPDATED_TIME);
        if(!globalModifiedDate.equalsIgnoreCase(tempModifyDate)) {
            try {
                if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                        globalModifiedDate = tempModifyDate;
                        List<NameValuePair> paramsL = new ArrayList<>();
                        paramsL.add(new BasicNameValuePair("uId", languageBlockPreferences.getString("UID", "")));
                        paramsL.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                        paramsL.add(new BasicNameValuePair("count", String.valueOf(languageBlockDbhelper.getCursorCount(TABLE_NAME, Constants.UPDATED_TIME))));
                        languageBlockResult = resturl.restUrlServerCall(activity, "block-list" + "/", "post", paramsL, "");
                        Logger.logV("the parameters are", "the params in the second time is" + paramsL);
                        parseResponse(languageBlockResult);

                }
            } catch (JSONException e) {
                Logger.logE("","",e);
            }
        }else{
            Logger.logD("BlockAsyncTask","Exception on calling Block Api");
            resturl.writeToTextFile("Conflict on Block date", tempModifyDate,"getBlocklist");
        }
    }
}
