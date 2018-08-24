package org.yale.convene.BeenClass;

import java.util.List;

/**
 * Created by mahiti on 9/4/15.
 */
public class StatusBean {
    private String typologyCode;
    private String surveyId;
    private String caseId="";
    private String language = "";
    private String name = "";
    private String primaryId = "";
    private String date ;
    private String clusterName;
    private int parent_form_primaryid;
    private String uuid;
    private String Languageid;
    private String beneficiaryName;
    private String summaryQids;
    private String serverPrimaryKey;

    private List<QuestionAnswer> questionAnswerList;

    public  StatusBean(){

    }
    public StatusBean(String CaseId, String lang, String tCode, String sId, String clusterCode) {
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

    public StatusBean(String clusterName, String date, String surveyStatus, String section2, String language1, String typoCode, String pendSurveyId,
                      List<QuestionAnswer> questionAnswerList,int parent_form_primaryid) {
        this.clusterName=clusterName;
        this.date=date;
        String surveyStatus1 = surveyStatus;
        this.language=language1;
        this.surveyId=pendSurveyId;
        this.typologyCode=typoCode;
        this.questionAnswerList=questionAnswerList;
        this.parent_form_primaryid=parent_form_primaryid;

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

    public String  getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public void setTypologyCode(String typologyCode) {
        this.typologyCode = typologyCode;
    }

    public StatusBean(String CaseId, String lang, String tCode, String sId, String clusterCode, String date) {
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

    public List<QuestionAnswer> getQuestionAnswerList() {
        return questionAnswerList;
    }

    public void setQuestionAnswerList(List<QuestionAnswer> questionAnswerList) {
        this.questionAnswerList = questionAnswerList;
    }

    public int getParent_form_primaryid() {
        return parent_form_primaryid;
    }

    public void setParent_form_primaryid(int parent_form_primaryid) {
        this.parent_form_primaryid = parent_form_primaryid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLanguageid() {
        return Languageid;
    }

    public void setLanguageid(String languageid) {
        Languageid = languageid;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getSummaryQids() {
        return summaryQids;
    }

    public void setSummaryQids(String summaryQids) {
        this.summaryQids = summaryQids;
    }

    public String getServerPrimaryKey() {
        return serverPrimaryKey;
    }

    public void setServerPrimaryKey(String serverPrimaryKey) {
        this.serverPrimaryKey = serverPrimaryKey;
    }
}
