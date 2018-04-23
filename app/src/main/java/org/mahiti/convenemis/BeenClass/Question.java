
package org.mahiti.convenemis.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Question {

    @SerializedName("questionId")
    @Expose
    private Integer questionId;
    @SerializedName("questionCode")
    @Expose
    private Integer questionCode;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("input")
    @Expose
    private Input input;
    @SerializedName("validateOn")
    @Expose
    private String validateOn;
    @SerializedName("validations")
    @Expose
    private List<Validation> validations = null;

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(Integer questionCode) {
        this.questionCode = questionCode;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public String getValidateOn() {
        return validateOn;
    }

    public void setValidateOn(String validateOn) {
        this.validateOn = validateOn;
    }

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
    }

}
