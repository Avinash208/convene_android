package org.fwwb.convene.fwwbcode.presentor.taskpresentor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import net.sqlcipher.database.SQLiteDatabase;

import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class TaskListHelper {


    private final Activity activity;
    private final TaskListListeners listListeners;
    private final SharedPreferences sharedPreferencesDefault;

    private DBHandler dbHandler;
    private ConveneDatabaseHelper conveneDatabaseHelper;
    private ExternalDbOpenHelper externalDbOpenHelper;
    private List<TaskItemBean> currentList = new ArrayList<>();
    private List<TaskItemBean> totalTaskList = new ArrayList<>();
    private List<TaskItemBean> upcomingList = new ArrayList<>();
    private List<TaskItemBean> recentList = new ArrayList<>();

    public TaskListHelper(Activity activity, TaskListListeners listListeners)
    {
        this.activity = activity;
        this.listListeners = listListeners;
        dbHandler = new DBHandler(activity);
        sharedPreferencesDefault = PreferenceManager.getDefaultSharedPreferences(activity);
        conveneDatabaseHelper = ConveneDatabaseHelper.getInstance(activity, sharedPreferencesDefault.getString(Constants.CONVENE_DB,""), sharedPreferencesDefault.getString("UID",""));
        externalDbOpenHelper = ExternalDbOpenHelper.getInstance(activity,sharedPreferencesDefault.getString(Constants.DBNAME, ""), sharedPreferencesDefault.getString("inv_id", ""));
        String summurryQid = externalDbOpenHelper.getSummaryQid(188,externalDbOpenHelper);
        getTaskSurveys(188);
        getTaskList(summurryQid);
        String nameQids = getNameSurvey(conveneDatabaseHelper);
        getCompleteList(nameQids);
        Logger.logD("TaskListHelper", "size " + totalTaskList.size());


    }

    private void getCompleteList(String nameQids) {
        for (int i = 0; i < totalTaskList.size(); i++) {
            TaskItemBean itemBean = totalTaskList.get(i);
            try {
                if (itemBean.getTrainingUuid() != null) {
                    String name = getNameFromResponce(itemBean.getTrainingUuid(), nameQids,0,dbHandler);
                    totalTaskList.get(i).setTrainingName(name);
                }
                if (itemBean.getBatchUuid() != null) {
                    String name = getNameFromResponce(itemBean.getBatchUuid(), nameQids, 0, dbHandler);
                    String benTpe = getNameFromResponce(itemBean.getBatchUuid(), nameQids, 1, dbHandler);
                    totalTaskList.get(i).setBatchName(name);
                    if (benTpe != null && !benTpe.isEmpty())
                        totalTaskList.get(i).setBeneficiaryType(Integer.parseInt(benTpe));
                    int participants = getParticipants(itemBean);
                    totalTaskList.get(i).setBatchParticipants(participants);
                }

                if (totalTaskList.get(i).getTrainingDate() != null && !totalTaskList.get(i).getTrainingDate().isEmpty())
                {
                    String dateStr = totalTaskList.get(i).getTrainingDate();
                    totalTaskList.get(i).setTrainingDate(dateStr.replace("/","-"));

                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    Date date;

                        date = format.parse(totalTaskList.get(i).getTrainingDate());

                    Calendar calendar = Calendar.getInstance();
                    String monthNumber  = (String) DateFormat.format("MM",   date);
                    String yearNumber  = (String) DateFormat.format("yyyy",   date);
                    String monthNumber2  = (String) DateFormat.format("MM",   calendar.getTime());
                    String yearNumber2  = (String) DateFormat.format("yyyy",   calendar.getTime());
                    if (Integer.parseInt(monthNumber) == Integer.parseInt(monthNumber2) &&(Integer.parseInt(yearNumber)==Integer.parseInt(yearNumber2)))
                        currentList.add(totalTaskList.get(i));
                    if (Integer.parseInt(monthNumber) > Integer.parseInt(monthNumber2)|| (Integer.parseInt(yearNumber)>Integer.parseInt(yearNumber2)) )
                        upcomingList.add(totalTaskList.get(i));
                    if (Integer.parseInt(monthNumber) < Integer.parseInt(monthNumber2) || (Integer.parseInt(yearNumber)<Integer.parseInt(yearNumber2)))
                        recentList.add(totalTaskList.get(i));
                }




            } catch (Exception e) {
                Logger.logE("getCompleteList", e.getMessage(), e);
            }

        }
        listListeners.currentTaskList(currentList);
        listListeners.upcomingTaskList(upcomingList);
        listListeners.recentTaskList(recentList);
    }

    private int getParticipants(TaskItemBean itemBean) {

        return dbHandler.getChildDetailsFromBeneficiaryLinkage(itemBean.getBeneficiaryType(),itemBean.getBatchUuid()).size();
    }

    public static String getNameFromResponce(String uuid, String nameQids, int i, DBHandler dbHandler) {
        String answer = "";
        String  query;
        query = "Select ans_text from Response where survey_id='"+uuid+"' and q_id In ("+nameQids+")";
        if (i==1)
            query = "Select typology_code from Response where survey_id='"+uuid+"' and q_id In ("+nameQids+")";
        try {
            SQLiteDatabase db = dbHandler.getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {

                    if (i==1)
                        answer = cursor.getString(cursor.getColumnIndex("typology_code"));
                    else
                        answer = cursor.getString(cursor.getColumnIndex("ans_text"));
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            Logger.logV("", "getPauseSurvey from Survey table" + e);
        }

        return answer;
    }

    public static String getNameSurvey(ConveneDatabaseHelper conveneDatabaseHelper) {
        String nameQids =  "";
        String query = "Select group_concat(id,\",\") as surveyName from Question where display_as_name='true'";
        try {
            android.database.sqlite.SQLiteDatabase conveneDatabase = conveneDatabaseHelper.openDataBase();
            Cursor cursor = conveneDatabase.rawQuery(query, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    nameQids = cursor.getString(cursor.getColumnIndex("surveyName"));
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            Logger.logV("", "getPauseSurvey from Survey table" + e);
        }
        return nameQids;
    }

    private void getTaskSurveys(int surveyId) {

        String query = "Select uuid,survey_status from Survey where survey_status != 0 and survey_ids='"+surveyId+"'";
        try {
            SQLiteDatabase db = dbHandler.getdatabaseinstance_read();
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    TaskItemBean taskItemBean = new TaskItemBean();
                    String survey_status = cursor.getString(cursor.getColumnIndex("survey_status"));
                    taskItemBean.setTaskUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                    totalTaskList.add(taskItemBean);
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            Logger.logV("", "getPauseSurvey from Survey table" + e);
        }
    }

    private void getTaskList(String questionIds)//1260,1267,1268,1277,1280
    {
        if (questionIds == null || questionIds.isEmpty())
            return;
        if (totalTaskList.isEmpty())
            return;

        String coatedQids = getCoatedId(questionIds);
        String[] questionArray = questionIds.split(",");


        for (int i = 0; i < totalTaskList.size(); i++) {
            TaskItemBean itemBean = totalTaskList.get(i);
            try {
                if (itemBean.getTaskUuid() == null)
                    continue;
                String pendingSurveyQuery = "SELECT ans_text,sub_questionId,q_id,ans_code FROM response where survey_id='" + itemBean.getTaskUuid() + "'";
                SQLiteDatabase db = dbHandler.getdatabaseinstance_read();
                Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
                if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                    do {
                        String qId = cursor.getString(cursor.getColumnIndex("q_id"));
                        String answer = cursor.getString(cursor.getColumnIndex("ans_text"));
                        String answerCode;
                        answerCode = cursor.getString(cursor.getColumnIndex("ans_code"));
                        Logger.logD("getTaskList", "answerCode " + answerCode);
                        Logger.logD("getTaskList", "answer " + answer);
                        if (qId.equals(questionArray[0]) && qId.equals(questionArray[0])) {
                            totalTaskList.get(i).setBatchUuid(answer);
                        } else if (questionArray.length > 1 && qId.equals(questionArray[1])) {
                            totalTaskList.get(i).setTrainingUuid(answer);
                        } else if (questionArray.length > 2 && qId.equals(questionArray[2])) {
                            totalTaskList.get(i).setTrainingDate(answer);
                        } else if (questionArray.length > 3 && qId.equals(questionArray[3])) {
                            if (answer != null && !answer.isEmpty())
                                totalTaskList.get(i).setTrainingHour(Integer.parseInt(answer));
                        } else if (questionArray.length > 4 && qId.equals(questionArray[4])) {
                            totalTaskList.get(i).setTrainingStatus(Integer.parseInt(answer));
                        }


                    } while (cursor.moveToNext());

                }
                cursor.close();
            } catch (Exception e) {
                Logger.logV("", "getPauseSurvey from Survey table" + e);
            }
        }


    }

    private String getCoatedId(String questionIds) {
        StringBuilder coatedString = new StringBuilder();

        for (String item:questionIds.split(","))
        {
            if (coatedString.length() == 0)
                coatedString = new StringBuilder("'" + item + "'");
            else
                coatedString.append(",'").append(item).append("'");
        }

        return coatedString.toString();
    }
}
