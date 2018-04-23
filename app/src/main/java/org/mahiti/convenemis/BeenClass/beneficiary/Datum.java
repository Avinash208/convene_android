
package org.mahiti.convenemis.BeenClass.beneficiary;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Datum {

    @SerializedName("alias_name")
    @Expose
    private String aliasName;
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
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("address")
    @Expose
    private List<Address> address = new ArrayList<>();
    @SerializedName("dob_option")
    @Expose
    private String dobOption;

    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("contact_no")
    @Expose
    private List<String> contactNo = new ArrayList<>();
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
    @SerializedName("least_location_id")
    @Expose
    private Integer least_location_id;
    @SerializedName("beneficiary_type")
    @Expose
    private String beneficiaryType;
    @SerializedName("least_location_name")
    @Expose
    private String least_location_name;
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("parent_uuid")
    @Expose
    private String parent_uuid;

    private String status;
    private boolean isSelected;
    private int syncStatus;


    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getDobOption() {
        return dobOption;
    }

    public void setDobOption(String dobOption) {
        this.dobOption = dobOption;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getParent_uuid() {
        return parent_uuid;
    }

    public void setParent_uuid(String parent_uuid) {
        this.parent_uuid = parent_uuid;
    }

    public String toString(){
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public List<String> getContactNo() {
        return contactNo;
    }

    public void setContactNo(List<String> contactNo) {
        this.contactNo = contactNo;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getLeast_location_id() {
        return least_location_id;
    }

    public void setLeast_location_id(Integer least_location_id) {
        this.least_location_id = least_location_id;
    }

    public String getLeast_location_name() {
        return least_location_name;
    }

    public void setLeast_location_name(String least_location_name) {
        this.least_location_name = least_location_name;
    }
}
