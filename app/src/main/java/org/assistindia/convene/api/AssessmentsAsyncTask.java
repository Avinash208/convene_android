package org.assistindia.convene.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.assistindia.convene.beansClassSetQuestion.CallApis;
import org.assistindia.convene.database.ConveneDatabaseHelper;
import org.assistindia.convene.utils.CheckNetwork;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.RestUrl;
import org.assistindia.convene.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class AssessmentsAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences assessmentPreferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ConveneDatabaseHelper dbhelper;
    String result = "";
    private CallApis assessmentApis;
    private String globalModifiedDate ="";
    private static final String TABLE_NAME=Constants.ASSESSMENT;
    private static final String UPDATE_TIME_KEY="updated_time";

    /**
     * AssessmentsAsyncTask constructor
     * @param context param
     * @param activity param
     * @param assessment param
     *
     */
    /*
     * Calling categories task constructor
     */
    public AssessmentsAsyncTask(Context context, Activity activity,CallApis assessment) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        assessmentApis=assessment;
        assessmentPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper = ConveneDatabaseHelper.getInstance(context, assessmentPreferences.getString(Constants.CONVENE_DB,""), assessmentPreferences.getString("UID",""));
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            List<NameValuePair> assessmentParamsL = new ArrayList<>();
            globalModifiedDate =dbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            assessmentParamsL.add(new BasicNameValuePair("uId", assessmentPreferences.getString("UID","")));
            assessmentParamsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            assessmentParamsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
            result = resturl.restUrlServerCall(activity, "assessment-list" + "/", "post", assessmentParamsL, "");
            Logger.logV("the parameters are", "the params" + assessmentParamsL);
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
                assessmentApis.assessmentApi(5,false);
            }else {
                assessmentApis.assessmentApi(5,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            assessmentApis.assessmentApi(5,false);
        }

    }


    /**
     * parseResponse method
     * @param result result
     */
    public void parseResponse(String result) {
        try {
            JSONObject object = new JSONObject(result);
            int status = object.getInt("status");
            if (status == 2) {
                methodToFillDb(object);
                methodToCallPagination(object);
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
        //Modify by Guru
        String tempModifyDate = dbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
        if(!globalModifiedDate.equalsIgnoreCase(tempModifyDate)) {
            try {
                if (!("Data already sent").equalsIgnoreCase(jsonObject.getString("message"))){
                    globalModifiedDate = tempModifyDate;
                    List<NameValuePair> assessmentNameValueParamsL = new ArrayList<>();
                    assessmentNameValueParamsL.add(new BasicNameValuePair("uId", assessmentPreferences.getString("UID", "")));
                    assessmentNameValueParamsL.add(new BasicNameValuePair("updatedtime", tempModifyDate));
                    assessmentNameValueParamsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
                    result = resturl.restUrlServerCall(activity, "assessment-list" + "/", "post", assessmentNameValueParamsL, "");
                    Logger.logV("the parameters are", "the params" + assessmentNameValueParamsL);
                    parseResponse(result);
                }
            } catch (JSONException e) {
                Logger.logE("","",e);
            }
        }else{
            Logger.logD("AssessmentAsyncTask","Exception on Calling Assessment Api");
            resturl.writeToTextFile("Conflict on Assessment date",tempModifyDate,"getAssessmentlist");
        }
    }

    /**
     * methodToFillDb method
     * @param jsonObject param
     */
    private void methodToFillDb(JSONObject jsonObject) {
        try {
            JSONArray assessmentArray = jsonObject.getJSONArray(Constants.ASSESSMENT);
            if (assessmentArray.length() > 0) {
                dbhelper.updateAssessmentArray(assessmentArray);
            }
            else
            {
                SharedPreferences.Editor edit = assessmentPreferences.edit();
                edit.putBoolean("AssessmentDbUpdated", false);
                edit.apply();
                ToastUtils.displayToastUi("Updated successfully", context);

            }
        } catch (Exception e) {
            Logger.logE("","",e);
            SharedPreferences.Editor editor = assessmentPreferences.edit();
            editor.putBoolean("AssessmentDbUpdated", false);
            editor.apply();
        }
    }
}
