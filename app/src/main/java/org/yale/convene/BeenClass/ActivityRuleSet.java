package org.yale.convene.BeenClass;

/**
 * Created by mahiti on 21/08/18.
 */

public class ActivityRuleSet {
    private  String datatype;
    private String operator;
    private int  QuestionId;
    private  int formId;
    private  String value;
    private String questionDisplay;

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int questionId) {
        QuestionId = questionId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getQuestionDisplay() {
        return questionDisplay;
    }

    public void setQuestionDisplay(String questionDisplay) {
        this.questionDisplay = questionDisplay;
    }
}
