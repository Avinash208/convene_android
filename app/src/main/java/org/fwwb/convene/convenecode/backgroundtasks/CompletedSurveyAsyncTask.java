package org.fwwb.convene.convenecode.backgroundtasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.fwwb.convene.convenecode.BeenClass.SurveysBean;
import org.fwwb.convene.convenecode.backgroundcallbacks.PendingCompletedSurveyAsyncResultListener;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.database.ResponseCheckController;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// The types specify: 1. input data type (String)
// 2. progress type (Integer)
// 3. result type (String)

public class CompletedSurveyAsyncTask extends AsyncTask<String, Integer, List<SurveysBean>> {
    private List<SurveysBean> sourceList;
    private List<SurveysBean> resultList = new ArrayList<>();
    private ExternalDbOpenHelper externalDbOpenHelper;
    private PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener;

    private ResponseCheckController responseCheckController;
    private DBHandler handler;
    private String surveyPrimaryKeyId;
    SharedPreferences preferences;

    protected void onPreExecute() {
        // Executed in UIThread
    }

    public CompletedSurveyAsyncTask(Context con, List<SurveysBean> sourceList, ExternalDbOpenHelper externalDbOpenHelper, SharedPreferences defaultPreferences,String surveyPrimaryKeyId,
                                    PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener, DBHandler handler) {
        this.sourceList = sourceList;
        this.responseCheckController = new ResponseCheckController(con);
        this.externalDbOpenHelper = externalDbOpenHelper;
        this.pendingCompletedSurveyAsyncResultListener = pendingCompletedSurveyAsyncResultListener;
        this.handler=handler;
        this.surveyPrimaryKeyId=surveyPrimaryKeyId;
        this.preferences=defaultPreferences;
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
        if (preferences.getString(Constants.PROJECTFLOW, "").equalsIgnoreCase("1")) {
            int getSeletedProjectActivity = preferences.getInt(Constants.SELECTEDPROJECTID, 0);
            Logger.logD("Seleced Project Activity", getSeletedProjectActivity + "");
            List<SurveysBean> sortedProjectActivity=removeAddedActivityBasedOnProject(result, getSeletedProjectActivity);
            pendingCompletedSurveyAsyncResultListener.completedSurveys(sortedProjectActivity);
        } else {
            pendingCompletedSurveyAsyncResultListener.completedSurveys(result);
        }
    }
    /**
     * @param result completed Activity list
     * @param getSeletedProjectActivity selected survey id from the project flow .
     * @return return sorted according to the selected project
     */
    private List<SurveysBean> removeAddedActivityBasedOnProject(List<SurveysBean> result, int getSeletedProjectActivity) {
        List<SurveysBean> sortedList= new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getId() == getSeletedProjectActivity) {
                sortedList.add(result.get(i));

            }
        }
        return sortedList;
    }


}
                                