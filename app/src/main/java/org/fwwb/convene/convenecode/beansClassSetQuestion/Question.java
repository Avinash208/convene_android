
package org.fwwb.convene.convenecode.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.fwwb.convene.convenecode.BeenClass.QuestionJson;

public class Question {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("question_code")
    @Expose
    private Integer questionCode;
    @SerializedName("answer_type")
    @Expose
    private Integer answerType;
    @SerializedName("survey_id")
    @Expose
    private Integer surveyId;
    @SerializedName("block_id")
    @Expose
    private Integer blockId;
    @SerializedName("sub_question")
    @Expose
    private String subQuestion;
    @SerializedName("validation")
    @Expose
    private String validation;
    @SerializedName("question_text")
    @Expose
    private String questionText;
    @SerializedName("help_text")
    @Expose
    private String helpText;
    @SerializedName("instruction_text")
    @Expose
    private String instructionText;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("language_id")
    @Expose
    private Integer languageId;
    @SerializedName("mandatory")
    @Expose
    private Integer mandatory;
    @SerializedName("question_order")
    @Expose
    private Double questionOrder;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("keyword")
    @Expose
    private String keyword;
    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @SerializedName("extra_column1")
    @Expose
    private String extraColumn1;
    @SerializedName("extra_column2")
    @Expose
    private Integer extraColumn2;

    @SerializedName("parent_beneficiary_id")
    @Expose
    private Integer parentBeneficiaryid;

    @SerializedName("question_id")
    @Expose
    private Integer questionid;
    @SerializedName("question_json")
    @Expose
    private QuestionJson questionJson;
    @SerializedName("display_as_name")
    @Expose
    private String displayAsName;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(Integer questionCode) {
        this.questionCode = questionCode;
    }

    public Integer getAnswerType() {
        return answerType;
    }

    public void setAnswerType(Integer answerType) {
        this.answerType = answerType;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public String getSubQuestion() {
        return subQuestion;
    }

    public void setSubQuestion(String subQuestion) {
        this.subQuestion = subQuestion;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getInstructionText() {
        return instructionText;
    }

    public void setInstructionText(String instructionText) {
        this.instructionText = instructionText;
    }


    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
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

    public Integer getMandatory() {
        return mandatory;
    }

    public void setMandatory(Integer mandatory) {
        this.mandatory = mandatory;
    }

    public Double getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Double questionOrder) {
        this.questionOrder = questionOrder;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public Integer getParentBeneficiaryid() {
        return parentBeneficiaryid;
    }

    public void setParentBeneficiaryid(Integer parentBeneficiaryid) {
        this.parentBeneficiaryid = parentBeneficiaryid;
    }

    public Integer getQuestionid() {
        return questionid;
    }

    public void setQuestionid(Integer questionid) {
        this.questionid = questionid;
    }


    public QuestionJson getQuestionJson() {
        return questionJson;
    }

    public void setQuestionJson(QuestionJson questionJson) {
        this.questionJson = questionJson;
    }

    public String getDisplayAsName() {
        return displayAsName;
    }

    public void setDisplayAsName(String displayAsName) {
        this.displayAsName = displayAsName;
    }
}
