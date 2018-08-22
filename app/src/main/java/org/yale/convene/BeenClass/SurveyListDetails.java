
package org.yale.convene.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.yale.convene.BeenClass.parentChild.SurveyDetail;

import java.util.List;

public class SurveyListDetails {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("surveyDetails")
    @Expose
    private List<SurveyDetail> surveyDetails = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("application_levels")
    @Expose
    private String applicationLevels;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SurveyDetail> getSurveyDetails() {
        return surveyDetails;
    }

    public void setSurveyDetails(List<SurveyDetail> surveyDetails) {
        this.surveyDetails = surveyDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApplicationLevels() {
        return applicationLevels;
    }

    public void setApplicationLevels(String applicationLevels) {
        this.applicationLevels = applicationLevels;
    }

}
