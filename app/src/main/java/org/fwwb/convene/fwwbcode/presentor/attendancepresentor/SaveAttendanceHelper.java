package org.fwwb.convene.fwwbcode.presentor.attendancepresentor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.fwwb.convene.convenecode.BeenClass.Response;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.utils.PreferenceConstants;
import org.fwwb.convene.convenecode.utils.StartSurvey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static org.fwwb.convene.convenecode.utils.Constants.SURVEY_ID;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class SaveAttendanceHelper implements SaveAttendanceListener {

    private Activity activity;
    private int surveyId;
    private int questionId;
    private int optionId;
    private DBHandler handler;
    private SaveAttendanceListener saveAttendanceListener;

    public SaveAttendanceHelper(Activity activity, int surveyId,int questionId,SaveAttendanceListener saveAttendanceListener)
    {
        this.activity = activity;
        this.surveyId = surveyId;
        this.questionId = questionId;
        this.handler = new DBHandler(activity);
        this.saveAttendanceListener = saveAttendanceListener;
    }

    public void callSurvey(String memberUuid, String surveyUuid, String trainingUuid, int optionId, String batchUuid){
        this.optionId = optionId;
        SharedPreferences prefs;
        prefs = activity.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SURVEY_ID, surveyId);
        editor.apply();
        if (surveyUuid.isEmpty())
            new StartSurvey(activity, activity, prefs.getInt(SURVEY_ID, 0), prefs.getInt(SURVEY_ID, 0), "", memberUuid, "", surveyUuid, "",this,trainingUuid,batchUuid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else {

            savedAttendanceSurvey(true, memberUuid, surveyUuid);
        }
    }

    @Override
    public void savedAttendance(boolean isSaved, String memberUuid, String surveyUuid) {

    }

    @Override
    public void savedAttendanceSurvey(boolean isSaved, String memberUuid, String surveyUuid) {
       if (isSaved)
       {

           Response response = new Response(String.valueOf(questionId), "", String.valueOf(optionId), "", questionId, 0, String.valueOf(surveyId), 0, optionId, "R");
           saveResponce(response,surveyUuid,memberUuid);
       }
       else
       {
        saveAttendanceListener.savedAttendanceSurvey(false,null,null);
       }
    }

    private void saveResponce(Response response, String surveyUuid, String memberUuid) {
        handler.deleteExistingSurveyRecord(surveyUuid,1);
        HashMap<String, String> checkboxInsert = new HashMap<>();
        checkboxInsert.put(PreferenceConstants.SURVEY_ID_HASH, surveyUuid);
        checkboxInsert.put("q_id", response.getQ_id());
        checkboxInsert.put("ans_code", response.getAns_code());
        checkboxInsert.put(PreferenceConstants.ANS_TEXT, response.getAnswer());
        checkboxInsert.put("pre_question", "");
        checkboxInsert.put("next_question", "");
        checkboxInsert.put("answered_on", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
        checkboxInsert.put("sub_questionId", response.getSub_questionId());
        checkboxInsert.put("q_code", String.valueOf(response.getQcode()));
        checkboxInsert.put("primarykey", String.valueOf(response.getPrimarykey()));
        checkboxInsert.put("typologyId", String.valueOf(response.getTypologyId()));
        checkboxInsert.put("group_id", String.valueOf(response.getGroup_id()));
        checkboxInsert.put("primaryID", String.valueOf(response.getPrimaryID()));
        checkboxInsert.put("qtype", String.valueOf(response.getQ_type()));
        if(response.getGroup_id()!=0 || "0".equalsIgnoreCase(response.getSub_questionId()))
            checkboxInsert.put("response_dump_pid", String.valueOf(surveyUuid));
         handler.insertResponseDataToDB(checkboxInsert);
         handler.updateEndSurveyStatusDataToDB(surveyUuid);
        saveAttendanceListener.savedAttendanceSurvey(true,memberUuid,surveyUuid);
    }
}
