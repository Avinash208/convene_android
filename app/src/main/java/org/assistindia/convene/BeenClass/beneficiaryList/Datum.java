
package org.assistindia.convene.BeenClass.beneficiaryList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("edit_user_perm")
    @Expose
    private Boolean editUserPerm;
    @SerializedName("school_type_id")
    @Expose
    private String schoolTypeId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("jsondata")
    @Expose
    private Jsondata jsondata;
    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("btype")
    @Expose
    private String btype;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("partner_id")
    @Expose
    private String partnerId;
    @SerializedName("id")
    @Expose
    private Integer id;

    public Boolean getEditUserPerm() {
        return editUserPerm;
    }

    public void setEditUserPerm(Boolean editUserPerm) {
        this.editUserPerm = editUserPerm;
    }

    public String getSchoolTypeId() {
        return schoolTypeId;
    }

    public void setSchoolTypeId(String schoolTypeId) {
        this.schoolTypeId = schoolTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Jsondata getJsondata() {
        return jsondata;
    }

    public void setJsondata(Jsondata jsondata) {
        this.jsondata = jsondata;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBtype() {
        return btype;
    }

    public void setBtype(String btype) {
        this.btype = btype;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
