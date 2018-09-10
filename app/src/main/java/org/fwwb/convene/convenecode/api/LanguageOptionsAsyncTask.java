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
import org.fwwb.convene.convenecode.BeenClass.regionallanguage.GetLanguageOptions;
import org.fwwb.convene.convenecode.beansClassSetQuestion.CallApis;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.utils.CheckNetwork;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.RestUrl;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 10/2/17.
 */

public class LanguageOptionsAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences langaugeOptionPreferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ConveneDatabaseHelper languageOptionDbhelper;
    String result = "";
    android.app.ProgressDialog loginDialog;
    private CallApis optApi;
    private String globalModifiedDate ="";
    private static final String UPDATE_TIME_KEY="updated_time";
    private static final String TABLE_NAME="LanguageOptions";

    /**
     * LanguageOptionsAsyncTask constructor
     * @param context param
     * @param activity param
     * @param optionsApi param
     */
    /*
     * Calling categories task constructor
     */
    public LanguageOptionsAsyncTask(Context context, Activity activity,CallApis optionsApi) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        optApi=optionsApi;
        langaugeOptionPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        languageOptionDbhelper = ConveneDatabaseHelper.getInstance(context, langaugeOptionPreferences.getString(Constants.CONVENE_DB,""), langaugeOptionPreferences.getString("UID",""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            globalModifiedDate = languageOptionDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            List<NameValuePair> paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("uId", langaugeOptionPreferences.getString("UID","")));
            paramsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            paramsL.add(new BasicNameValuePair("count", String.valueOf(languageOptionDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
            result = resturl.restUrlServerCall(activity,"language-choice" + "/", "post", paramsL, "");
            Logger.logV("the parameters are", "the params" + paramsL);
            Logger.logV("language options", "the json response of language options" + result);
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
                optApi.optionsApi(11,false);
            }else {
                optApi.optionsApi(11,true);
                Utils.copyEncryptedConveneDataBase(context, langaugeOptionPreferences);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            optApi.optionsApi(11,false);
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
                methodToFillDb(jsonObject);
                methodToCallPagination(jsonObject);

            }
        } catch (Exception e) {
            Logger.logE("Exception", "Exception in LangaugeOptions " , e);

        }
    }

    /**
     * methodToCallPagination method
     * @param jsonObject param
     */
    private void methodToCallPagination(JSONObject jsonObject) {
        //Modify by guru
        String tempModifyDate = languageOptionDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
        if (!globalModifiedDate.equalsIgnoreCase(tempModifyDate)){

            try {
                if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                    globalModifiedDate = tempModifyDate;
                    Logger.logD("SENDERROR_LOG", "NO languageQuestion table date conflict");
                    List<NameValuePair> paramsL = new ArrayList<>();
                    paramsL.add(new BasicNameValuePair("uId", langaugeOptionPreferences.getString("UID","")));
                    paramsL.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                    paramsL.add(new BasicNameValuePair("count", String.valueOf(languageOptionDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
                    result = resturl.restUrlServerCall(activity,"language-choice" + "/", "post", paramsL, "");
                    Logger.logV("the parameters are", "the params" + paramsL);
                    parseResponse(result);
                }
            } catch (JSONException e) {
                Logger.logE("","",e);
            }
        }else{
            Logger.logD("SENDERROR_LOG", "sendERRORLOG of Assessment table ");
            resturl.writeToTextFile("Conflict in the Langaugeoptions date", tempModifyDate , "getLanguageOptionlist");
        }

    }

    /**
     * methodToFillDb method
     * @param jsonObject param
     */
    private void methodToFillDb(JSONObject jsonObject) {
        try {
            JSONArray langArray = jsonObject.getJSONArray(TABLE_NAME);
            Logger.logV("Language Options","Json Array " + langArray.toString());
            if (langArray.length() > 0) {
                Gson gson = new Gson();
                GetLanguageOptions level1List = gson.fromJson(result, GetLanguageOptions.class);
                languageOptionDbhelper.updateLanguageOptions(level1List);
            }
            else {
                SharedPreferences.Editor editor = langaugeOptionPreferences.edit();
                editor.putBoolean("OptionsDbUpdated", false);
                editor.apply();
                ToastUtils.displayToastUi("Updated successfully", context);

            }
        } catch (Exception e) {
            SharedPreferences.Editor editor = langaugeOptionPreferences.edit();
            editor.putBoolean("OptionsDbUpdated", false);
            editor.apply();
        }
    }

}
