
package org.assistindia.convene.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkipRules {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("survey_id")
    @Expose
    private Integer surveyId;
    @SerializedName("question_id")
    @Expose
    private Integer questionId;
    @SerializedName("sub_module_type")
    @Expose
    private String subModuleType;
    @SerializedName("show_status")
    @Expose
    private Integer showStatus;
    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @SerializedName("extra_column1")
    @Expose
    private String extraColumn1;
    @SerializedName("extra_column2")
    @Expose
    private Integer extraColumn2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getSubModuleType() {
        return subModuleType;
    }

    public void setSubModuleType(String subModuleType) {
        this.subModuleType = subModuleType;
    }

    public Integer getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(Integer showStatus) {
        this.showStatus = showStatus;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getExtraColumn1() {
        return extraColumn1;
    }

    public void setExtraColumn1(String extraColumn1) {
        this.extraColumn1 = extraColumn1;
    }

    public Integer getExtraColumn2() {
        return extraColumn2;
    }

    public void setExtraColumn2(Integer extraColumn2) {
        this.extraColumn2 = extraColumn2;
    }

}
