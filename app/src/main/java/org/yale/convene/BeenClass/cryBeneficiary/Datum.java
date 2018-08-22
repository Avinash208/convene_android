
package org.yale.convene.BeenClass.cryBeneficiary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("parent")
    @Expose
    private String parent;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("beneficiary_type_id")
    @Expose
    private Integer beneficiaryTypeId;
    @SerializedName("btype")
    @Expose
    private String btype;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("partner")
    @Expose
    private String partner;
    @SerializedName("partner_id")
    @Expose
    private Integer partnerId;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("beneficiary_type")
    @Expose
    private String beneficiaryType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getBeneficiaryTypeId() {
        return beneficiaryTypeId;
    }

    public void setBeneficiaryTypeId(Integer beneficiaryTypeId) {
        this.beneficiaryTypeId = beneficiaryTypeId;
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

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(String beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

}
