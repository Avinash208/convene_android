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
import org.json.JSONException;
import org.json.JSONObject;
import org.assistindia.convene.beansClassSetQuestion.CallApis;
import org.assistindia.convene.beansClassSetQuestion.OptionsBeen;
import org.assistindia.convene.database.ConveneDatabaseHelper;
import org.assistindia.convene.utils.CheckNetwork;
import org.assistindia.convene.utils.Constants;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.RestUrl;
import org.assistindia.convene.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;




public class OptionsAsyncTask extends AsyncTask<Context, Integer, String> {
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
    private CallApis optApi;
    private String globalModifiedDate ="";
    private static final String TABLE_NAME="Options";
    private static final String UPDATE_TIME_KEY="updated_time";

    /**
     * {@link OptionsAsyncTask constructor}
     * @param context param
     * @param activity param
     * @param optionsApi param
     */
    /*
     * Calling categories task constructor
     */
    public OptionsAsyncTask(Context context, Activity activity,CallApis optionsApi) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        optApi=optionsApi;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        dbhelper = ConveneDatabaseHelper.getInstance(context, preferences.getString(Constants.CONVENE_DB,""),preferences.getString("UID",""));
    }


    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";

        }
        try {
            List<NameValuePair> paramsL = new ArrayList<>();
            globalModifiedDate =dbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
            Logger.logV("","first time updated date" + globalModifiedDate);
            paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID","")));
            paramsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
            paramsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
            result = resturl.restUrlServerCall(activity,"choice-list" + "/", "post", paramsL, "");
            Logger.logV("the parameters are", "the params" + paramsL);
            Logger.logV("Choice ","Choice response" + result);
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
                optApi.optionsApi(9,false);
            }else {
                optApi.optionsApi(9,true);
            }
        }catch (Exception e){
            Logger.logE("","",e);
            optApi.optionsApi(9,true);
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
            Logger.logE("","",e);
        }
    }

    /**
     * methodToCallPagination method
     * @param jsonObject param
     */
    private void methodToCallPagination(JSONObject jsonObject) {
        //Modify by guru
        Logger.logV("globalModifiedDate", "the previous option api modified "+ globalModifiedDate);
        String tempModifiedMax = dbhelper.getLastUpDate(TABLE_NAME, UPDATE_TIME_KEY);
        Logger.logV("update date", "the after option api modified  "+tempModifiedMax);

        if(!globalModifiedDate.equalsIgnoreCase(tempModifiedMax)) {
            try {
                if (!jsonObject.isNull(TABLE_NAME) && jsonObject.getJSONArray(TABLE_NAME).length()>0){
                    globalModifiedDate = tempModifiedMax;
                    List<NameValuePair> paramsL = new ArrayList<>();
                    paramsL.add(new BasicNameValuePair("uId", preferences.getString("UID", "")));
                    globalModifiedDate = tempModifiedMax;
                    paramsL.add(new BasicNameValuePair("updatedtime", globalModifiedDate));
                    paramsL.add(new BasicNameValuePair("count", String.valueOf(dbhelper.getCursorCount(TABLE_NAME, UPDATE_TIME_KEY))));
                    result = resturl.restUrlServerCall(activity, "choice-list" + "/", "post", paramsL, "");
                    Logger.logV("the parameters are", "the params in the second time is" + paramsL);
                    parseResponse(result);
                }
            } catch (JSONException e) {
                Logger.logE("OptionsAsyncTask","facing error",e);
            }
        }else{
            Logger.logD("Conflict on Option date",tempModifiedMax);
            resturl.writeToTextFile("Conflict on Option date",tempModifiedMax,"getChoicelist");
        }
    }

    /**
     * methodToFillDb method
     * @param jsonObject param
     */
    private void methodToFillDb(JSONObject jsonObject) {
        try {
            JSONArray langArray = jsonObject.getJSONArray(TABLE_NAME);
            if (langArray.length() > 0) {
                Gson gson = new Gson();
                OptionsBeen level1List = gson.fromJson(result, OptionsBeen.class);
                dbhelper.updateOptions(level1List);
            }
            else
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("OptionsDbUpdated", false);
                editor.apply();
                ToastUtils.displayToastUi("Updated successfully", context);

            }
        } catch (Exception e) {
            Logger.logE("","",e);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("OptionsDbUpdated", false);
            editor.apply();
        }
    }
}
