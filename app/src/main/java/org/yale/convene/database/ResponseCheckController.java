package org.yale.convene.database;

import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.yale.convene.utils.Logger;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */

public class ResponseCheckController {

    public final String TAG = "ResponseCheckController";
    private DBHandler dbHandler;
    private Context context;
    private SQLiteDatabase db;

    public ResponseCheckController(Context context){
        this.context = context;
        dbHandler = new DBHandler(context);
        db = dbHandler.getdatabaseinstance_read();

    }

    /**
     * @param surveyID
     * @param uuid

     * @return
     */
    public int getPauseSurvey(String surveyID, String uuid) {
        int getServerPrimaryKey = -1;
        try {
            String pendingSurveyQuery = "SELECT id,server_primary_key FROM Survey where survey_status=0 and survey_ids='" + surveyID + "' and uuid='" + uuid + "'";
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD("pendingSurveyQuery", " getPauseSurvey" + pendingSurveyQuery + "-->" + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    getServerPrimaryKey = cursor.getInt(cursor.getColumnIndex("id"));
                    Logger.logD("pendingSurveyQuery", " getServerPrimaryKey" + getServerPrimaryKey);

                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            Logger.logV("", "getPauseSurvey from Survey table" + e);
        }
        return getServerPrimaryKey;
    }

   /* *//**
     * @param id
     * @param dbOpenHelper
     * @return
     *//*
    public StatusBean getPauseCompletedRecords(int id, ExternalDbOpenHelper dbOpenHelper) {
        StatusBean syncedList = new StatusBean();
            String pendingSurveyQuery = "SELECT id,capture_date,survey_ids FROM Survey where survey_ids="+ id ;
            Cursor cursor = db.rawQuery(pendingSurveyQuery, null);
            Logger.logD(TAG, " getPauseCompletedRecords" + pendingSurveyQuery + "-->" + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    String getEndDate = cursor.getString(cursor.getColumnIndex("capture_date"));
                    StatusBean = dbOpenHelper.getDetails(getEndDate, id);

                } while (cursor.moveToNext());
                cursor.close();
            }

        return syncedList;
    }*/

}
