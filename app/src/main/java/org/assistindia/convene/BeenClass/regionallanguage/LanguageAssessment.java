
package org.assistindia.convene.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageAssessment {

    @SerializedName("updated_time")
    @Expose
    private String updatedTime;

    @SerializedName("extra_column2")
    @Expose
    private Integer extraColumn2;

    @SerializedName("extra_column1")
    @Expose
    private String extraColumn1;

    @SerializedName("assessment")
    @Expose
    private String assessment;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("assessment_pid")
    @Expose
    private Integer assessmentPid;
    @SerializedName("language_id")
    @Expose
    private Integer languageId;

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public Integer getAssessmentPid() {
        return assessmentPid;
    }

    public void setAssessmentPid(Integer assessmentPid) {
        this.assessmentPid = assessmentPid;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }


    public Integer getExtraColumn2() {
        return extraColumn2;
    }

    public void setExtraColumn2(Integer extraColumn2) {
        this.extraColumn2 = extraColumn2;
    }


    public String getExtraColumn1() {
        return extraColumn1;
    }

    public void setExtraColumn1(String extraColumn1) {
        this.extraColumn1 = extraColumn1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

}
