
package org.assistindia.convene.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkipMandatory {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("question_pid")
    @Expose
    private Integer questionPid;
    @SerializedName("question_validation")
    @Expose
    private String questionValidation;
    @SerializedName("validation_order")
    @Expose
    private Double validationOrder;
    @SerializedName("skip_or_mandatory")
    @Expose
    private Integer skipOrMandatory;
    @SerializedName("sub_module_type")
    @Expose
    private String subModuleType;
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

    public Integer getQuestionPid() {
        return questionPid;
    }

    public void setQuestionPid(Integer questionPid) {
        this.questionPid = questionPid;
    }

    public String getQuestionValidation() {
        return questionValidation;
    }

    public void setQuestionValidation(String questionValidation) {
        this.questionValidation = questionValidation;
    }

    public Double getValidationOrder() {
        return validationOrder;
    }

    public void setValidationOrder(Double validationOrder) {
        this.validationOrder = validationOrder;
    }

    public Integer getSkipOrMandatory() {
        return skipOrMandatory;
    }

    public void setSkipOrMandatory(Integer skipOrMandatory) {
        this.skipOrMandatory = skipOrMandatory;
    }

    public String getSubModuleType() {
        return subModuleType;
    }

    public void setSubModuleType(String subModuleType) {
        this.subModuleType = subModuleType;
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
