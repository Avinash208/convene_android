package org.mahiti.convenemis.BeenClass;

/**
 * Created by mahiti on 9/4/15.
 */
public class StatusBean {
    private String typologyCode;
    private int surveyId;
    private String caseId="";
    private String language = "";
    private String name = "";
    private String primaryId = "";
    private String date ;
    private String clusterName;

    public  StatusBean(){

    }
    public StatusBean(String CaseId, String lang, String tCode, int sId, String clusterCode) {
        this.caseId=CaseId;
        this.language = lang;
        this.typologyCode=tCode;
        this.surveyId=sId;
        this.clusterCode = clusterCode;
    }


    public void setLanguage(String language) {
        this.language = language;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public StatusBean(String clusterName, String date, String surveyStatus, String section2, String language1, String typoCode, int pendSurveyId) {
        this.clusterName=clusterName;
        this.date=date;
        String surveyStatus1 = surveyStatus;
        this.language=language1;
        this.surveyId=pendSurveyId;
        this.typologyCode=typoCode;

    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public void setClusterCode(String clusterCode) {
        this.clusterCode = clusterCode;
    }

    private String clusterCode = "";

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getTypologyCode() {
        return typologyCode;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public void setTypologyCode(String typologyCode) {
        this.typologyCode = typologyCode;
    }

    public StatusBean(String CaseId, String lang, String tCode, int sId, String clusterCode, String date) {
        this.caseId=CaseId;
        this.language = lang;
        this.typologyCode=tCode;
        this.surveyId=sId;
        this.clusterCode = clusterCode;
        this.date=date;
    }


    public void setLang(String lang) {
        this.language = lang;
    }

    public String getLanguage() {
        return language;
    }

}
