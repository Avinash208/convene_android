package org.assistindia.convene;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.assistindia.convene.database.CommonDao;
import org.assistindia.convene.database.DBHandler;
import org.assistindia.convene.database.DataBaseMapperClass;
import org.assistindia.convene.database.Utilities;
import org.assistindia.convene.utils.CheckNetwork;
import org.assistindia.convene.utils.ClusterInfo;
import org.assistindia.convene.utils.Logger;
import org.assistindia.convene.utils.MultiPartRestClient;
import org.assistindia.convene.utils.Operator;
import org.assistindia.convene.utils.RestUrl;
import org.assistindia.convene.utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AutoSyncActivity extends Activity {
    public static final String TAG = "AutoSyncActivity";
    private RestUrl autoSyncResturl;
    CheckNetwork autoSyncChckNework;
    private SharedPreferences autoSyncPreferences;
    SQLiteDatabase autoSyncDatabase;
    DBHandler autoSyncHandler;
    Double latitude;
    Double longitude;
    int hrId;
    String startDate;
    String endDate;
    String versionNumber;
    int languageId;
    int surveyStatus;
    String autoSyncCreatedOn;
    String reason;
    String paperEntryReason = "";
    int autoSyncSurveyKey;
    String latestAppVersion = "";


    String autoSyncSurveyID = "";
    String autoSyncSurveyIdNumber = "";
    String modeStatus;
    String modeStatusEach;
    List<String> modeStatusList;
    String sampleId = "";
    String clusterForSync = "";
    String bloodCollected = "";
    Context context;
    Operator op;
    String offSurveyReason = "";
    String lastQcode = "";
    String online = "0";
    String part2Charge = "";
    String gpsTracker = "";
    String consentStatus = "0";
    String typologyCode = "";
    List<String> imageQIdList;
    List<String> imageCapturedTimeList;
    List<String> imageCapturedFilePathList;
    String clusterKey;
    String clusterMember;
    String beneficiaryDetails;
    String beneficiary_id = "0";
    String facility_id = "0";
    String uuid = "";
    String beneficiaryTypeId;
    String captureDate;
    String facilityTypeId;
    private String server_primary_key = "";
    private static final String GPS_TRACKER = "gps_tracker";
    private static final String FACILITY_TYPE_ID = "facility_type_id";
    private static final String CLUSTER_ID = "cluster_id";
    private static final String REASON_KEY = "reason";
    private static final String POST_PARAMETERS_VALUE = "the posting parameters values are";
    private static final String PLAY_STORE = "playstore";
    private static final String RESPONSE_ID_KEY = "response_id";
    private static final String exceptionStr ="Exception in fetchDatabaseValues";
    private static final String surveyStatusStr = "survey_status";
    private static final String benTypeIdStr ="beneficiary_type_id";


    public AutoSyncActivity(Context context) {
        this.context = context;
        SQLiteDatabase.loadLibs(context);
        autoSyncChckNework = new CheckNetwork(context);
        autoSyncResturl = new RestUrl(context);
        autoSyncHandler = new DBHandler(context);
        op = new Operator(context);
        autoSyncPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void callingAutoSync(int fromValue1) {
        SurveyUpdateTask task = new SurveyUpdateTask(context, fromValue1);
        task.execute();
    }

    /**
     * This async task will call at while syncing the survey
     */
    public class SurveyUpdateTask extends AsyncTask<Context, Integer, String> {
        Context mContext;
        int fromValue;
        String domainId = "";


        public SurveyUpdateTask(Context context, int fromValues) {

            mContext = context;
            fromValue = fromValues;
        }

        @Override
        protected String doInBackground(Context... arg0) {
            modeStatusList = new ArrayList<>();
            List<String> surveyIdList = new ArrayList<>();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            String imei = tm.getDeviceId();
            CommonDao commondao = new CommonDao(mContext);

            /*Selecting the list pending records as survey id's and then selecting the each record and its responce's finally sending to
             server*/
            try {
                String selectQuery1 = "Select uuid,mode_status,survey_status From Survey where survey_status='1'";
                autoSyncDatabase = autoSyncHandler.getdatabaseinstance();
                Cursor cursorSurveyId = autoSyncDatabase.rawQuery(selectQuery1, null);
                if (cursorSurveyId == null || !cursorSurveyId.moveToFirst()) {
                    setSendingSurveyFlag();
                    return null;
                }
                do {
                    modeStatus = cursorSurveyId.getString(cursorSurveyId.getColumnIndex("mode_status"));
                    autoSyncSurveyIdNumber = cursorSurveyId.getString(cursorSurveyId.getColumnIndex("uuid"));
                    surveyIdList.add(autoSyncSurveyIdNumber);
                    modeStatusList.add(modeStatus);
                } while (cursorSurveyId.moveToNext());
                cursorSurveyId.close();

                if (surveyIdList.isEmpty()) {
                    setSendingSurveyFlag();
                    return null;
                }
                for (int i = 0; i < surveyIdList.size(); i++) {
                    surveyIdListLooping(surveyIdList, i, imei, commondao);
                }

            } catch (Exception e) {
                ToastUtils.displayToastUi(autoSyncPreferences.getString(ClusterInfo.probOccurredWhileDataSend, ""), mContext);
                Logger.logE(AutoSyncActivity.class.getSimpleName(), "Exception in SurevyUpdateTask outside loop", e);
                autoSyncResturl.writeToTextFile(Log.getStackTraceString(e), "", "SurveydataSending");
            }
            setSendingSurveyFlag();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // Proxy the call to the Activity (Not using here)
        }


        /**
         *
         * @param surveyIdList
         * @param i
         * @param imei
         * @param commondao
         */
        public void surveyIdListLooping(List<String> surveyIdList, int i, String imei, CommonDao commondao) {
            try {
                autoSyncDatabase = autoSyncHandler.getdatabaseinstance();
                autoSyncSurveyID = surveyIdList.get(i);
                modeStatusEach = modeStatusList.get(i);
                String query = "Select * From Survey where uuid='" + autoSyncSurveyID + "'";
                Cursor cursor1 = autoSyncDatabase.rawQuery(query, null);
                /*Calling method for fetching local database data to local variables*/
                setDatabaseData(cursor1);

                String selectQuery = "SELECT * FROM Response where survey_id='" + autoSyncSurveyID + "' group by q_id";

                autoSyncDatabase = autoSyncHandler.getdatabaseinstance();

                Cursor cursor = autoSyncDatabase.rawQuery(selectQuery, null);
                /*Calling function for Synchronizing data to server*/
                fetchDatabaseValues(cursor, imei, commondao, autoSyncSurveyID);
            } catch (Exception e) {
                ToastUtils.displayToastUi(autoSyncPreferences.getString(ClusterInfo.probOccurredWhileDataSend, ""), mContext);
                autoSyncResturl.writeToTextFile(Log.getStackTraceString(e), "", "SurveydataSending");
                Logger.logE(AutoSyncActivity.class.getSimpleName(), "Exception in SurevyUpdateTask", e);
            }
        }

        /*
        Setting sendingSurvey flag to recall server task next time
         */
        public void setSendingSurveyFlag() {

            if (autoSyncPreferences != null) {
                SharedPreferences.Editor editor = autoSyncPreferences.edit();
                editor.putBoolean("sendingSurvey", false);
                editor.apply();
            }
        }

        /**
         * fetching database data Answers and Syncto server to local variables
         */
        public void fetchDatabaseValues(Cursor cursor, String imei, CommonDao commonDao, String sectionId) {
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }
            /*
            getAnswersFromCursor is method for getting all answers to one global object as array
             */
            JSONObject array = getAnswersFromCursor(cursor, commonDao);
            Logger.logD("JSONARRAY","JSONARRAY OF ALL ANSWERS" + array.toString());
            /*
            Getting tab details for this particular record
             */
            JSONObject surveyList = autoSyncHandler.getTabInfoRecords("Select * from Tabdetails where survey_id = '" + autoSyncSurveyID+"'");

            /*Calling method setPostParameters for setting postparameters from local variables that are fetched from local database*/

            if (!autoSyncChckNework.checkNetwork()) {
                Logger.logD("INTERNET", "No network connection");
                return;
            }

           /*Calling method for sending data to server from phone*/

            try {

                String result = MultiPartRestClient.run(mContext, setPostParameters(imei, String.valueOf(surveyList), "0", array));

                if (result == null) {
                    return;
                }
                Logger.logD(TAG,"API result - "+result);
                JSONObject json = new JSONObject(result);
                String res = json.getString("responseType");
                if (!"1".equals(res)) {
                    return;
                }
                // getting back the primary key from the server and storing in the survey table
                int responseID=json.getInt(RESPONSE_ID_KEY);
               int getUpdatedPrimaryKey= autoSyncHandler.updateSurveyDataToDB(autoSyncSurveyID,responseID);
                Logger.logD("getUpdatedPrimaryKey","Updated PrimaryKey"+responseID+"the table PK->"+getUpdatedPrimaryKey);
                SharedPreferences.Editor editor = autoSyncPreferences.edit();
                editor.putString("latest_app_version_feeds", json.getString("latestappversion"));
                editor.putString("latest_db_version_feeds", json.getString("latestdbversion"));
                editor.putString("q_db_version", json.getString("app_db_ve"));
                editor.apply();
                Intent intent = new Intent(context, MyIntentService.class);
                context.startService(intent);
               // new PeriodicityNewAsyncTask(context).execute();

            } catch (Exception e) {
                Logger.logE(AutoSyncActivity.class.getSimpleName(), "Exception in fetchDatabaseValues outside loop", e);
                /*Sending error to server and storing in sdcard*/
                ToastUtils.displayToastUi(autoSyncPreferences.getString(ClusterInfo.probOccurredWhileDataSend, ""), mContext);
                autoSyncResturl.writeToTextFile(Log.getStackTraceString(e), String.valueOf(array), "SyncSurveyActivity_SyncFunction");
            }
        }


        /**
         *
         * @param cursor
         * @param commonDao
         * @return
         */
        public JSONObject getAnswersFromCursor(Cursor cursor, CommonDao commonDao) {
            JSONArray array = new JSONArray();
            JSONArray responseArray = new JSONArray();
            JSONObject qidObject = new JSONObject();
            imageCapturedTimeList = new ArrayList<>();
            imageQIdList = new ArrayList<>();
            imageCapturedFilePathList = new ArrayList<>();

            do {
                JSONObject valueKeypareArray = new JSONObject();
                JSONArray qidValueArray = new JSONArray();
                int autoSyncQidNumber = cursor.getInt(cursor.getColumnIndex("q_id"));
                String autoSyncAnsCode = cursor.getString(cursor.getColumnIndex("ans_code"));
                String subQuestionCode = cursor.getString(cursor.getColumnIndex("sub_questionId"));
                String subQuestionId = cursor.getString(cursor.getColumnIndex("primarykey"));
                String autoSyncAnswer = cursor.getString(cursor.getColumnIndex("ans_text"));
                String surveyPrimaryKey = cursor.getString(cursor.getColumnIndex("survey_id"));
                Logger.logV("answers are", autoSyncAnswer);
                String qtype = cursor.getString(cursor.getColumnIndex("qtype"));
                int groupID= cursor.getInt(cursor.getColumnIndex("group_id"));
                int primaryID= cursor.getInt(cursor.getColumnIndex("primary_id"));
                int jsondump= cursor.getInt(cursor.getColumnIndex("response_dump_pid"));
                String jsonDump="";
                if (jsondump!=0){
                    qidValueArray=   DataBaseMapperClass.getJsonObject(autoSyncQidNumber,autoSyncDatabase,autoSyncSurveyID,qidValueArray);
                }

               /*Sending only english answers to back even if it is english answers it fetching from db and sending back*/
                {
                    autoSyncAnswer = getEnglishText(autoSyncAnswer, autoSyncAnswer);
                }
                String answeredOn = cursor.getString(cursor.getColumnIndex("answered_on"));
                String qCodes = cursor.getString(cursor.getColumnIndex("q_code"));
                try {
                   if (qtype.equalsIgnoreCase("T") || qtype.equalsIgnoreCase("C") || qtype.equalsIgnoreCase("D") || qtype.equalsIgnoreCase("AI"))
                       valueKeypareArray.put(qtype+"_"+subQuestionId+"_"+String.valueOf(groupID),autoSyncAnswer);
                    else if(qtype.equals("R") || qtype.equals("S") )
                        valueKeypareArray.put(qtype+"_"+subQuestionId+"_"+String.valueOf(groupID),autoSyncAnsCode);
                   else if(qtype.equals("AW")){
                       Map<String,String> getAddressResponse= autoSyncHandler.getAddressResponse(surveyPrimaryKey);
                       valueKeypareArray.put("1",getAddressResponse.get("1"));
                       valueKeypareArray.put("2",getAddressResponse.get("2"));
                       valueKeypareArray.put("3",getAddressResponse.get("3"));
                       valueKeypareArray.put("4",getAddressResponse.get("4"));
                       valueKeypareArray.put("5",getAddressResponse.get("5"));
                       valueKeypareArray.put("6",getAddressResponse.get("6"));
                       valueKeypareArray.put("7",getAddressResponse.get("7"));
                   }
                    else
                       valueKeypareArray.put(qtype+"_"+subQuestionId+"_"+String.valueOf(groupID),String.valueOf(primaryID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    qidObject.put(String.valueOf(autoSyncQidNumber),qidValueArray);

                } catch (JSONException e) {
                    Logger.logE(AutoSyncActivity.class.getSimpleName(), exceptionStr, e);
                }
               qidValueArray.put(valueKeypareArray);
                JSONObject innerObject = new JSONObject();
                try {
                    innerObject.put("q_id", autoSyncQidNumber);
                    innerObject.put("ac", autoSyncAnsCode);
                    innerObject.put("an", autoSyncAnswer);
                    innerObject.put("anon", answeredOn);
                    innerObject.put("subq", subQuestionCode);
                    innerObject.put("qc", qCodes);
                    innerObject.put("group_id", groupID);
                    innerObject.put("op_id", primaryID);
                    innerObject.put("index", subQuestionId);
                    innerObject.put("jsondump", jsonDump);
                    String answerImageCheck = autoSyncAnswer.toLowerCase();
                    if (answerImageCheck.contains(".png") || answerImageCheck.contains(".jpeg") || answerImageCheck.contains(".jpg") || answerImageCheck.contains(".mp4") || answerImageCheck.contains(".3gp")) {
                        imageQIdList.add(String.valueOf(autoSyncQidNumber));
                        imageCapturedTimeList.add(answeredOn);
                        imageCapturedFilePathList.add(autoSyncAnswer);
                    }
                    array.put(innerObject);
                    responseArray.put(qidObject);
                } catch (JSONException e) {
                    Logger.logE(AutoSyncActivity.class.getSimpleName(), exceptionStr, e);
                }
                Logger.logV("answers are", responseArray.toString());
            } while (cursor.moveToNext());
            cursor.close();
            return qidObject;
        }

        /*
         Null condition check and assigning the data
         */
        /**
         * @param answerText
         * @param autoSyncAnswer
         * @return
         */
        public String getEnglishText(String answerText, String autoSyncAnswer) {

            if (answerText != null && !"".equals(answerText)) {
                return answerText;
            }
            return autoSyncAnswer;
        }

        /*
          fetching database data to local variables
         */
        /**
         * @param cursor1
         */
        public void setDatabaseData(Cursor cursor1) {
            if (cursor1 != null && cursor1.moveToFirst()) {
                do {
                    String[] columns = {"latitude", "longitude", "user_id", "start_date", "end_date", "version_num", "app_version",
                            "language_id", surveyStatusStr, REASON_KEY, CLUSTER_ID, "specimen_id", "survey_key", "",
                            "", "last_qcode", "paper_entry_reason", "", GPS_TRACKER, "",
                            "", "consent_status", "survey_ids", "cluster_key","cluster_name",benTypeIdStr,FACILITY_TYPE_ID};
                    latitude = cursor1.getDouble(cursor1.getColumnIndex(columns[0]));
                    longitude = cursor1.getDouble(cursor1.getColumnIndex(columns[1]));
                    hrId = cursor1.getInt(cursor1.getColumnIndex(columns[2]));
                    startDate = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                    endDate = cursor1.getString(cursor1.getColumnIndex(columns[4]));
                    versionNumber = cursor1.getString(cursor1.getColumnIndex(columns[5]));
                    latestAppVersion = String.valueOf(cursor1.getFloat(cursor1.getColumnIndex(columns[6])));
                    languageId = cursor1.getInt(cursor1.getColumnIndex(columns[7]));
                    surveyStatus = cursor1.getInt(cursor1.getColumnIndex(columns[8]));
                    reason = cursor1.getString(cursor1.getColumnIndex(columns[9]));
                    clusterForSync = cursor1.getString(cursor1.getColumnIndex(columns[10]));
                    sampleId = cursor1.getString(cursor1.getColumnIndex(columns[11]));
                    autoSyncSurveyKey = cursor1.getInt(cursor1.getColumnIndex(columns[12]));
              //      bloodCollected = cursor1.getString(cursor1.getColumnIndex(columns[13]));
                 //   offSurveyReason = cursor1.getString(cursor1.getColumnIndex(columns[14]));
                    lastQcode = cursor1.getString(cursor1.getColumnIndex(columns[15]));
                    paperEntryReason = cursor1.getString(cursor1.getColumnIndex(columns[16]));
                  //  online = cursor1.getString(cursor1.getColumnIndex(columns[17]));
                    gpsTracker = cursor1.getString(cursor1.getColumnIndex(columns[18]));
              //      domainId = cursor1.getString(cursor1.getColumnIndex(columns[20]));
                //    consentStatus = cursor1.getString(cursor1.getColumnIndex(columns[21]));
                    typologyCode = cursor1.getString(cursor1.getColumnIndex(columns[22]));
                    clusterKey = cursor1.getString(cursor1.getColumnIndex(columns[23]));
                  //  clusterMember ="Attibele";// cursor1.getString(cursor1.getColumnIndex(columns[24]));
                    clusterMember =cursor1.getString(cursor1.getColumnIndex(columns[24]));
                    beneficiaryTypeId=cursor1.getString(cursor1.getColumnIndex(benTypeIdStr));
                    captureDate =cursor1.getString(cursor1.getColumnIndex("capture_date"));
                    if(beneficiaryTypeId==null){
                        beneficiaryTypeId="0";
                    }
                    facilityTypeId=cursor1.getString(cursor1.getColumnIndex(FACILITY_TYPE_ID));
                    if(facilityTypeId==null){
                        facilityTypeId="0";
                    }
                    beneficiaryDetails=cursor1.getString(cursor1.getColumnIndex("beneficiary_ids"));
                   /* facility_id=cursor1.getString(cursor1.getColumnIndex("facility_ids"));
                    if(facility_id.isEmpty()){
                        facility_id="0";
                    }*/
                    beneficiary_id=cursor1.getString(cursor1.getColumnIndex("beneficiary_ids"));
                    if(beneficiary_id.isEmpty()){
                        beneficiary_id="0";
                    }
                    uuid=cursor1.getString(cursor1.getColumnIndex("uuid"));
                    server_primary_key=String.valueOf(cursor1.getInt(cursor1.getColumnIndex("server_primary_key")));
                    Logger.logD("C", "ClusterKey : ---------" + clusterKey + "--" + cursor1.getString(cursor1.getColumnIndex(columns[23])));

                    if (gpsTracker == null) {
                        gpsTracker = "";
                    }
                    if (reason == null) {
                        reason = "";
                    }
                    if ("0.0".equals(versionNumber)) {
                        versionNumber = "1.0";
                    }
                    autoSyncCreatedOn = getDate();
                } while (cursor1.moveToNext());
                cursor1.close();
            }
        }


        /**
         * @param deviceId
         * @param surveyList
         * @param sectionId
         * @param array
         * @return
         */
        public MultipartBody.Builder setPostParameters(String deviceId, String surveyList, String sectionId, JSONObject array) {

            MultipartBody.Builder builderMultiPart = new MultipartBody.Builder().setType(MultipartBody.FORM);
            Logger.logV(POST_PARAMETERS_VALUE, "la" + String.valueOf(latitude));
            Logger.logV(POST_PARAMETERS_VALUE, "lo" + String.valueOf(longitude));
            Logger.logV(POST_PARAMETERS_VALUE, "sample_id" + String.valueOf(sampleId));
            Logger.logV(POST_PARAMETERS_VALUE, "uId" + String.valueOf(hrId));
            Logger.logV(POST_PARAMETERS_VALUE, "la" + String.valueOf(latitude));
            Logger.logV(POST_PARAMETERS_VALUE, "sd" + startDate);
            Logger.logV(POST_PARAMETERS_VALUE, "ed" + endDate);
            Logger.logV(POST_PARAMETERS_VALUE, "vn" + versionNumber);
            Logger.logV(POST_PARAMETERS_VALUE, "av" + latestAppVersion);
            Logger.logV(POST_PARAMETERS_VALUE, "l_id" + String.valueOf(languageId));
            Logger.logV(POST_PARAMETERS_VALUE, "imei" + deviceId);
            Logger.logV(POST_PARAMETERS_VALUE, "survey_id" + String.valueOf(autoSyncSurveyKey));
            Logger.logV(POST_PARAMETERS_VALUE, "mode" + online);
            Logger.logV(POST_PARAMETERS_VALUE, "part2_charge" + "");
            Logger.logV(POST_PARAMETERS_VALUE, GPS_TRACKER + gpsTracker);
            Logger.logV(POST_PARAMETERS_VALUE, "created_on" + autoSyncCreatedOn);
            Logger.logV(POST_PARAMETERS_VALUE, "sp_s_o" + autoSyncCreatedOn);
            Logger.logV(POST_PARAMETERS_VALUE, REASON_KEY + reason);
            Logger.logV(POST_PARAMETERS_VALUE, "beneficiary_id" + beneficiary_id);

            Logger.logV(POST_PARAMETERS_VALUE, benTypeIdStr + beneficiaryTypeId );
            Logger.logV(POST_PARAMETERS_VALUE, FACILITY_TYPE_ID + facilityTypeId );
            Logger.logV(POST_PARAMETERS_VALUE, "facility_id" + facility_id);
            Logger.logV(POST_PARAMETERS_VALUE, "uuid" + uuid);
            Logger.logV(POST_PARAMETERS_VALUE, CLUSTER_ID + clusterForSync);
            Logger.logV(POST_PARAMETERS_VALUE, "answers_array" + String.valueOf(array));
            Logger.logV(POST_PARAMETERS_VALUE, "OperatorDetails" + surveyList);
            Logger.logV(POST_PARAMETERS_VALUE, "is_cus_rom" + autoSyncPreferences.getString(PLAY_STORE, ""));
            Logger.logV(POST_PARAMETERS_VALUE, "pe_r" +paperEntryReason);
            Logger.logV(POST_PARAMETERS_VALUE, "lqc" + lastQcode);
            Logger.logV(POST_PARAMETERS_VALUE, "survey_part" + sectionId);
            Logger.logV(POST_PARAMETERS_VALUE, "t_id" + typologyCode);
            Logger.logV(POST_PARAMETERS_VALUE, "clusterKey" + clusterKey);
            Logger.logV(POST_PARAMETERS_VALUE, "clusterMember" + clusterMember);
            Logger.logV(POST_PARAMETERS_VALUE, "captured_date" + captureDate);

            // Adding post parameter
            builderMultiPart.addFormDataPart("la", String.valueOf(latitude));
            builderMultiPart.addFormDataPart("lo", String.valueOf(longitude));
            builderMultiPart.addFormDataPart("sample_id", String.valueOf(sampleId));
            builderMultiPart.addFormDataPart("uId", String.valueOf(hrId));
            builderMultiPart.addFormDataPart("la", String.valueOf(latitude));
            builderMultiPart.addFormDataPart("sd", startDate);
            builderMultiPart.addFormDataPart("ed", endDate);
            builderMultiPart.addFormDataPart("vn", versionNumber);
            builderMultiPart.addFormDataPart("av", latestAppVersion);
            builderMultiPart.addFormDataPart("l_id", String.valueOf(languageId));
            builderMultiPart.addFormDataPart("imei", deviceId);
            builderMultiPart.addFormDataPart("survey_id", String.valueOf(autoSyncSurveyKey));
            builderMultiPart.addFormDataPart("mode", online);
            builderMultiPart.addFormDataPart("part2_charge", "");
            builderMultiPart.addFormDataPart(GPS_TRACKER, gpsTracker);
            builderMultiPart.addFormDataPart("created_on", autoSyncCreatedOn);
            builderMultiPart.addFormDataPart("sp_s_o", autoSyncCreatedOn);
            builderMultiPart.addFormDataPart(REASON_KEY, reason);
            builderMultiPart.addFormDataPart("beneficiary_id",beneficiary_id);
            builderMultiPart.addFormDataPart(benTypeIdStr,beneficiaryTypeId);
            builderMultiPart.addFormDataPart(FACILITY_TYPE_ID,facilityTypeId);
            builderMultiPart.addFormDataPart("facility_id",facility_id);
            builderMultiPart.addFormDataPart("uuid",uuid);
            builderMultiPart.addFormDataPart(REASON_KEY, reason);
            builderMultiPart.addFormDataPart(CLUSTER_ID, clusterForSync);
            builderMultiPart.addFormDataPart("answers_array", String.valueOf(array));
            builderMultiPart.addFormDataPart("OperatorDetails", surveyList);
            builderMultiPart.addFormDataPart("is_cus_rom", autoSyncPreferences.getString(PLAY_STORE, ""));
            builderMultiPart.addFormDataPart("pe_r", paperEntryReason);
            //lqc last question code
            builderMultiPart.addFormDataPart("lqc", lastQcode);
            builderMultiPart.addFormDataPart("survey_part", sectionId);
            builderMultiPart.addFormDataPart("t_id", typologyCode);
            builderMultiPart.addFormDataPart("clusterKey", clusterKey);
            builderMultiPart.addFormDataPart("clustername", clusterMember);
            builderMultiPart.addFormDataPart("captured_date","");

            // Is it a force sync of regular submission
            if(fromValue == 3)
                fromValue = 1;
            else
                fromValue = 0;
            Logger.logV(POST_PARAMETERS_VALUE, "f_sy" + fromValue);
            builderMultiPart.addFormDataPart("f_sy", String.valueOf(fromValue));

            // changing integer flag to text for api purpose
            String completionStatus;
            if (surveyStatus == 1)
                completionStatus = "yes";
            else
                completionStatus = "no";
            Logger.logV(POST_PARAMETERS_VALUE, surveyStatusStr + completionStatus);
            builderMultiPart.addFormDataPart(surveyStatusStr, completionStatus);

            // Editing or inserting purpose
            if (server_primary_key!=null && !server_primary_key.equals("0")) {
                builderMultiPart.addFormDataPart(RESPONSE_ID_KEY, server_primary_key);
                Logger.logV(RESPONSE_ID_KEY, "response_id" + server_primary_key);
            }else{
                builderMultiPart.addFormDataPart(RESPONSE_ID_KEY, "");
                Logger.logV(RESPONSE_ID_KEY, "response_id" + "");
            }

            // adding images to builder in a for loop base
            JSONArray imageArray = new JSONArray();
            for (int i = 0; i < imageQIdList.size(); i++) {
                try {
                    File imageFile = new File(imageCapturedFilePathList.get(i));
                    if (imageFile.exists()) {

                        RequestBody body = RequestBody.create(MediaType.parse("image/*"), imageFile);
                        //  Parameters are ，  request key ， File name  ， RequestBody
                        builderMultiPart.addFormDataPart(String.valueOf(imageQIdList.get(i)), imageFile.getName(), body);
                        JSONObject json = new JSONObject();
                        json.put("qid", String.valueOf(imageQIdList.get(i)));
                        json.put("time", imageCapturedTimeList.get(i));
                        Logger.logV(POST_PARAMETERS_VALUE, "time" +  imageCapturedTimeList.get(i));
                        Logger.logV(POST_PARAMETERS_VALUE, "String.valueOf(imageQIdList.get(i)) : " + imageQIdList.get(i));
                        imageArray.put(json);
                    }
                } catch (Exception e) {
                    Logger.logE(AutoSyncActivity.class.getSimpleName(), exceptionStr, e);
                }
            }
            builderMultiPart.addFormDataPart("image_info", imageArray.toString());
            Logger.logV(POST_PARAMETERS_VALUE, "image_info" + imageArray.toString());

            // is sdcard present
            boolean sdcard = Utilities.isSdPresent();
            int sdcardValue = 0;
            if (sdcard) {
                sdcardValue = 1;
            }
            builderMultiPart.addFormDataPart("sdc", String.valueOf(sdcardValue));
            Logger.logV(POST_PARAMETERS_VALUE, "sdc" + String.valueOf(sdcardValue));

            // Getting stocken from which is sent from server side
            String sToken = autoSyncPreferences.getString(String.valueOf(hrId), "");
            if ("".equals(sToken)) {
                sToken = autoSyncPreferences.getString("s_token", "");
            }
            String concatenateAll = String.valueOf(latitude).concat(String.valueOf(longitude))
                    .concat(sampleId).concat(String.valueOf(hrId))
                    .concat(String.valueOf(startDate)).concat(String.valueOf(endDate))
                    .concat(String.valueOf(versionNumber)).concat(latestAppVersion)
                    .concat(String.valueOf(languageId)).concat(deviceId)
                    .concat(String.valueOf(autoSyncSurveyKey))
                    .concat(online).concat(part2Charge).concat(String.valueOf(fromValue))
                    .concat(gpsTracker).concat(bloodCollected).concat(completionStatus)
                    .concat(String.valueOf(autoSyncCreatedOn)).concat(String.valueOf(autoSyncCreatedOn))
                    .concat(reason).concat(clusterForSync).concat(String.valueOf(array))
                    .concat(surveyList).concat(autoSyncPreferences.getString(PLAY_STORE, ""))
                    .concat(paperEntryReason).concat(offSurveyReason).concat(lastQcode)
                    .concat(String.valueOf(sdcardValue)).concat(domainId).concat(sectionId)
                    .concat(consentStatus).concat(sToken);

            // MD5 based stocken generation as security purpose
            builderMultiPart.addFormDataPart("sToken", autoSyncResturl.md5(concatenateAll));

            return builderMultiPart;
        }
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());
    }

}
