
package org.yale.convene.BeenClass.facilitiesBeen;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("beneficiary_id")
    @Expose
    private Object beneficiaryId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("facility_subtype_id")
    @Expose
    private Integer facilitySubtypeId;
    @SerializedName("thematic_area_id")
    @Expose
    private Integer thematicAreaId;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("parent_id")
    @Expose
    private Object parentId;
    @SerializedName("facility_type_id")
    @Expose
    private Integer facilityTypeId;
    @SerializedName("btype")
    @Expose
    private Object btype;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("partner_id")
    @Expose
    private Object partnerId;
    @SerializedName("id")
    @Expose
    private Integer id;

    public Object getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Object beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFacilitySubtypeId() {
        return facilitySubtypeId;
    }

    public void setFacilitySubtypeId(Integer facilitySubtypeId) {
        this.facilitySubtypeId = facilitySubtypeId;
    }

    public Integer getThematicAreaId() {
        return thematicAreaId;
    }

    public void setThematicAreaId(Integer thematicAreaId) {
        this.thematicAreaId = thematicAreaId;
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

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public Integer getFacilityTypeId() {
        return facilityTypeId;
    }

    public void setFacilityTypeId(Integer facilityTypeId) {
        this.facilityTypeId = facilityTypeId;
    }

    public Object getBtype() {
        return btype;
    }

    public void setBtype(Object btype) {
        this.btype = btype;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Object getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Object partnerId) {
        this.partnerId = partnerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public  String toString()
    {
        return name;
    }

}
