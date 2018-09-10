package org.fwwb.convene.convenecode.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class DependencyObject {

    @SerializedName("dependency_qid")
    @Expose
    private Integer dependencyQid;

    @SerializedName("depending_datatype")
    @Expose
    private String dependingDatatype;

    @SerializedName("question_id")
    @Expose
    private Integer questionId;

    private String value;


    public Integer getDependencyQid() {
        return dependencyQid;
    }

    public void setDependencyQid(Integer dependencyQid) {
        this.dependencyQid = dependencyQid;
    }

    public String getDependingDatatype() {
        return dependingDatatype;
    }

    public void setDependingDatatype(String dependingDatatype) {
        this.dependingDatatype = dependingDatatype;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
