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
           /* int getCount = externalDbOpenHelper.getPeriodicityPreviousCountOnline(sourceList.get(i).getId(), sourceList.get(i).getPeriodicityFlag(), sourceList.get(i).getBeneficiaryIds(), sourceList.get(i).getFacilityIds(), defaultPreferences.getString(Constants.UUID,""), new Date());
            int getSurveyCount = periodicityCheckControllerDbHelper.getPeriodicityPreviousCountOffline(String.valueOf(sourceList.get(i).getId()), sourceList.get(i).getPeriodicityFlag(), defaultPreferences.getString(Constants.UUID,""), new Date());*/
            int getCount =0;
            int getSurveyCount = 0;
            int addCount = getCount + getSurveyCount;
            if(sourceList.get(i).getPeriodicity()>addCount)
            {
                SurveysBean been = getSurveyPendingStuatus(sourceList.get(i));
                resultList.add(been);
            }
        }
        return resultList;
    }
    
        protected void onProgressUpdate(Integer... values) {
            // Used to update progress indicator
        }

        protected void onPostExecute(List<SurveysBean> result) {
            // Executed in UIThread
            List<SurveysBean> resultPendingTemp =handler.isSurveyCompleted(surveyPrimaryKeyId,externalDbOpenHelper);



           /* for (int i=0;i<result.size();i++){
               if (handler.isSurveyCompleted(result.get(i).getId(),surveyPrimaryKeyId)){
                   resultPendingTemp.add(result.get(i));
               }
           }
*/
            pendingCompletedSurveyAsyncResultListener.pendingSurveys(result);
        }

    private SurveysBean getSurveyPendingStuatus( SurveysBean surveysBean) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssaa", Locale.US);
        String startPeriod = periodicityCheckControllerDbHelper.getPeriodicityPeriod(surveysBean.getPeriodicityFlag(), defaultPreferences.getString(Constants.CREATED_DATE,""));
        String currentPeriod = periodicityCheckControllerDbHelper.getPeriodicityPeriod(surveysBean.getPeriodicityFlag(), dateFormat.format(new Date()));
//      surveyStatus 1: Pending* 2:Pending 3:Continue
        if (!startPeriod.equals(currentPeriod) && PeriodicityUtils.isEligibleForExtension(surveysBean.getPLimit(), surveysBean.getPeriodicityFlag())) {
            Date date = PeriodicityUtils.getPreviousPeriodicityTime(surveysBean.getPeriodicityFlag());
            int getCountPrev = externalDbOpenHelper.getPeriodicityPreviousCountOnline(surveysBean.getId(), surveysBean.getPeriodicityFlag(), surveysBean.getBeneficiaryIds(), surveysBean.getFacilityIds(), defaultPreferences.getString("uuid", ""), date);
            int getSurveyCountPrev = periodicityCheckControllerDbHelper.getPeriodicityPreviousCountOffline(String.valueOf(surveysBean.getId()), surveysBean.getPeriodicityFlag(), defaultPreferences.getString("uuid", ""), date);
            if (surveysBean.getPeriodicity() > (getCountPrev + getSurveyCountPrev))
                surveysBean.setSurveyStatus(1);
            else
                surveysBean.setSurveyStatus(2);

        }
        else
        {
            surveysBean.setSurveyStatus(2);
        }
         int pId=responseCheckController.getPauseSurvey(String.valueOf(surveysBean.getId()), defaultPreferences.getString("uuid",""));
        if (pId>=0 ) {
            surveysBean.setContinue(1);
            surveysBean.setSurveyPId(pId);
        }
        else
            surveysBean.setContinue(2);

        return surveysBean;
    }
    }
                                