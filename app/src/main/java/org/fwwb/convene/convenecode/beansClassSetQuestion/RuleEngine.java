
package org.fwwb.convene.convenecode.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RuleEngine {

    @SerializedName("operator")
    @Expose
    private String operator;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("data_type")
    @Expose
    private String dataType;
    @SerializedName("question_id")
    @Expose
    private Integer questionId;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

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

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

}
