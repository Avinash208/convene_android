
package org.fwwb.convene.convenecode.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ruleset {

    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("data_type")
    @Expose
    private String dataType;
    @SerializedName("operator")
    @Expose
    private String operator;
    @SerializedName("question_id")
    @Expose
    private Integer questionId;
    @SerializedName("form_id")
    @Expose
    private Integer formId;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("display_question")
    @Expose
    private String displayQuestion;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getDisplayQuestion() {
        return displayQuestion;
    }

    public void setDisplayQuestion(String displayQuestion) {
        this.displayQuestion = displayQuestion;
    }

}
