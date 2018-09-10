package org.fwwb.convene.convenecode.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponsesDatum {

    @SerializedName("response_id")
    @Expose
    private Integer responseId;

    @SerializedName("bene_uuid")
    @Expose
    private String bene_uuid;
    @SerializedName("cluster_id")
    @Expose
    private String cluster_id;
    @SerializedName("cluster_level")
    @Expose
    private String cluster_level;
    @SerializedName("faci_uuid")
    @Expose
    private String faci_uuid;
    @SerializedName("survey_id")
    @Expose
    private Integer surveyId;
    @SerializedName("collected_date")
    @Expose
    private String collectedDate;

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("server_date_time")
    @Expose
    private String serverDateTime;

    public Integer getResponseId() {
        return responseId;
    }

    public void setResponseId(Integer responseId) {
        this.responseId = responseId;
    }

    public String getBene_uuid() {
        return bene_uuid;
    }

    public void setBene_uuid(String bene_uuid) {
        this.bene_uuid = bene_uuid;
    }

    public String getFaci_uuid() {
        return faci_uuid;
    }

    public void setFaci_uuid(String faci_uuid) {
        this.faci_uuid = faci_uuid;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public String getCollectedDate() {
        return collectedDate;
    }

    public void setCollectedDate(String collectedDate) {
        this.collectedDate = collectedDate;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getServerDateTime() {
        return serverDateTime;
    }

    public void setServerDateTime(String serverDateTime) {
        this.serverDateTime = serverDateTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(String cluster_id) {
        this.cluster_id = cluster_id;
    }

    public String getCluster_level() {
        return cluster_level;
    }

    public void setCluster_level(String cluster_level) {
        this.cluster_level = cluster_level;
    }
}