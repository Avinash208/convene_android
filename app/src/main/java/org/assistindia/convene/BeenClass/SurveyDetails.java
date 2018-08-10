package org.assistindia.convene.BeenClass;

import java.io.Serializable;

/**
 * Created by mahiti on 28/12/15.
 */
public class SurveyDetails implements Serializable {

    private int surveyId;
    private String orderSurveyId;
    private String surveyName;
    private String surveyVersion;
    public SurveyDetails(){
        // not using this constructor
    }
    public SurveyDetails(int surveyId, String surveyName, String version, String orderSurvey_Id){
        this.surveyId = surveyId;
        this.orderSurveyId = orderSurvey_Id;
        this.surveyName = surveyName;
        this.surveyVersion = version;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyVersion() {
        return surveyVersion;
    }

    public void setSurveyVersion(String surveyVersion) {
        this.surveyVersion = surveyVersion;
    }

    public String getOrderSurveyId() {
        return orderSurveyId;
    }

    public void setOrderSurveyId(String orderSurveyId) {
        this.orderSurveyId = orderSurveyId;
    }
}