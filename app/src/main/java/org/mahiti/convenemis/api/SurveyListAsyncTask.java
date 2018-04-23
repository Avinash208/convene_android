package org.mahiti.convenemis.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.parentChild.SurveyListDetails;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.network.ClusterToTypo;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.RestUrl;

import java.util.ArrayList;
import java.util.List;

public class SurveyListAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences preferences;
    private RestUrl resturl;
    CheckNetwork chNetwork;
    Activity activity;
    ExternalDbOpenHelper surveyListDbhelper;
    String result="";
    android.app.ProgressDialog loginDialog;
    private String userId;
    private ClusterToTypo typo;
    SharedPreferences.Editor editor;
    /*
     * Calling categories task constructor
     */
    public SurveyListAsyncTask(Context context, Activity activity, ClusterToTypo typoObj, String uId) {
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        surveyListDbhelper =new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME,""),preferences.getString("uId",""));
        this.userId=uId;
        this.typo=typoObj;
    }

    protected void onPreExecute()
    {
        super.onPreExecute();
        loginDialog= ProgressUtils.showProgress(activity, false, "Loading Data collection forms" + "...");
        loginDialog.show();
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            List<NameValuePair> paramsL=new ArrayList<>();
            paramsL.add(new BasicNameValuePair("uId", userId));
            result=resturl.restUrlServerCall(activity, "survey-list/", "post", paramsL,"");
            Logger.logV("the parameters are", "the params"+paramsL);
            parseResponse(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        try {
            if((categoryList).contains(Constants.HTMLSTRING)){
                ProgressUtils.CancelProgress(loginDialog);
                typo.surveyListSuccess(false);
            }else{
                ProgressUtils.CancelProgress(loginDialog);
                typo.surveyListSuccess(true);
            }

        }catch (Exception e){
            Logger.logE("","",e);
            ProgressUtils.CancelProgress(loginDialog);
            typo.surveyListSuccess(false);
        }


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    public void parseResponse(String result)
    {
        try {
            final JSONObject json = new JSONObject(result);
            int status=json.getInt("status");
            if (status == 2) {
                Gson gson = new Gson();
                SurveyListDetails level1List = gson.fromJson(result, SurveyListDetails.class);
                surveyListDbhelper.updateSurveyList(level1List);
                String applicationLevels=json.getString("application_levels");
                editor=preferences.edit();
                editor.putString("app_Levels",applicationLevels);
                editor.apply();
            }
        } catch (Exception e) {
            Logger.logE("Login", "insertUserDetailsInPref error", e);
        }
    }
}