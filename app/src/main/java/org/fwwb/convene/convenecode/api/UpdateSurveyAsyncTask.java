package org.fwwb.convene.convenecode.api;

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
import org.fwwb.convene.convenecode.BeenClass.Response;
import org.fwwb.convene.convenecode.beansClassSetQuestion.FillSurveyResponseInterface;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.network.InsertResponseTask;
import org.fwwb.convene.convenecode.utils.CheckNetwork;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.RestUrl;

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
//        syncSurveyHandler.AllPreviousResponse();
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

            JSONArray ja_data = jsonObject.getJSONArray("ResponsesData");
            int length = ja_data.length();
            for(int i=0; i<length; i++) {
                JSONObject jsonObj = ja_data.getJSONObject(i);
                       insertIntoSurveyTable(jsonObj.getString("response_id"),jsonObj.getString("response_dump"),jsonObj.getString("server_date_time"),
                        jsonObj.getString("survey_id"),jsonObj.getString("cluster_name"),jsonObj.getInt("cluster_id"),jsonObj.getString("bene_uuid"),
                        jsonObj.getString("location"),jsonObj.getString("cluster_beneficiary"),
                               jsonObj.getString("grid_inline_questions"), jsonObj.getString("app_answer_on") ,jsonObj.getString("training_survey_id"),jsonObj);
            }
        } catch (Exception e) {
            Logger.logE("the status", "the questionsArray lenth is " ,e);
        }

    }

    private void insertIntoSurveyTable(String response_UUID, String response_DUMP, String server_modified_date,
                                       String surveyId, String cluster_name, int cluserid, String uuid, String location,
                                       String cluster_beneficiary, String gridResponse, String surveyCaptureDate, String jsonObj, JSONObject obj) {
         Map<String, String> values = new HashMap<>();
        values.put("start_survey_status", "0");
        values.put("inv_id", preferences.getString("uId",""));
        values.put("start_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
        values.put("end_date", surveyCaptureDate);
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
        values.put("typology_code", surveyId);
        values.put("typocodes", surveyId);
        values.put("sectionId", "");
        values.put("cluster_id", String.valueOf(cluserid));
        Logger.logD("cluserid----->", String.valueOf(cluserid));
        values.put("clustername", cluster_name);
        values.put("clusterkey", "");

        values.put("level1", "");
        values.put("level2", "");
        values.put("level3", "");
        values.put("level4", "");
        values.put("level5", "");
        values.put("level6", "");
        values.put("level7", "");
        values.put("response_dump", response_DUMP);
        values.put("response_parent_uuid",response_UUID);
        values.put("ben_uuid",response_UUID);
        values.put("uuid",uuid);
        values.put("beneficiary_details", "");
        values.put("beneficiary_ids", cluster_beneficiary);
        values.put("beneficiary_details_status", "1");
        values.put("training_survey_id",jsonObj );
        try {
            if (obj.has("training_uuid"))
                values.put("trainingUuid",obj.getString("training_uuid"));
            if (obj.has("batch_uuid"))
                values.put("batchUuid",obj.getString("batch_uuid"));
        } catch (Exception e) {
            Logger.logE(TAG,e.getMessage(),e);
        }
        values.put("extra_column", "1");
        Logger.logV("SyncClass", "the response is" + String.valueOf(values));
        String response = syncSurveyHandler.insertSurveyDataToDB(values);
        Logger.logV("response", "the response is" + response);
        updateToResponseTable(response,response_DUMP,location,gridResponse,surveyId);
    }

    /**
     * @param response response
     * @param response_DUMP response_DUMP
     * @param location
     * @param surveyId
     */
    private void updateToResponseTable(String response, String response_DUMP, String location, String gridResponseIds, String surveyId) {
        UpdateServerResponsetoDatabase(response_DUMP,response,location,gridResponseIds,surveyId);
    }

    private void UpdateServerResponsetoDatabase(String getResponseResult, String surveyPrimaryKey, String location, String gridResponseids, String surveyId) {
       List<String> getGridResponseIds= new ArrayList<>();
        if (!gridResponseids.equals("")){
            try {
                JSONArray jsonArray =new JSONArray(gridResponseids);
                for (int i=0;i<jsonArray.length();i++){
                    getGridResponseIds.add(String.valueOf(jsonArray.getInt(i)));
                }

            } catch (JSONException e) {
               Logger.logD(TAG,"Grid Response Json"+e);
            }
        }
        final String surveyResponseID= surveyPrimaryKey;

       try{
           List<Response> collectResponse= new ArrayList<>();
           JSONObject jsonObjectGetQuestion= new JSONObject(getResponseResult);

           Iterator<String> iter = jsonObjectGetQuestion.keys();
           while (iter.hasNext()) {
               String key = iter.next();
               Logger.logD(TAG,"-->"+key);
               if (getGridResponseIds.contains(key)){
                  JSONObject innerJsonObject= new JSONObject(jsonObjectGetQuestion.get(key).toString());
                   Iterator<String> innerSubQuestioniter = innerJsonObject.keys();
                   while (innerSubQuestioniter.hasNext()) {
                       String innerKey = innerSubQuestioniter.next();
                       JSONObject inInnerObject= new JSONObject(innerJsonObject.get(innerKey).toString());
                       Iterator<String> iniInnerSubQuestioniter = inInnerObject.keys();
                       while (iniInnerSubQuestioniter.hasNext()) {
                           String inInnerKey = iniInnerSubQuestioniter.next();
                           String getQuestionType=dbOpenHelper.getAssessmentType(inInnerKey);
                           Object value = inInnerObject.get(inInnerKey);
                           Logger.logD(TAG,"-->"+value);
                           if (("R").equalsIgnoreCase(getQuestionType) || ("S").equalsIgnoreCase(getQuestionType) ) {
                               Response responseTemp = new Response(key, "", String.valueOf(value), "0", Integer.parseInt(key), Integer.parseInt(innerKey), surveyId, Integer.parseInt(inInnerKey), Integer.parseInt(innerKey), getQuestionType);
                               collectResponse.add(responseTemp);
                           } else if (("T").equalsIgnoreCase(getQuestionType) || ("D").equalsIgnoreCase(getQuestionType) || ("C").equalsIgnoreCase(getQuestionType)){
                               Response responseTemp = new Response(key, String.valueOf(value), "", "0", Integer.parseInt(key), Integer.parseInt(innerKey), surveyId, Integer.parseInt(inInnerKey), Integer.parseInt(innerKey), getQuestionType);
                               collectResponse.add(responseTemp);
                           }
                       }
                   }
               }
               if (!key.equals("address")) {
                   String getQuestionType=dbOpenHelper.getQuestionType(key);
                   Object value = jsonObjectGetQuestion.get(key);
                   Logger.logD(TAG,"-->"+value);
                   if (("R").equalsIgnoreCase(getQuestionType) || ("S").equalsIgnoreCase(getQuestionType) ) {
                       Response responseTemp = new Response(key, "", String.valueOf(value), "0", Integer.parseInt(key), 0, surveyId, 0, 0, getQuestionType);
                       collectResponse.add(responseTemp);
                   } else if (("T").equalsIgnoreCase(getQuestionType) || ("D").equalsIgnoreCase(getQuestionType) ||("AI").equalsIgnoreCase(getQuestionType) || ("C").equalsIgnoreCase(getQuestionType)){
                       Response responseTemp = new Response(key, String.valueOf(value), "", "0", Integer.parseInt(key), 0, surveyId, 0, 0, getQuestionType);
                       collectResponse.add(responseTemp);
                   }
               }else{
                  if (location!=null) {
                      JSONObject jsonObject = new JSONObject(location);
                      syncSurveyHandler.updateAddressRecordFromServer(jsonObject, surveyPrimaryKey);
                  }


               }
           }
           syncSurveyHandler.deleteExistingResponse(surveyResponseID);
           new InsertResponseTask().insertResponseTask(collectResponse,syncSurveyHandler ,surveyResponseID);
           updateOtherChoice(getResponseResult,surveyPrimaryKey);

       }catch (Exception e){
           Logger.logE(TAG,"Exception in the filling the response",e);
       }

    }

    private void updateOtherChoice(String getResponseResult, String surveyPrimaryKey) {
        List<Response> collectResponse= new ArrayList<>();
        try {
            JSONObject  jsonObjectGetQuestion = new JSONObject(getResponseResult);
            Iterator<String> iter = jsonObjectGetQuestion.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                if (key.equals("other")){
                    JSONObject questionMark = jsonObjectGetQuestion.getJSONObject("other");
                    Iterator keys = questionMark.keys();
                    while(keys.hasNext()) {
                        String currentDynamicKey = (String)keys.next();
                        JSONObject currentDynamicValue = questionMark.getJSONObject(currentDynamicKey);
                        Iterator innerkeys = currentDynamicValue.keys();
                        while(innerkeys.hasNext()) {
                            String innerKeyKey = (String)innerkeys.next();
                            Object value = currentDynamicValue.get(innerKeyKey);
                            Logger.logD(TAG,"other-->"+value.toString());
                            for (int l=0;l<collectResponse.size();l++){
                                Response response=collectResponse.get(l);
                                if (response.getQ_id().equalsIgnoreCase(currentDynamicKey)){
                                    response.setSub_questionId(value.toString());
                                }
                            }
                             syncSurveyHandler.UpdateOtherchoice(currentDynamicKey, value.toString(),surveyPrimaryKey);

                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}