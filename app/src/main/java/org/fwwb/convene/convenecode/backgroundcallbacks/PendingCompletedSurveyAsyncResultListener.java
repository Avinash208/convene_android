package org.fwwb.convene.convenecode.backgroundcallbacks;

import org.fwwb.convene.convenecode.BeenClass.SurveysBean;

import java.util.List;

/**
 * Created by mahiti on 28/3/18.
 */

public interface PendingCompletedSurveyAsyncResultListener {

     void pendingSurveys(List<SurveysBean> pendingSurvey);
     void completedSurveys(List<SurveysBean> pendingSurvey);
}
