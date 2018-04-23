package org.mahiti.convenemis.backgroundtasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.mahiti.convenemis.BeenClass.StatusBean;
import org.mahiti.convenemis.BeenClass.SurveysBean;
import org.mahiti.convenemis.backgroundcallbacks.PendingCompletedSurveyAsyncResultListener;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.ResponseCheckController;
import org.mahiti.convenemis.database.SurveyControllerDbHelper;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.PeriodicityUtils;

import java.util.ArrayList;
import java.util.List;

// The types specify: 1. input data type (String)
// 2. progress type (Integer)
// 3. result type (String)

public class CompletedSurveyAsyncTask extends AsyncTask<String, Integer, List<SurveysBean>> {
    private static String uuid;
    private final SurveyControllerDbHelper periodicityCheckControllerDbHelper;
    private List<SurveysBean> sourceList;
    private List<SurveysBean> resultList = new ArrayList<>();
    private ExternalDbOpenHelper externalDbOpenHelper;
    private PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener;

    private ResponseCheckController responseCheckController;

    protected void onPreExecute() {
        // Executed in UIThread
    }

    public CompletedSurveyAsyncTask(Context con, List<SurveysBean> sourceList, ExternalDbOpenHelper externalDbOpenHelper, SharedPreferences defaultPreferences, SurveyControllerDbHelper periodicityCheckControllerDbHelper, PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener) {
        this.sourceList = sourceList;
        this.responseCheckController = new ResponseCheckController(con);
        this.externalDbOpenHelper = externalDbOpenHelper;
        this.periodicityCheckControllerDbHelper = periodicityCheckControllerDbHelper;
        this.pendingCompletedSurveyAsyncResultListener = pendingCompletedSurveyAsyncResultListener;
        uuid=defaultPreferences.getString(Constants.UUID,"");
    }

    protected List<SurveysBean> doInBackground(String... strings) {
        //Need to move into single asyncTask
        for(int i=0;i<sourceList.size();i++) {
             int completedCount1=externalDbOpenHelper.getPeriodicityPreviousSurveyCount(String.valueOf(sourceList.get(i).getId()), sourceList.get(i).getBeneficiaryIds(),uuid);
             int completedCount2=periodicityCheckControllerDbHelper.getPeriodicityPreviousCompleted(String.valueOf(sourceList.get(i).getId()), sourceList.get(i).getBeneficiaryIds(),uuid);
            if (completedCount1 != 0 || completedCount2 != 0) {
                SurveysBean surveysBean = getSurveyViewStatus(sourceList.get(i));
                resultList.add(surveysBean);
            }
        }
        return resultList;
    }

    private SurveysBean getSurveyViewStatus(SurveysBean surveysBean) {
        String captureDate = "";
        List<StatusBean> getCompletedSurveyList= externalDbOpenHelper.getCompletedRecords(uuid,surveysBean.getId(),surveysBean.getBeneficiaryIds(),surveysBean.getFacilityIds());
        if (!getCompletedSurveyList.isEmpty()) {
            if (getCompletedSurveyList.get(0) != null)
                captureDate = getCompletedSurveyList.get(0).getDate();
            if (getCompletedSurveyList.get(0).getDate().isEmpty()) {
                List<StatusBean> getPausedCompletedSurveyList = responseCheckController.getPauseCompletedRecords(surveysBean.getId(), externalDbOpenHelper);
                if (getPausedCompletedSurveyList.get(0) != null)
                    captureDate = getPausedCompletedSurveyList.get(0).getDate();
            }
        }
        try {
            if (PeriodicityUtils.isCurrentPeriodicity(captureDate,surveysBean.getPeriodicityFlag()) )
            {
                surveysBean.setIsViewOrEdit(1);
            }
            else if (PeriodicityUtils.isBelongsToPreviousPeriodicity(surveysBean.getPeriodicityFlag(),captureDate) && PeriodicityUtils.isEligibleForExtension(surveysBean.getPLimit(),surveysBean.getPeriodicityFlag()))
            {
                surveysBean.setIsViewOrEdit(1);
            }
            else
            {
                surveysBean.setIsViewOrEdit(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveysBean;
    }



    protected void onProgressUpdate(Integer... values) {
        // Used to update progress indicator
    }

    protected void onPostExecute(List<SurveysBean> result) {
        // Executed in UIThread
        pendingCompletedSurveyAsyncResultListener.completedSurveys(result);
    }
}
                                