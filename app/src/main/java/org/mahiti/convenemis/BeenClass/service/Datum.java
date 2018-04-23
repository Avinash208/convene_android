
package org.mahiti.convenemis.BeenClass.service;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum implements Parcelable{

    @SerializedName("service_subtype_id")
    @Expose
    private Integer serviceSubtypeId;
    @SerializedName("beneficiary_id")
    @Expose
    private Integer beneficiaryId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("thematic_area_id")
    @Expose
    private Integer thematicAreaId;
    @SerializedName("service_type_id")
    @Expose
    private Integer serviceTypeId;
    @SerializedName("service_subtype")
    @Expose
    private String service_subtype;
    @SerializedName("thematic_area")
    @Expose
    private String thematic_area;
    @SerializedName("service_type")
    @Expose
    private String service_type;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("partner_id")
    @Expose
    private Integer partnerId;
    @SerializedName("id")
    @Expose
    private Integer id;

    public Datum(Parcel in) {
        name = in.readString();
        created = in.readString();
        service_subtype = in.readString();
        thematic_area = in.readString();
        service_type = in.readString();
        modified = in.readString();
    }

    public static final Creator<Datum> CREATOR = new Creator<Datum>() {
        @Override
        public Datum createFromParcel(Parcel in) {
            return new Datum(in);
        }

        @Override
        public Datum[] newArray(int size) {
            return new Datum[size];
        }
    };

    public Datum() {

    }

    public String getService_subtype() {
        return service_subtype;
    }

    public void setService_subtype(String service_subtype) {
        this.service_subtype = service_subtype;
    }

    public String getThematic_area() {
        return thematic_area;
    }

    public void setThematic_area(String thematic_area) {
        this.thematic_area = thematic_area;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public Integer getServiceSubtypeId() {
        return serviceSubtypeId;
    }

    public void setServiceSubtypeId(Integer serviceSubtypeId) {
        this.serviceSubtypeId = serviceSubtypeId;
    }

    public Integer getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Integer beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
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

    public Integer getThematicAreaId() {
        return thematicAreaId;
    }

    public void setThematicAreaId(Integer thematicAreaId) {
        this.thematicAreaId = thematicAreaId;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
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

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(created);
        parcel.writeString(service_subtype);
        parcel.writeString(thematic_area);
        parcel.writeString(service_type);
        parcel.writeString(modified);
        parcel.writeInt(id);
        parcel.writeInt(active);
        parcel.writeInt(serviceSubtypeId);
        parcel.writeInt(serviceTypeId);
        parcel.writeInt(thematicAreaId);
    }
}
