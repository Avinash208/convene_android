
package org.yale.convene.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Linkage {

    @SerializedName("child_form_id")
    @Expose
    private String childFormId;
    @SerializedName("child_form_primaryid")
    @Expose
    private Integer childFormPrimaryid;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("linked_on")
    @Expose
    private String linkedOn;
    @SerializedName("child_form_type")
    @Expose
    private Integer childFormType;
    @SerializedName("parent_form_type")
    @Expose
    private Integer parentFormType;
    @SerializedName("parent_form_id")
    @Expose
    private String parentFormId;
    @SerializedName("parent_form_primaryid")
    @Expose
    private Integer parentFormPrimaryid;
    @SerializedName("relation_id")
    @Expose
    private Integer relationId;

    public String getChildFormId() {
        return childFormId;
    }

    public void setChildFormId(String childFormId) {
        this.childFormId = childFormId;
    }

    public Integer getChildFormPrimaryid() {
        return childFormPrimaryid;
    }

    public void setChildFormPrimaryid(Integer childFormPrimaryid) {
        this.childFormPrimaryid = childFormPrimaryid;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLinkedOn() {
        return linkedOn;
    }

    public void setLinkedOn(String linkedOn) {
        this.linkedOn = linkedOn;
    }

    public Integer getChildFormType() {
        return childFormType;
    }

    public void setChildFormType(Integer childFormType) {
        this.childFormType = childFormType;
    }

    public Integer getParentFormType() {
        return parentFormType;
    }

    public void setParentFormType(Integer parentFormType) {
        this.parentFormType = parentFormType;
    }

    public String getParentFormId() {
        return parentFormId;
    }

    public void setParentFormId(String parentFormId) {
        this.parentFormId = parentFormId;
    }

    public Integer getParentFormPrimaryid() {
        return parentFormPrimaryid;
    }

    public void setParentFormPrimaryid(Integer parentFormPrimaryid) {
        this.parentFormPrimaryid = parentFormPrimaryid;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

}
