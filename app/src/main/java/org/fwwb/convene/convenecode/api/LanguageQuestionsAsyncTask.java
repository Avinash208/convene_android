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
import org.fwwb.convene.convenecode.BeenClass.regionallanguage.GetLanguageQuestions;
import org.fwwb.convene.convenecode.beansClassSetQuestion.CallApis;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.utils.CheckNetwork;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.RestUrl;
import org.fwwb.convene.convenecode.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;




public class LanguageQuestionsAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences preferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    private ConveneDatabaseHelper dbhelper;
    String result = "";
    android.app.ProgressDialog loginDialog;
    private CallApis langQuestionApi;
    private String globalModifiedDate ="";
    private static final String UPDATE_TIME_KEY="updated_time";
    private String languageQuestionStr = "LanguageQuestion";

    /**
     * LanguageQuestionsAsyncTask constructor
     * @param context param
     * @param activity param
     * @param langApi param
     *
     */
    /*
     * Calling categories task constructor
     */
    public LanguageQuestionsAsyncTask(Context context, Activity activity,CallApis langApi) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        langQuestionApi=langApi;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB,""),preferences.getString("UID",""));
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            globalModifiedDate =dbhelper.getLastUpDate(languageQuestionStr, UPDATE_TIME_KEY);
            List<NameValuePair> paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID","")));
            paramsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            paramsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(languageQuestionStr, UPDATE_TIME_KEY))));
            result = resturl.restUrlServerCall(activity,"language-question-list" + "/", "post", paramsL, "");

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
                langQuestionApi.languageQuestionApi(4,false);
            }else {
                langQuestionApi.languageQuestionApi(4,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            langQuestionApi.languageQuestionApi(4,false);
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * parseResponsemethod
     * @param result result
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
     * methodToCallPagination method
     * @param jsonObject param
     *
     */
    private void methodToCallPagination(JSONObject jsonObject) {
        //Modify by Guru
        String tempModifyDate = dbhelper.getLastUpDate(languageQuestionStr, UPDATE_TIME_KEY);
            if (!globalModifiedDate.equalsIgnoreCase(tempModifyDate)){
                Logger.logD("SENDERROR_LOG", "NO languageQuestion table date conflict");
                try {
                    if (!"Data already sent".equalsIgnoreCase(jsonObject.getString("message"))){
                        globalModifiedDate = tempModifyDate;
                        List<NameValuePair> paramsL = new ArrayList<>();
                        paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID","")));
                        paramsL.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                        paramsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(Constants.ASSESSMENT, UPDATE_TIME_KEY))));
                        result = resturl.restUrlServerCall(activity, "language-question-list" + "/", "post", paramsL, "");
                        Logger.logV("the parameters are", "the params" + paramsL);
                        parseResponse(result);
                    }
                } catch (JSONException e) {
                    Logger.logE("","",e);
                }

            }else{
                Logger.logD("SENDERROR_LOG", "sendERRORLOG of Assessment table ");
                resturl.writeToTextFile("Conflict in the Assessment date",tempModifyDate , Constants.ASSESSMENT);
            }

    }

    /**
     * methodToFillDBmethod
     * @param jsonObject pparam
     *
     */
    private void methodToFillDB(JSONObject jsonObject) {
        try {
            JSONArray langArray = jsonObject.getJSONArray(languageQuestionStr);
            if (langArray.length() > 0) {
                Gson gson = new Gson();
                GetLanguageQuestions level1List = gson.fromJson(result, GetLanguageQuestions.class);
                dbhelper.updatelanguageQuestion(level1List);
            }
            else
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("LanguageQuestionDbUpdated", false);
                editor.apply();
                ToastUtils.displayToastUi("Updated successfully", context);

            }
        } catch (Exception e) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("LanguageQuestionDbUpdated", false);
            editor.apply();
        }
    }
}
