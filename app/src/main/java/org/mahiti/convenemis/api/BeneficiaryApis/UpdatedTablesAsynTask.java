package org.mahiti.convenemis.api.BeneficiaryApis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.mahiti.convenemis.R;
import org.mahiti.convenemis.beansClassSetQuestion.UpdatedTablesInerface;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.RestUrl;
import org.mahiti.convenemis.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 8/2/17.
 */

public class UpdatedTablesAsynTask extends AsyncTask<Context, Integer, String> {
    private final Animation translatebu;
    /*
         * Declaring all the variables and views
         */
    private SharedPreferences preferences;
    private RestUrl resturl;
    private Context context;
    private CheckNetwork chNetwork;
    Activity activity;
    private ConveneDatabaseHelper dbhelper;
    private String result = "";
    private android.app.ProgressDialog loginDialog;
    private JSONObject tablesListObj;
    private UpdatedTablesInerface tablesInerface;
    private ProgressBar progressBar;
    private TextView beneficiaryStatus;
    private static final String TAG="UpdateTableAsyncTask";

    /**
     * UpdatedTablesAsynTask constructor
     * @param context param
     * @param activity  param
     * @param updatedTables param
     * @param progressBar param
     * @param beneficiaryStatus param
     */
    /*
     * Calling categories task constructor
     */
    public UpdatedTablesAsynTask(Context context, Activity activity, UpdatedTablesInerface updatedTables, ProgressBar progressBar, TextView beneficiaryStatus) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB,""),preferences.getString("UID",""));
        tablesInerface=updatedTables;
        tablesListObj=new JSONObject();
        this.progressBar=progressBar;
        this.beneficiaryStatus=beneficiaryStatus;
        translatebu= AnimationUtils.loadAnimation(context, R.anim.animationfile);
    }

    /**
     * onPreExecute method
     *
     */
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setProgress(5);
    }

    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";
        }
        try {
            List<NameValuePair> paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID", "")));
            Logger.logV("the json oobject is", "the json object is" + getTablesUpddates().toString());
            paramsL.add(new BasicNameValuePair("UpdatedDateTime", getTablesUpddates().toString()));
            result = resturl.restUrlServerCall(activity,"updated-tables" + "/", "post", paramsL, "");
            publishProgress();
            Logger.logV("the parameters are", "the params" + paramsL);
            parseResponse(result);

        } catch (Exception e) {
            Logger.logE(TAG,"",e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        ProgressUtils.CancelProgress(loginDialog);
        tablesInerface.updatedTablesList(tablesListObj,true);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(15);
        beneficiaryStatus.setTextColor(Color.parseColor("#d24645"));
        beneficiaryStatus.setText(context.getString(R.string.updating_beneficiary));
        beneficiaryStatus.setAnimation(translatebu);

    }

    /**
     * getTablesUpddates method
     * @return param
     */
    public JSONObject getTablesUpddates() {

        String block = dbhelper.getLastUpDate("Block",Constants.UPDATED_TIME);
        String langBlock = dbhelper.getLastUpDate("LanguageBlock",Constants.UPDATED_TIME);
        String question =dbhelper.getLastUpDate("Question",Constants.UPDATED_TIME);
        String langQuestion = dbhelper.getLastUpDate("LanguageQuestion",Constants.UPDATED_TIME);
        String assessment = dbhelper.getLastUpDate(Constants.ASSESSMENT,Constants.UPDATED_TIME);
        String langAssessment = dbhelper.getLastUpDate("LanguageAssessment",Constants.UPDATED_TIME);
        String skipMandatory = dbhelper.getLastUpDate("SkipMandatory",Constants.UPDATED_TIME);
        String skipRules= dbhelper.getLastUpDate("SkipRules",Constants.UPDATED_TIME);
        String options = dbhelper.getLastUpDate("Options",Constants.UPDATED_TIME);
        String langLables = dbhelper.getLastUpDate("LanguageLabels",Constants.UPDATED_TIME);
        String langOptions = dbhelper.getLastLanguageOptionsUpdates("LanguageOptions",Constants.UPDATED_TIME);
        JSONObject innerObject = new JSONObject();
        try {
            innerObject.put("Block", block);
            innerObject.put("LanguageBlock", langBlock);
            innerObject.put("Question", question);
            innerObject.put("LanguageQuestion", langQuestion);
            innerObject.put(Constants.ASSESSMENT, assessment);
            innerObject.put("LanguageAssessment", langAssessment);
            innerObject.put("SkipMandatory", skipMandatory);
            innerObject.put("SkipRules", skipRules);
            innerObject.put("Options", options);
            innerObject.put("LanguageLabels", langLables);
            innerObject.put("LanguageOptions", langOptions);

        } catch (JSONException e)
        {
            Logger.logE(TAG,"",e);
        }
        Logger.logV("the json object is","the object is"+innerObject);
        return innerObject;
    }

    /**
     * parseResponse method
     * @param result resullt
     */
    public void parseResponse(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            int status = jsonObject.getInt("status");
            String stateId = jsonObject.getString("state_id");
            SharedPreferences.Editor editor1 = preferences.edit();
            editor1.putString("state_id", stateId);
            editor1.apply();
            if (status == 2) {
                try {
                    updateApplicationAPKVersion(jsonObject);
                    JSONObject  langArray = jsonObject.getJSONObject("updatedTables");
                    if (langArray.length() > 0) {
                        tablesListObj=langArray;
                    }
                    else
                    {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("updatedTablesDbUpdated", false);
                        editor.apply();
                        ToastUtils.displayToastUi(context.getString(R.string.updateSuccess), context);

                    }
                } catch (Exception e) {
                    Logger.logE(TAG,"",e);

                }finally {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("updatedTablesDbUpdated", false);
                    editor.apply();
                }
            }
        } catch (Exception e) {
            Logger.logE(TAG,"",e);
        }
    }

    private void updateApplicationAPKVersion(JSONObject jsonObject) {
        try {
            int appVerstion=0;
            PackageManager manager = context.getPackageManager();
            PackageInfo info = null;
            try {
                info = manager.getPackageInfo(
                        context.getPackageName(), 0);
                appVerstion = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                Logger.logD(" sc card ","E"+e);
            }
            JSONObject UpdateAPKOBject=jsonObject.getJSONObject("updateAPK");
            SharedPreferences.Editor editor = preferences.edit();

            if (UpdateAPKOBject.getInt(Constants.APPVERSION)>appVerstion){
                editor.putInt(Constants.UPDATEDAPKVERSION,UpdateAPKOBject.getInt(Constants.APPVERSION));
                editor.putString("UPDATE_APK_RESPONSE",UpdateAPKOBject.toString());
                checkandSetPreference(UpdateAPKOBject,editor);
            }else{
                Logger.logD(Constants.UPDATEDAPKVERSION,"No Version change");
                editor.putInt(Constants.UPDATEDAPKVERSION,appVerstion);
            }
            editor.apply();
        } catch (JSONException e) {
            Logger.logE("Exception","in the Update APK version",e);
        }
    }

    private void checkandSetPreference(JSONObject updateAPKOBject, SharedPreferences.Editor editor) {
        try{
            if ("True".equalsIgnoreCase(updateAPKOBject.getString("forceUpdate"))) {
                editor.putBoolean("UPDATE_APK",true);
            }else{
                editor.putBoolean("UPDATE_APK",false);
            }
        }catch (Exception e){
            Logger.logD("checkandSetPreference","in the "+e);
        }
    }
}

