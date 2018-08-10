package org.assistindia.convene.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.assistindia.convene.beansClassSetQuestion.CallApis;
import org.assistindia.convene.beansClassSetQuestion.QuestionBeen;
import org.assistindia.convene.database.ConveneDatabaseHelper;
import org.assistindia.convene.utils.CheckNetwork;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.RestUrl;
import org.assistindia.convene.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class QuestionsAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences questionPreferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ConveneDatabaseHelper questionDbhelper;
    String result = "";
    android.app.ProgressDialog loginDialog;
    private CallApis questionApi;
    private String mainModifiedDate="";
    private static final String TABLE_NAME="Question";
    private static final String UPDATE_TIME_KEY="updated_time";

    /**
     * QuestionsAsyncTask method
     * @param context param
     * @param activity param
     * @param queApi param
     *
     */
    /*
     * Calling categories task constructor
     */
    public QuestionsAsyncTask(Context context, Activity activity,CallApis queApi) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        questionApi=queApi;
        questionPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        questionDbhelper = ConveneDatabaseHelper.getInstance(context, questionPreferences.getString(Constants.CONVENE_DB,""), questionPreferences.getString("UID",""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            mainModifiedDate= questionDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            List<NameValuePair> questionParamsL = new ArrayList<>();
            questionParamsL.add(new BasicNameValuePair("uId", questionPreferences.getString("UID","")));
            questionParamsL.add(new BasicNameValuePair("updatedtime", mainModifiedDate));
            questionParamsL.add(new BasicNameValuePair("count", String.valueOf(questionDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
            result = resturl.restUrlServerCall(activity,"question-list" + "/", "post", questionParamsL, "");
            Logger.logV("", "the params" + questionParamsL);
            parseResponse(result);

        } catch (Exception e) {
           Logger.logE("","",e);
        }

        return result;
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
                questionApi.questionApi(3,false);
            }else {
                questionApi.questionApi(3,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            questionApi.questionApi(3,false);
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
                methodToParseNextresponse(jsonObject,result);
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
        String tempModifyDate = questionDbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
        if(!mainModifiedDate.equalsIgnoreCase(tempModifyDate)) {
            try {
                if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                    mainModifiedDate = tempModifyDate;
                    List<NameValuePair> paramsL = new ArrayList<>();
                    paramsL.add(new BasicNameValuePair("uId", questionPreferences.getString("UID", "")));
                    paramsL.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                    paramsL.add(new BasicNameValuePair("count", String.valueOf(questionDbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
                    result = resturl.restUrlServerCall(activity, "question-list" + "/", "post", paramsL, "");
                    Logger.logV("the parameters are", "the params in the second time is" + paramsL);
                    parseResponse(result);
                }
            } catch (Exception e) {
                Logger.logE("","",e);
            }
        }else{
            Logger.logV("QuestionAsyncTask","sendError on Question Api");
            resturl.writeToTextFile("Conflict on Question date", tempModifyDate,"getQuestionlist");
        }
    }

    /**
     * methodToParseNextresponse method
     * @param jsonObject param
     * @param result result
     */
    private void methodToParseNextresponse(JSONObject jsonObject, String result) {
        try {
            if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                JSONArray langArray = jsonObject.getJSONArray(TABLE_NAME);
                if (langArray.length() > 0) {
                    Gson gson = new Gson();
                    QuestionBeen level1List = gson.fromJson(result, QuestionBeen.class);
                    questionDbhelper.updateQuestions(level1List);
                }
                else
                {
                    SharedPreferences.Editor editor = questionPreferences.edit();
                    editor.putBoolean("QuestionDbUpdated", false);
                    editor.apply();
                    ToastUtils.displayToastUi("Updated successfully", context);

                }
            }

        } catch (Exception e) {
            Logger.logE("","",e);
            SharedPreferences.Editor editor = questionPreferences.edit();
            editor.putBoolean("QuestionDbUpdated", false);
            editor.apply();
        }
    }
}
