package org.yale.convene.backgroundcallbacks;

import org.yale.convene.BeenClass.SurveysBean;

import java.util.List;

/**
 * Created by mahiti on 28/3/18.
 */

public interface PendingCompletedSurveyAsyncResultListener {

     void pendingSurveys(List<SurveysBean> pendingSurvey);
     void completedSurveys(List<SurveysBean> pendingSurvey);
}
