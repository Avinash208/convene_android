package org.mahiti.convenemis.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mahiti.convenemis.BeenClass.Response;
import org.mahiti.convenemis.beansClassSetQuestion.FillSurveyResponseInterface;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.network.InsertResponseTask;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.RestUrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by mahiti on 25/1/17.
 */

public class UpdateSurveyAsyncTask extends AsyncTask<Context, Integer, String> {
    private static final String BLOCK = "Block";
    private static final String TAG = "UpdateSurveyAsyncTask";
    public static final String UPDATE_TIME = "updated_time";
    /*
         * Declaring all the variables and views
         */
    private SharedPreferences preferences;
    private RestUrl resturl;
    private Context context;
    CheckNetwork chNetwork;
    Activity activity;
    ConveneDatabaseHelper dbOpenHelper;
    String result = "";
    android.app.ProgressDialog loginDialog;
    FillSurveyResponseInterface fillSurveyResponseInterface;

    String getSurveyModifiedDate;
    private DBHandler syncSurveyHandler;
    public android.database.sqlite.SQLiteDatabase database;
    /*
     * Calling categories task constructor
     */
    public UpdateSurveyAsyncTask(Context context, Activity activity, FillSurveyResponseInterface  fillSurveyResponseInterface) {
        this.context = context;
        resturl = new RestUrl(context);
        chNetwork = new CheckNetwork(context);
        this.activity = activity;
        this.fillSurveyResponseInterface=fillSurveyResponseInterface;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        syncSurveyHandler = new DBHandler(context);
        dbOpenHelper = ConveneDatabaseHelper.getInstance(context, preferences.getString("CONVENEDB",""),preferences.getString("UID",""));
    }
    @Override
    protected String doInBackground(Context... params) {
        if (!chNetwork.checkNetwork()) {
            return "";
        }
        try {
            getSurveyModifiedDate=syncSurveyHandler.getSurveyLastUpDate("Survey", UPDATE_TIME);
            List<NameValuePair> paramsL = new ArrayList<>();
            paramsL.add(new BasicNameValuePair("userid", preferences.getString("UID","")));
            paramsL.add(new BasicNameValuePair("serverdatetime", syncSurveyHandler.getSurveyLastUpDate("Survey", UPDATE_TIME)));
            result = resturl.restUrlServerCall(activity,"responses" + "/", "post", paramsL, "");
            Logger.logV("the parameters are", "the params" + paramsL);
            parseResponse(result);

        } catch (Exception e) {
            Logger.logE("Exception","in",e);

        }

        return result;
    }

    @Override
    protected void onPostExecute(String categoryList) {
        super.onPostExecute(categoryList);
        fillSurveyResponseInterface.fillSurveyResponseInterfaceCallBack(true);
    }
    public void parseResponse(String result) {
        try {
            Logger.logV("the parameters are", "the result" + result);
            JSONObject jsonObject = new JSONObject(result);

            int status = jsonObject.getInt("status");
            if (status == 2) {
                methodToStoreDatabase(jsonObject,result);
                methodToCallPagination(jsonObject);
            }
        } catch (Exception e) {
            Logger.logE("Exception in adapter","find",e);
        }
    }

    private void methodToCallPagination(JSONObject jsonObject) {
      try{
          JSONArray blockArray = jsonObject.getJSONArray("ResponsesData");
          if (blockArray.length() > 0) {

                  List<NameValuePair> paramsL = new ArrayList<>();
                  paramsL.add(new BasicNameValuePair("userid", preferences.getString("UID","")));
                  paramsL.add(new BasicNameValuePair("serverdatetime", syncSurveyHandler.getSurveyLastUpDate("Survey", UPDATE_TIME)));
                  String secondResult = resturl.restUrlServerCall(activity,"responses" + "/", "post", paramsL, "");
              Logger.logV("the parameters are", "the params in the second time is" + paramsL);
                  parseResponse(secondResult);
              }
          } catch (Exception e){
          Logger.logE("the status", "the questionsArray lenth is " ,e);
      }
    }

    /**
     * methdo to check block array and store to database .
     * @param jsonObject
     * @param result
     */
    private void methodToStoreDatabase(JSONObject jsonObject, String result) {
        try {
            JSONArray jsonArray= new JSONArray();
            JSONArray ja_data = jsonObject.getJSONArray("ResponsesData");
            int length = ja_data.length();
            for(int i=0; i<length; i++) {
                JSONObject jsonObj = ja_data.getJSONObject(i);
                insertIntoSurveyTable(jsonObj.getString("response_id"),jsonObj.getString("response_dump"),jsonObj.getString("server_date_time"),
                        jsonObj.getString("survey_id"),jsonObj.getString("cluster_name"),jsonObj.getInt("cluster_id"),jsonObj.getString("bene_uuid"),
                        jsonObj.getString("location"));
            }
        } catch (Exception e) {
            Logger.logE("the status", "the questionsArray lenth is " ,e);
        }

    }

    private void insertIntoSurveyTable(String response_UUID, String response_DUMP, String server_modified_date,
                                       String survey_id, String cluster_name, int cluserid,String uuid, String location) {
         Map<String, String> values = new HashMap<>();
        values.put("start_survey_status", "0");
        values.put("inv_id", preferences.getString("uId",""));
        values.put("start_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
        values.put("end_date", "0");
        values.put("version_num", "");
        values.put("app_version", "");
        values.put("language", String.valueOf(preferences.getInt("selectedLangauge",1)));
        values.put("lat", preferences.getString("LATITUDE",""));
        values.put("long", preferences.getString("LONGITUDE",""));
        values.put("survey_status", "2");
        values.put("sync_status", "2");
        values.put("sync_date", server_modified_date);
        values.put("mode_status", "");
        values.put("specimen_id", "");
        values.put("survey_key", "0");
        values.put("reason", "");
        values.put("paper_entry_reason", "");
        values.put("reason_off_survey", "2");
        values.put("last_qcode", "");
        values.put("survey_status2", "0");
        values.put("survey_status1", "0");
        values.put("domain_id", "");
        values.put("p1_charge", "");
        values.put("gps_tracker", "");
        values.put("consent_status", "");
        values.put("typology_code", survey_id);
        values.put("typocodes", survey_id);
        values.put("sectionId", "");
        values.put("cluster_id", String.valueOf(cluserid));
        Logger.logD("cluserid----->", String.valueOf(cluserid));
        values.put("clustername", cluster_name);
        values.put("clusterkey", "");
        values.put("clusterkey", "");
        values.put("level1", "");
        values.put("level2", "");
        values.put("level3", "");
        values.put("level4", "");
        values.put("level5", "");
        values.put("level6", "");
        values.put("level7", "");
        values.put("response_dump", response_DUMP);
       /* if((0)==prefs.getInt(Constants.PARENT_ID,0)){
            values.put("response_parent_uuid","");
        }else{
            String parentResponseId=syncSurveyHandler.getParentResponseUidFromSurvey(String.valueOf(prefs.getInt(Constants.SURVEY_ID, 0)));*/
            values.put("response_parent_uuid",response_UUID);
       // }
        values.put("ben_uuid",response_UUID);
        values.put("uuid",uuid);
        values.put("beneficiary_details", "");
        values.put("beneficiary_details_status", "1");
        if (true) {
            values.put("extra_column", "1");
        } else {
            values.put("extra_column", "0");
        }
        Logger.logV("SyncClass", "the response is" + String.valueOf(values));
        Logger.logV("SyncClass", "the response is" + String.valueOf(values));
        String response = syncSurveyHandler.insertSurveyDataToDB(values);
        Logger.logV("response", "the response is" + response);
        updateToResponseTable(response,response_DUMP,location);
    }

    /**
     * @param response response
     * @param response_DUMP response_DUMP
     * @param location
     */
    private void updateToResponseTable(String response, String response_DUMP, String location) {
        UpdateServerResponsetoDatabase(response_DUMP,response,location);
    }

    private void UpdateServerResponsetoDatabase(String getResponseResult, String surveyPrimaryKey, String location) {
        final String surveyResponseID= surveyPrimaryKey;
       try{
           List<Response> collectResponse= new ArrayList<>();
           JSONObject jsonObjectGetQuestion= new JSONObject(getResponseResult);

           Iterator<String> iter = jsonObjectGetQuestion.keys();
           while (iter.hasNext()) {
               String key = iter.next();
               Logger.logD(TAG,"-->"+key);
               if (!key.equals("address")) {
                   String getQuestionType=dbOpenHelper.getQuestionType(key);
                   Object value = jsonObjectGetQuestion.get(key);
                   Logger.logD(TAG,"-->"+value);
                   if (("R").equalsIgnoreCase(getQuestionType) || ("S").equalsIgnoreCase(getQuestionType) ) {
                       Response responseTemp = new Response(key, "", String.valueOf(value), "", Integer.parseInt(key), 0, "", 0, 0, getQuestionType);
                       collectResponse.add(responseTemp);
                   } else if (("T").equalsIgnoreCase(getQuestionType) || ("D").equalsIgnoreCase(getQuestionType)){
                       Response responseTemp = new Response(key, String.valueOf(value), "", "", Integer.parseInt(key), 0, "", 0, 0, getQuestionType);
                       collectResponse.add(responseTemp);
                   }
               }else{
                 /* if (location!=null) {
                      JSONObject jsonObject = new JSONObject(location);
                      syncSurveyHandler.updateAddressRecordFromServer(jsonObject, surveyPrimaryKey);
                  }*/


               }
           }
           syncSurveyHandler.deleteExistingResponse(surveyResponseID);
           new InsertResponseTask().insertResponseTask(collectResponse,syncSurveyHandler ,surveyResponseID);
       }catch (Exception e){
           Logger.logE(TAG,"Exception in the filling the response",e);
       }

    }


}
