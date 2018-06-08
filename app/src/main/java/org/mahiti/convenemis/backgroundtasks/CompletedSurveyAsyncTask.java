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
            SurveysBean getCompletedList = getCompletedSurveyViewStatus(sourceList.get(i),surveyPrimaryKeyId);
           if (getCompletedList.getSurveyName()!=null)
                resultList.add(getCompletedList);
        }
        return resultList;
    }

    private SurveysBean getCompletedSurveyViewStatus(SurveysBean surveysBean, String surveyPrimaryKeyId) {
        return handler.getPauseCompletedRecords(surveysBean.getId(),externalDbOpenHelper,surveyPrimaryKeyId);
    }
    protected void onProgressUpdate(Integer... values) {
        // Used to update progress indicator
    }

    protected void onPostExecute(List<SurveysBean> result) {
        // Executed in UIThread
       /* List<SurveysBean> tempUuiidList= new ArrayList<>();

        for (int k=0;k<result.size();k++){
            if (!handler.isSurveyCompeted(result.get(k).getUuid())){
                tempUuiidList.add(result.get(k));
            }
        }*/

        pendingCompletedSurveyAsyncResultListener.completedSurveys(result);
    }


}
                                