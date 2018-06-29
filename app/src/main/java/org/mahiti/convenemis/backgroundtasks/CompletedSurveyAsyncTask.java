package org.mahiti.convenemis.backgroundtasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.mahiti.convenemis.BeenClass.SurveysBean;
import org.mahiti.convenemis.backgroundcallbacks.PendingCompletedSurveyAsyncResultListener;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.ResponseCheckController;
import org.mahiti.convenemis.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// The types specify: 1. input data type (String)
// 2. progress type (Integer)
// 3. result type (String)

public class CompletedSurveyAsyncTask extends AsyncTask<String, Integer, List<SurveysBean>> {
    private static String uuid;
  //  private final SurveyControllerDbHelper periodicityCheckControllerDbHelper;
    private List<SurveysBean> sourceList;
    private List<SurveysBean> resultList = new ArrayList<>();
    private ExternalDbOpenHelper externalDbOpenHelper;
    private PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener;

    private ResponseCheckController responseCheckController;
    private DBHandler handler;
    private String surveyPrimaryKeyId;

    protected void onPreExecute() {
        // Executed in UIThread
    }

    public CompletedSurveyAsyncTask(Context con, List<SurveysBean> sourceList, ExternalDbOpenHelper externalDbOpenHelper, SharedPreferences defaultPreferences,String surveyPrimaryKeyId,
                                    PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener, DBHandler handler) {
        this.sourceList = sourceList;
        this.responseCheckController = new ResponseCheckController(con);
        this.externalDbOpenHelper = externalDbOpenHelper;
        this.pendingCompletedSurveyAsyncResultListener = pendingCompletedSurveyAsyncResultListener;
        uuid=defaultPreferences.getString(Constants.UUID,"");
        this.handler=handler;
        this.surveyPrimaryKeyId=surveyPrimaryKeyId;
    }

    protected List<SurveysBean> doInBackground(String... strings) {
        for(int i=0;i<sourceList.size();i++) {
            if (handler.isSurveyCompleted(sourceList.get(i),surveyPrimaryKeyId,externalDbOpenHelper)) {
                int getCount =handler.getPeriodicityPreviousCountOnline(sourceList.get(i),sourceList.get(i).getId(), sourceList.get(i).getPeriodicityFlag(), new Date(),surveyPrimaryKeyId);
                if (getCount==0) {
                    sourceList.get(i).setSurveyEndDate(sourceList.get(i).getSurveyEndDate());
                    sourceList.get(i).setSurveyDone(1);
                    resultList.add(sourceList.get(i));
                }else{
                    sourceList.get(i).setSurveyDone(0);
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
        // Executed in UIThread
        pendingCompletedSurveyAsyncResultListener.completedSurveys(result);
    }


}
                                