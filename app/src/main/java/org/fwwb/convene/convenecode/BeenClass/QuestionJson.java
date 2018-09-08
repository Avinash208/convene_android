package org.fwwb.convene.convenecode.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class QuestionJson {

    @SerializedName("query_has_parent")
    @Expose
    private Integer queryHasParent;

    @SerializedName("parent_beneficiary_id")
    @Expose
    private Integer parentBeneficiaryId;

    @SerializedName("dependency")
    @Expose
    private List<DependencyObject> dependencyQId;

    @SerializedName("reference_id")
    @Expose
    private Integer referenceId;

    @SerializedName("display_datatype")
    @Expose
    private String dependencyDatatype;

    public Integer getQueryHasParent() {
        return queryHasParent;
    }

    public void setQueryHasParent(Integer queryHasParent) {
        this.queryHasParent = queryHasParent;
    }

    public Integer getParentBeneficiaryId() {
        return parentBeneficiaryId;
    }

    public void setParentBeneficiaryId(Integer parentBeneficiaryId) {
        this.parentBeneficiaryId = parentBeneficiaryId;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public String getDependencyDatatype() {
        return dependencyDatatype;
    }

    public void setDependencyDatatype(String dependencyDatatype) {
        this.dependencyDatatype = dependencyDatatype;
    }

    public List<DependencyObject> getDependencyQId() {
        return dependencyQId;
    }

    public void setDependencyQId(List<DependencyObject> dependencyQId) {
        this.dependencyQId = dependencyQId;
    }
}
