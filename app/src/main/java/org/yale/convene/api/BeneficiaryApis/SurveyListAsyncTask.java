package org.yale.convene.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.yale.convene.BeenClass.parentChild.SurveyListDetails;
import org.yale.convene.R;
import org.yale.convene.UpdateMasterLoading;
import org.yale.convene.database.ExternalDbOpenHelper;
import org.yale.convene.network.ClusterToTypo;
import org.yale.convene.utils.CheckNetwork;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.ProgressUtils;
import org.yale.convene.utils.RestUrl;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 30/1/17.
 */


public class SurveyListAsyncTask extends AsyncTask<Context, Integer, String> {
    /*
     * Declaring all the variables and views
     */
    private SharedPreferences preferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ExternalDbOpenHelper dbhelper;
    String result = "";
    android.app.ProgressDialog loginDialog;
    String userId;
    ClusterToTypo typo;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    TextView surveyStatus;

    /**
     * SurveyListAsyncTask constructor
     *
     * @param context        param
     * @param activity       param
     * @param typoObj        param
     * @param uId            param
     * @param progressBar    param
     * @param locationStatus param
     */
    /*
     * Calling categories task constructor
     */
    public SurveyListAsyncTask(UpdateMasterLoading context, UpdateMasterLoading activity, ClusterToTypo typoObj, String uId, ProgressBar progressBar, TextView locationStatus) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper = new ExternalDbOpenHelper(context, preferences.getString(Constants.DBNAME, ""), preferences.getString("uId", ""));
        this.userId = uId;
        this.typo = typoObj;
        this.progressBar = progressBar;
        this.surveyStatus = locationStatus;
        dbhelper.deleteCompleteSurveys();
    }


    /**
     * onPreExecute method
     */
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setProgress(0);
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            List<NameValuePair> paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("uId", userId));
            result = resturl.restUrlServerCall(activity, "survey-list/", "post", paramsL, "");
            publishProgress(10);
            Logger.logV("the parameters are", "the params" + paramsL);
            parseResponse(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        ProgressUtils.CancelProgress(loginDialog);
        typo.surveyListSuccess(true);

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Animation translatebu = AnimationUtils.loadAnimation(context, R.anim.animationfile);
        progressBar.setProgress(values[0]);
        surveyStatus.setTextColor(Color.parseColor("#d24645"));
        surveyStatus.setText(R.string.loading_survey);
        //   surveyStatus.startAnimation(translatebu);

    }

    /**
     * parseResponse method
     *
     * @param result param
     */
    public void parseResponse(String result) {
        try {
            final JSONObject json = new JSONObject(result);
            int status = json.getInt("status");
            if (status == 2) {
                Gson gson = new Gson();
                SurveyListDetails level1List = gson.fromJson(result, SurveyListDetails.class);
                dbhelper.updateSurveyList(level1List);
                String applicationLevels = json.getString("application_levels");
                editor = preferences.edit();
                editor.putString("app_Levels", applicationLevels);
                editor.apply();
            }
        } catch (Exception e) {
            Logger.logE("Login", "insertUserDetailsInPref error", e);
        }
    }
}