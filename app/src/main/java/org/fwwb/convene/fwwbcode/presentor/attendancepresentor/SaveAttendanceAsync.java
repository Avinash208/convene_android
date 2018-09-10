package org.fwwb.convene.fwwbcode.presentor.attendancepresentor;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.fwwb.convene.BuildConfig;
import org.fwwb.convene.convenecode.ListingActivity;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.StartSurvey;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.fwwb.convene.convenecode.utils.Constants.SURVEY_ID;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class SaveAttendanceAsync extends AsyncTask<String,Integer,String>{


    private static final String DATE_FORMAT_TEMP = "yyyy-MM-dd HH:mm:ss";
    private int surveyId;
    private String trainingUuid;
    private String surveyUuid;
    private String memberUuid;
    private int questionId;
    private int option;
    private SaveAttendanceListener saveAttendanceListener;
    private DBHandler dbHandler;
    private SharedPreferences preferences;
    private float charge;

    public SaveAttendanceAsync(JSONObject jsonObject, SaveAttendanceListener saveAttendanceListener, DBHandler dbHandler, SharedPreferences preferences, float charge)
    {
        this.dbHandler = dbHandler;
        this.preferences = preferences;
        this.charge = charge;

        try {
            this.surveyId = jsonObject.getInt("surveyId");
            this.trainingUuid = jsonObject.getString("trainingUuid");
            this.surveyUuid = jsonObject.getString("surveyUuid");
            this.memberUuid = jsonObject.getString("memberUuid");
            this.questionId = jsonObject.getInt("questionId");
            this.option = jsonObject.getInt("option");
        } catch (JSONException e) {
            Logger.logE("SaveAttendanceAsync",e.getMessage(),e);
        }
        this.saveAttendanceListener = saveAttendanceListener;
    }
    @Override
    protected String doInBackground(String... strings) {

        if (surveyUuid.isEmpty())
        {
            createAndSave();
        }
        else
        {
            updateSurvey();
        }
        return null;
    }

    private void updateSurvey() {

    }

    private void createAndSave() {


//        Map<String, String> values  =  new HashMap<>();
//        values.put("uuid", UUID.randomUUID().toString());
//        values.put(Constants.START_SURVEY_STATUS, "0");
//        values.put("inv_id",preferences.getString("uId", ""));
//        values.put("start_date",  new SimpleDateFormat(DATE_FORMAT_TEMP, Locale.ENGLISH).format(new Date()));
//        values.put(Constants.END_DATE, "0");
//        values.put("version_num", String.valueOf(BuildConfig.VERSION_CODE));
//        values.put("app_version", "");
//        values.put("language", String.valueOf(preferences.getInt(Constants.SELECTEDLANGUAGE, 1)));
//        values.put("lat", preferences.getString("LATITUDE", ""));
//        values.put("long", preferences.getString("LONGITUDE", ""));
//        values.put("survey_status", "0");
//        values.put("sync_status", "0");
//        values.put("sync_date", "0");
//        values.put("mode_status", "");
//        values.put("specimen_id", String.valueOf(StartSurvey.getUtcTimeInMillis(new SimpleDateFormat(DATE_FORMAT_TEMP, Locale.ENGLISH).format(new Date()),DATE_FORMAT_TEMP)));
//        values.put("survey_key", "0");
//        values.put("reason", "");
//        values.put("paper_entry_reason", "");
//        values.put("reason_off_survey", "2");
//        values.put("last_qcode", "");
//        values.put("survey_status2", "0");
//        values.put("survey_status1", "0");
//        values.put("domain_id", "");
//        values.put("p1_charge", String.valueOf(charge));
//        values.put("gps_tracker", String.valueOf(gpsTracker.gps_tracker));
//        values.put("consent_status", "");
//        values.put("server_primary_key", responsePrimaryID);
//        values.put("level1", "");
//        values.put("level2", "");
//        values.put("level3", "");
//        values.put("level4", "");
//        values.put("level5", "");
//        values.put("level6", "");
//        values.put("level7", "");
//        values.put(Constants.SPECIMEN_ID, queryValues.get(Constants.SPECIMEN_ID));
//        values.put(Constants.SURVEY_KEY, queryValues.get(Constants.SURVEY_KEY));
//        values.put("paper_entry_reason", queryValues.get("paper_entry_reason"));
//        values.put("last_qcode", queryValues.get("last_qcode"));
//        values.put("gps_tracker", queryValues.get("gps_tracker"));
//        values.put("typology_code", queryValues.get(TYPOLOGYCODE));
//        values.put("cluster_id", queryValues.get("cluster_id"));
//        values.put("clustername", queryValues.get("clustername"));
//        values.put("clusterkey", queryValues.get("clusterkey"));
//        values.put("beneficiary_details", queryValues.get("beneficiary_details"));
//
//        values.put("beneficiary_ids", queryValues.get(BEN_ID_STR));
//        values.put("facility_ids", queryValues.get("facility_ids"));
//        values.put("capture_date", queryValues.get("captured_date"));
//        values.put("beneficiary_type_id", queryValues.get("beneficiary_type_id"));
//        values.put("facility_type_id", queryValues.get("facility_type_id"));
//        values.put("response_parent_uuid", queryValues.get("response_parent_uuid"));
//        values.put("training_survey_id", queryValues.get("training_survey_id"));

    }
}
