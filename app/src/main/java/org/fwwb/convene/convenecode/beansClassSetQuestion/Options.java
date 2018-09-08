
package org.fwwb.convene.convenecode.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Options {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("question_pid")
    @Expose
    private Integer questionPid;
    @SerializedName("option_code")
    @Expose
    private String optionCode;
    @SerializedName("option_flag")
    @Expose
    private Integer optionFlag;
    @SerializedName("skip_code")
    @Expose
    private String skipCode;
    @SerializedName("validation")
    @Expose
    private String validation;
    @SerializedName("option_text")
    @Expose
    private String optionText;
    @SerializedName("order")
    @Expose
    private Integer option_order;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("language_id")
    @Expose
    private Integer languageId;
    @SerializedName("survey_id")
    @Expose
    private Integer surveyId;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("is_answer")
    @Expose
    private String isAnswer;
    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @SerializedName("extra_column1")
    @Expose
    private String extraColumn1;
    @SerializedName("extra_column2")
    @Expose
    private Integer extraColumn2;
    @SerializedName("Rule_engine")
    @Expose
    private String ruleEngin;
    @SerializedName("assessment_pid")
    @Expose
    private Integer assessmentPid;

    public Integer getAssessmentPid() {
        return assessmentPid;
    }

    public void setAssessmentPid(Integer assessmentPid) {
        this.assessmentPid = assessmentPid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionPid() {
        return questionPid;
    }

    public void setQuestionPid(Integer questionPid) {
        this.questionPid = questionPid;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public void setOptionCode(String optionCode) {
        this.optionCode = optionCode;
    }

    public Integer getOptionFlag() {
        return optionFlag;
    }

    public void setOptionFlag(Integer optionFlag) {
        this.optionFlag = optionFlag;
    }

    public String getSkipCode() {
        return skipCode;
    }

    public void setSkipCode(String skipCode) {
        this.skipCode = skipCode;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getIsAnswer() {
        return isAnswer;
    }

    public void setIsAnswer(String isAnswer) {
        this.isAnswer = isAnswer;
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

    public Integer getOption_order() {
        return option_order;
    }

    public void setOption_order(Integer option_order) {
        this.option_order = option_order;
    }

    public String getRuleEngin() {
        return ruleEngin;
    }

    public void setRuleEngin(String ruleEngin) {
        this.ruleEngin = ruleEngin;
    }
}
