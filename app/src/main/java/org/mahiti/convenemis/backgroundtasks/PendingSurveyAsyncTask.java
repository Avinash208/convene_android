package org.mahiti.convenemis.backgroundtasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.mahiti.convenemis.BeenClass.SurveysBean;
import org.mahiti.convenemis.backgroundcallbacks.PendingCompletedSurveyAsyncResultListener;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.ResponseCheckController;
import org.mahiti.convenemis.database.SurveyControllerDbHelper;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.PeriodicityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// The types specify: 1. input data type (String)
// 2. progress type (Integer)
// 3. result type (String)

public class PendingSurveyAsyncTask extends AsyncTask<String, Integer, List<SurveysBean>> {
    private List<SurveysBean> sourceList;
    private List<SurveysBean> resultList = new ArrayList<>();
    private ExternalDbOpenHelper externalDbOpenHelper;
    private SharedPreferences defaultPreferences;
    private SurveyControllerDbHelper periodicityCheckControllerDbHelper;
    private PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener;
    private ResponseCheckController responseCheckController;
    private DBHandler handler;
    String surveyPrimaryKeyId;
    protected void onPreExecute() {
        // Executed in UIThread
    }

    public PendingSurveyAsyncTask(Context con, List<SurveysBean> sourceList, ExternalDbOpenHelper externalDbOpenHelper, SharedPreferences defaultPreferences, SurveyControllerDbHelper periodicityCheckControllerDbHelper,
                                  PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener, DBHandler handler, String surveyPrimaryKeyId) {
        this.sourceList = sourceList;
        this.responseCheckController = new ResponseCheckController(con);
        this.externalDbOpenHelper = externalDbOpenHelper;
        this.defaultPreferences = defaultPreferences;
        this.periodicityCheckControllerDbHelper = periodicityCheckControllerDbHelper;
        this.pendingCompletedSurveyAsyncResultListener = pendingCompletedSurveyAsyncResultListener;
        this.handler=handler;
        this.surveyPrimaryKeyId=surveyPrimaryKeyId;
    }

    protected List<SurveysBean> doInBackground(String... strings) {
        for(int i=0;i<sourceList.size();i++) {

            int getSurveyCount = 0;
            int addCount =0;
            if(sourceList.get(i).getPeriodicity()>addCount)
            {
                if (!handler.isSurveyCompleted(sourceList.get(i),surveyPrimaryKeyId,externalDbOpenHelper)){
                    int getCount =externalDbOpenHelper.getPeriodicityPreviousCountOnline(sourceList.get(i).getId(), sourceList.get(i).getPeriodicityFlag(), new Date());
                    resultList.add(sourceList.get(i));
                }

            }
        }
        return resultList;
    }
    
        protected void onProgressUpdate(Integer... values) {
            // Used to update progress indicator
        }

        protected void onPostExecute(List<SurveysBean> result) {
            pendingCompletedSurveyAsyncResultListener.pendingSurveys(result);
        }
    }
                                