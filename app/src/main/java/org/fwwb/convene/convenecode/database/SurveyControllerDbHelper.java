package org.fwwb.convene.convenecode.database;

import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.fwwb.convene.convenecode.BeenClass.parentChild.LocationSurveyBeen;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.PeriodicityUtils;
import org.fwwb.convene.convenecode.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */

public class SurveyControllerDbHelper {
    public final String TAG = "SurveyControllerDbHelper";
    private static final String OR_STRFTIME = "' OR strftime('%Y %m', date(capture_date))='";
    private static final String START_DATE_KEY = "capture_date";

    private static final String AND_SURVEY_IDS = "' and survey_ids='";
    private static final String SURVEY_IDS_KEY = "survey_ids";
    private static final String selectionQuery = "SELECT * FROM Survey WHERE  ( strftime(";
    private SQLiteDatabase db;
    private String captureQuery = "'%Y %m', date(capture_date))='";

    public SurveyControllerDbHelper(Context context){
        db = new DBHandler(context).getdatabaseinstance_read();
    }

    /**
     * @param UID
     * @param PeriodicityFlag
     * @param uuid
     * @return
     */
    public int getPeriodicityPreviousCountOffline(String UID, String PeriodicityFlag, String uuid, Date date) {
        int getPeriodicityCount = 0;

        try {
            String query = "";
            String getCurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date);
            String[] splitMonth = getCurrentDate.split("-");
            if ("Monthly".equalsIgnoreCase(PeriodicityFlag)) {
                query = selectionQuery+captureQuery+splitMonth[0] +" "+ splitMonth[1] + "') and uuid='" + uuid + "' and survey_status=1 and survey_ids='" + UID+"'";
            }
            else if ("Quarterly".equalsIgnoreCase(PeriodicityFlag)) {
                List<String> Quarterly = Utils.getQuarterlyMonth(splitMonth[1]);
                String surveyStatusId = "' and survey_status=1 and survey_ids='" + UID+"'";
                query = selectionQuery+captureQuery+ splitMonth[0]+" " + Quarterly.get(0) + OR_STRFTIME + Quarterly.get(1)+ splitMonth[0]+" " + OR_STRFTIME + Quarterly.get(2) + "') and uuid='" + uuid + surveyStatusId;
            } else if ("Half Yearly".equalsIgnoreCase(PeriodicityFlag)) {
                List<String> halfYearly = Utils.getHalfYearly(splitMonth[1]);
                query = selectionQuery+captureQuery+ splitMonth[0]+" " + halfYearly.get(0)+")" + OR_STRFTIME + splitMonth[0]+" "+ halfYearly.get(1) + OR_STRFTIME+ splitMonth[0]+" " + halfYearly.get(2) + OR_STRFTIME + splitMonth[0]+" "+ halfYearly.get(3) + OR_STRFTIME + splitMonth[0]+" "+ halfYearly.get(4) + OR_STRFTIME + splitMonth[0]+" "+ halfYearly.get(5) + "' ) and uuid='" + uuid + "' and survey_status=1 and survey_ids='" + UID+"'";

            } else if ("Yearly".equalsIgnoreCase(PeriodicityFlag)) {

                String previousDate = String.valueOf(Integer.parseInt(splitMonth[0])-1);
                String nextDate = String.valueOf(Integer.parseInt(splitMonth[0])+1);
                String logic = "SELECT * FROM Survey WHERE  ((( strftime('%Y', date(capture_date))='" + splitMonth[0] + "' ) and CAST(strftime('%m', date(capture_date)) AS INTEGER)>=4) or ((strftime('%Y', date(capture_date))='" + nextDate + "') and CAST(strftime('%m', date(capture_date)) AS INTEGER)<=3)";
                if (Integer.parseInt(splitMonth[1])<=3)
                    logic = "SELECT * FROM Survey WHERE  ((( strftime('%Y', date(capture_date))='" + splitMonth[0] + "' ) and CAST(strftime('%m', date(capture_date)) AS INTEGER)<=3) or ((strftime('%Y', date(capture_date))='" + previousDate + "' ) and CAST(strftime('%m', date(capture_date)) AS INTEGER)>=4)";
                query = logic+") and survey_status=1  and uuid='" + uuid + "' and survey_ids='" + UID+"'";
            }
            Cursor cursor = db.rawQuery(query, null);
            Logger.logV(TAG, "the query getPeriodicityPreviousCount is in  list is getPeriodicityPreviousCountOffline " + query + cursor.getCount());
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                getPeriodicityCount = cursor.getCount();
            }
            cursor.close();
        } catch (Exception e) {
            Logger.logE(TAG, "", e);
        }
        return getPeriodicityCount;
    }


    /**
     * @param UID
     * @param beneficiaryIDS
     * @param uuid
     * @return
     */
    public int getPeriodicityPreviousCompleted(String UID, String beneficiaryIDS, String uuid) {
        int getPeriodicityCount = 0;
        String query = "";


        query = "Select * from Survey where survey_status =1 and survey_ids="+ UID + " and uuid='" + uuid + "'";


        Cursor cursor = db.rawQuery(query, null);
        Logger.logV("PerodicityQuery"," : "+query+" count "+cursor.getCount());
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            getPeriodicityCount = cursor.getCount();
        }
        cursor.close();
        return getPeriodicityCount;
    }


    public String getPeriodicityPeriod(String periodicity,String modifydate)
    {
        Date formattedDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.US);
        if(modifydate.isEmpty())
            modifydate = dateFormat.format(new Date());
        try {
            formattedDate = dateFormat.parse(modifydate);
        } catch (Exception e) {
            Logger.logE(TAG,e.getMessage(),e);
        }
        SimpleDateFormat format = new SimpleDateFormat("MM",Locale.US);
        String month = format.format(formattedDate);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.US);
        String year = yearFormat.format(formattedDate);
        if ("Monthly".equalsIgnoreCase(periodicity))
        {
            return month+year;
        }
        if ("Quarterly".equalsIgnoreCase(periodicity))
        {
            return PeriodicityUtils.getQuaterly(Integer.parseInt(month))+year;
        }
        if ("Yearly".equalsIgnoreCase(periodicity))
            return year;
        return "";
    }

    public Map<Integer,LocationSurveyBeen> getLocationOfflineSurveyRecords(String clusterIds, String surveyId, Map<Integer, LocationSurveyBeen> locationListBean, String periodicity, int pLimit) {
        String query = "SELECT * FROM Survey WHERE survey_ids ="+surveyId+" and cluster_id IN ("+clusterIds+") and (survey_status=1 OR survey_status=0) GROUP BY cluster_id ORDER BY capture_date DESC";
        Cursor cursor = db.rawQuery(query, null);
        Logger.logV(TAG,"getLocationOnlineSurveyRecords query: "+query+" count "+cursor.getCount());
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                int id =cursor.getInt(cursor.getColumnIndex("cluster_id"));
                LocationSurveyBeen tempBean = locationListBean.get(id);
                tempBean.setIsOnline(2);
                tempBean.setCaptureDate(cursor.getString(cursor.getColumnIndex("capture_date")));
                if (cursor.getInt(cursor.getColumnIndex("survey_status")) != 0) {
                    if(PeriodicityUtils.isCurrentPeriodicity(tempBean.getCaptureDate(),periodicity)) {
                        tempBean.setIsEditable(1);
                        tempBean.setSurveyStatusFlag(-2);
                    }
                    else if(PeriodicityUtils.isBelongsToPreviousPeriodicity(periodicity, tempBean.getCaptureDate())) {
                        tempBean.setIsEditable(2);
                        tempBean.setSurveyStatusFlag(1);
                    }
                    else
                        tempBean.setSurveyStatusFlag(2);
                }
                if (cursor.getInt(cursor.getColumnIndex("survey_status")) == 0){
                    tempBean.setIsContinue(1);
                }
                else
                    tempBean.setIsContinue(2);

                tempBean.setResponseId(cursor.getInt(cursor.getColumnIndex("id")));
                locationListBean.put(id,tempBean);

            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return locationListBean;


    }



}
