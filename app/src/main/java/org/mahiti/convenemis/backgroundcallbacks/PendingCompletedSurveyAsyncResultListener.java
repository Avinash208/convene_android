package org.mahiti.convenemis.backgroundcallbacks;

import org.mahiti.convenemis.BeenClass.SurveysBean;

import java.util.List;

/**
 * Created by mahiti on 28/3/18.
 */

public interface PendingCompletedSurveyAsyncResultListener {

     void pendingSurveys(List<SurveysBean> pendingSurvey);
     void completedSurveys(List<SurveysBean> pendingSurvey);
}
