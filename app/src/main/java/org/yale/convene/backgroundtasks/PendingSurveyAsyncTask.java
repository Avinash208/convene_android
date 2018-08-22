package org.yale.convene.backgroundtasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yale.convene.BeenClass.SurveysBean;
import org.yale.convene.backgroundcallbacks.PendingCompletedSurveyAsyncResultListener;
import org.yale.convene.database.DBHandler;
import org.yale.convene.database.DataBaseMapperClass;
import org.yale.convene.database.ExternalDbOpenHelper;
import org.yale.convene.database.SurveyControllerDbHelper;
import org.yale.convene.utils.Constants;
import org.yale.convene.utils.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.yale.convene.utils.Constants.TAG;

// The types specify: 1. input data type (String)
// 2. progress type (Integer)
// 3. result type (String)

public class PendingSurveyAsyncTask extends AsyncTask<String, Integer, List<SurveysBean>> {
    private List<SurveysBean> sourceList;
    private List<SurveysBean> resultList = new ArrayList<>();
    private ExternalDbOpenHelper externalDbOpenHelper;
    private PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener;
    private DBHandler handler;
    String surveyPrimaryKeyId;
    int surveyid;
    SharedPreferences preferences;

    protected void onPreExecute() {
        // Executed in UIThread
    }

    public PendingSurveyAsyncTask(Context con, List<SurveysBean> sourceList, ExternalDbOpenHelper externalDbOpenHelper, SharedPreferences defaultPreferences, SurveyControllerDbHelper periodicityCheckControllerDbHelper,
                                  PendingCompletedSurveyAsyncResultListener pendingCompletedSurveyAsyncResultListener, DBHandler handler, String surveyPrimaryKeyId, int surveyId) {
        this.sourceList = sourceList;
        this.externalDbOpenHelper = externalDbOpenHelper;
        this.pendingCompletedSurveyAsyncResultListener = pendingCompletedSurveyAsyncResultListener;
        this.handler = handler;
        this.surveyPrimaryKeyId = surveyPrimaryKeyId;
        this.surveyid = surveyId;
        this.preferences = defaultPreferences;
    }

    protected List<SurveysBean> doInBackground(String... strings) {
        for (int i = 0; i < sourceList.size(); i++) {

            int addCount = 0;
            if (sourceList.get(i).getPeriodicity() > addCount) {
                int getCount = handler.getPeriodicityPreviousCountOnline(sourceList.get(i), sourceList.get(i).getId(), sourceList.get(i).getPeriodicityFlag(), new Date(), surveyPrimaryKeyId);
                if (getCount == 0 && surveyid != sourceList.get(i).getId()) {
                  if (sourceList.get(i).getRuleEngine()!=null &&!sourceList.get(i).getRuleEngine().isEmpty()) {
                      if (validateRuleSet(sourceList.get(i).getRuleEngine(), surveyPrimaryKeyId))
                          resultList.add(sourceList.get(i));
                  }else{
                      resultList.add(sourceList.get(i));
                  }
                }

            }
        }
        return resultList;
    }

    private boolean validateRuleSet(String ruleSet, String surveyPrimaryKeyId) {
        if (!ruleSet.isEmpty()){
            try {
                JSONArray jsonArray= new JSONArray(ruleSet);
                List<Boolean> getSumValidation= new ArrayList<>();
               for (int i=0;i<jsonArray.length();i++){
                   JSONObject jsonObject= jsonArray.getJSONObject(i);
                   String getDataType= jsonObject.getString("data_type");
                   String getOperator= jsonObject.getString("operator");
                   String getValue= jsonObject.getString("value");
                   int getquestionId= jsonObject.getInt("question_id");
                   String getUserEnterText="";
                   if (getDataType.equalsIgnoreCase("Number"))
                         getUserEnterText= handler.getResponseText(surveyPrimaryKeyId,getquestionId);
                   else if (getDataType.equalsIgnoreCase("String"))
                       getUserEnterText= handler.getResponseChoise(surveyPrimaryKeyId,getquestionId,getValue);
                   Logger.logD("UserEntered Text","Response Table"+getUserEnterText);
                   if (!getUserEnterText.isEmpty()) {
                       if (DataBaseMapperClass.setRuleEnginScane(getValue, getOperator, Integer.parseInt(getUserEnterText))) {
                           getSumValidation.add(true);
                       }else{
                           getSumValidation.add(false);
                       }
                   }else if (getUserEnterText.isEmpty()){
                       getSumValidation.add(true);
                   }else{
                       getSumValidation.add(false);
                   }
               }
                if (getSumValidation.contains(false))
                    return false;
            } catch (JSONException e) {
                Logger.logE(TAG,"Ruleset Validation",e);
            }
        }

        return true;
    }
    protected void onPostExecute(List<SurveysBean> result) {
        if (preferences.getString(Constants.PROJECTFLOW, "").equalsIgnoreCase("1")) {
            int getSeletedProjectActivity = preferences.getInt(Constants.SELECTEDPROJECTID, 0);
            Logger.logD("Seleced Project Activity", getSeletedProjectActivity + "");
            List<SurveysBean> sortedProjectActivity=removeAddedActivityBasedOnProject(result, getSeletedProjectActivity);
            pendingCompletedSurveyAsyncResultListener.pendingSurveys(sortedProjectActivity);
        } else {
            pendingCompletedSurveyAsyncResultListener.pendingSurveys(result);
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
                                