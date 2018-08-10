package org.assistindia.convene.BeenClass;

/**
 * Created by mahiti on 20/9/17.
 */

public class SurveysBean {
    private String surveyName;
    private String beneficiaryIds;
    private String facilityIds;
    private int isSurveyDone;
    int id;
    private String uuid;

    private String periodicityFlag;
    private int pLimit;
    // 1: Pending* 2:Pending 3: edit/view 4: view

    private int isContinue;
    private int surveyPId ;
    private int surveyStatusBeenFlag;
    private int periodicitySurveyBeen;
    private int isViewOrEdit;
    private  String surveyEndDate;
    private  String ruleEngine;

    public String getSurveyEndDate() {
        return surveyEndDate;
    }

    public void setSurveyEndDate(String surveyEndDate) {
        this.surveyEndDate = surveyEndDate;
    }

    public int getPeriodicity() {
        return this.periodicitySurveyBeen;
    }

    public void setPeriodicity(int periodicity) {
        this.periodicitySurveyBeen = periodicity;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPeriodicityFlag() {
        return this.periodicityFlag;
    }

    public void setPeriodicityFlag(String periodicityFlag) {
        this.periodicityFlag = periodicityFlag;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int isSurveyDone() {
        return this.isSurveyDone;
    }

    public void setSurveyDone(int surveyDone) {
        this.isSurveyDone = surveyDone;
    }

    public String getSurveyName() {
        return this.surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getBeneficiaryIds() {
        return this.beneficiaryIds;
    }

    public void setBeneficiaryIds(String beneficiaryIds) {
        this.beneficiaryIds = beneficiaryIds;
    }

    public String getFacilityIds() {
        return this.facilityIds;
    }

    public void setFacilityIds(String facilityIds) {
        this.facilityIds = facilityIds;
    }

    public int getPLimit() {
        return this.pLimit;
    }

    public void setPLimit(int pLimit) {
        this.pLimit = pLimit;
    }

    public int getSurveyStatus() {
        //surveyStatus 1: Pending* 2:Pending 3:edit/view 4:view
        return this.surveyStatusBeenFlag;
    }

    public void setSurveyStatus(int surveyStatus) {
        this.surveyStatusBeenFlag = surveyStatus;
    }

    public int isContinue() {
        return this.isContinue;
    }

    public void setContinue(int aContinue) {
        this.isContinue = aContinue;
    }

    public int getSurveyPId() {
        return this.surveyPId;
    }

    public void setSurveyPId(int surveyPId) {
        this.surveyPId = surveyPId;
    }

    public int getIsViewOrEdit() {
        return isViewOrEdit;
    }

    public void setIsViewOrEdit(int isViewOrEdit) {
        //1: edit/view 2: view
        this.isViewOrEdit = isViewOrEdit;
    }

    public String getRuleEngine() {
        return ruleEngine;
    }

    public void setRuleEngine(String ruleEngine) {
        this.ruleEngine = ruleEngine;
    }
}
