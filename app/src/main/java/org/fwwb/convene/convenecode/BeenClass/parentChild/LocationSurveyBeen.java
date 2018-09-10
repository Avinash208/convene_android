package org.fwwb.convene.convenecode.BeenClass.parentChild;

public class LocationSurveyBeen {

    private String locationName;
    private String captureDate;
    private String createdDate;
    private String locationLevel;
    private int surveyStatusFlag;
    private int clusterId;
    private int responseId;

    private int isContinue;
    private int isOnline;
    private int isEditable;
    private String uuid;
    private int surveyid;


    public LocationSurveyBeen()
    {

    }
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(String captureDate) {
        this.captureDate = captureDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLocationLevel() {
        return locationLevel;
    }

    public void setLocationLevel(String locationLevel) {
        this.locationLevel = locationLevel;
    }

    public int getSurveyStatusFlag() {
        return surveyStatusFlag;
    }

    public void setSurveyStatusFlag(int surveyStatusFlag) {
        //surveyStatusFlag: 1=pending 2=*pending 3=currentCompleted
        this.surveyStatusFlag = surveyStatusFlag;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }


    public int getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(int isEditable) {
        this.isEditable = isEditable;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getIsContinue() {
        return isContinue;
    }

    public void setIsContinue(int isContinue) {
        this.isContinue = isContinue;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getSurveyid() {
        return surveyid;
    }

    public void setSurveyid(int surveyid) {
        this.surveyid = surveyid;
    }
}
