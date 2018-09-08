
package org.fwwb.convene.convenecode.beansClassSetQuestion;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Option {

    @SerializedName("assessment_pid")
    @Expose
    private Integer assessmentPid;
    @SerializedName("survey_id")
    @Expose
    private Integer surveyId;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("skip_code")
    @Expose
    private String skipCode;
    @SerializedName("extra_column1")
    @Expose
    private String extraColumn1;
    @SerializedName("language_id")
    @Expose
    private Integer languageId;
    @SerializedName("option_code")
    @Expose
    private String optionCode;
    @SerializedName("option_flag")
    @Expose
    private Integer optionFlag;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @SerializedName("Rule_engine")
    @Expose
    private List<RuleEngine> ruleEngine = null;
    @SerializedName("extra_column2")
    @Expose
    private Integer extraColumn2;
    @SerializedName("option_text")
    @Expose
    private String optionText;
    @SerializedName("validation")
    @Expose
    private String validation;
    @SerializedName("is_answer")
    @Expose
    private String isAnswer;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("question_pid")
    @Expose
    private Integer questionPid;

    @SerializedName("other_choice")
    @Expose
    private boolean otherChoice;

    public Integer getAssessmentPid() {
        return assessmentPid;
    }

    public void setAssessmentPid(Integer assessmentPid) {
        this.assessmentPid = assessmentPid;
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

    public String getSkipCode() {
        return skipCode;
    }

    public void setSkipCode(String skipCode) {
        this.skipCode = skipCode;
    }

    public String getExtraColumn1() {
        return extraColumn1;
    }

    public void setExtraColumn1(String extraColumn1) {
        this.extraColumn1 = extraColumn1;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public List<RuleEngine> getRuleEngine() {
        return ruleEngine;
    }

    public void setRuleEngine(List<RuleEngine> ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public Integer getExtraColumn2() {
        return extraColumn2;
    }

    public void setExtraColumn2(Integer extraColumn2) {
        this.extraColumn2 = extraColumn2;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getIsAnswer() {
        return isAnswer;
    }

    public void setIsAnswer(String isAnswer) {
        this.isAnswer = isAnswer;
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

    public boolean isOtherChoice() {
        return otherChoice;
    }

    public void setOtherChoice(boolean otherChoice) {
        this.otherChoice = otherChoice;
    }
}
