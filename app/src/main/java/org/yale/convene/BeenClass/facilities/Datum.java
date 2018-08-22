package org.yale.convene.BeenClass.facilities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Datum implements Parcelable{
@SerializedName("uuid")
@Expose
private String uuid;
@SerializedName("thematic_area")
@Expose
private String thematicArea;
@SerializedName("beneficiary_id")
@Expose
private Integer beneficiaryId;
@SerializedName("name")
@Expose
private String name;
@SerializedName("facility_subtype_id")
@Expose
private Integer facilitySubtypeId;
@SerializedName("thematic_area_id")
@Expose
private Integer thematicAreaId;
    @SerializedName("services")
    @Expose
    private List<Integer> services = new ArrayList<>();
@SerializedName("created")
@Expose
private String created;
@SerializedName("modified")
@Expose
private String modified;
@SerializedName("facility_type")
@Expose
private String facilityType;
@SerializedName("parent_id")
@Expose
private Integer parentId;
@SerializedName("facility_type_id")
@Expose
private Integer facilityTypeId;
@SerializedName("facility_subtype")
@Expose
private String facilitySubtype;
    @SerializedName("address1")
    @Expose
    private String address1;

    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("contact_no")
    @Expose
    private String contactNo;



@SerializedName("btype")
@Expose
private String btype;
    @SerializedName("boundary_name")
    @Expose
    private String boundaryName;

    @SerializedName("boundary_level")
    @Expose
    private String boundaryLevel;
    @SerializedName("boundary_id")
    @Expose
    private String boundaryId;
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

    private String status;
    private int syncStatus;

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Datum(Parcel in) {
        thematicArea = in.readString();
        name = in.readString();
        created = in.readString();
        modified = in.readString();
        facilityType = in.readString();
        facilitySubtype = in.readString();
        btype = in.readString();
        partner = in.readString();
        boundaryName=in.readString();
        uuid=in.readString();
        address1=in.readString();
        address2=in.readString();
        pincode=in.readString();
        contactNo=in.readString();
    }


    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getBoundaryLevel() {
        return boundaryLevel;
    }

    public void setBoundaryLevel(String boundaryLevel) {
        this.boundaryLevel = boundaryLevel;
    }

    public String getBoundaryName() {
        return boundaryName;
    }

    public void setBoundaryName(String boundaryName) {
        this.boundaryName = boundaryName;
    }

    public String getBoundaryId() {
        return boundaryId;
    }

    public void setBoundaryId(String boundaryId) {
        this.boundaryId = boundaryId;
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

    public String getThematicArea() {
return thematicArea;
}

public void setThematicArea(String thematicArea) {
this.thematicArea = thematicArea;
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

public Integer getFacilitySubtypeId() {
return facilitySubtypeId;
}

public void setFacilitySubtypeId(Integer facilitySubtypeId) {
this.facilitySubtypeId = facilitySubtypeId;
}

public String toString(){
    return  name;
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

public String getFacilityType() {
return facilityType;
}

public void setFacilityType(String facilityType) {
this.facilityType = facilityType;
}

public Integer getParentId() {
return parentId;
}

public void setParentId(Integer parentId) {
this.parentId = parentId;
}

public Integer getFacilityTypeId() {
return facilityTypeId;
}

public void setFacilityTypeId(Integer facilityTypeId) {
this.facilityTypeId = facilityTypeId;
}

public String getFacilitySubtype() {
return facilitySubtype;
}

public void setFacilitySubtype(String facilitySubtype) {
this.facilitySubtype = facilitySubtype;
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

    public List<Integer> getServices() {
        return services;
    }

    public void setServices(List<Integer> services) {
        this.services = services;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(thematicArea);
        parcel.writeString(name);
        parcel.writeString(created);
        parcel.writeString(modified);
        parcel.writeString(facilityType);
        parcel.writeString(facilitySubtype);
        parcel.writeString(btype);
        parcel.writeString(partner);
        parcel.writeString(boundaryName);
        parcel.writeString(uuid);
        parcel.writeString(pincode);
        parcel.writeString(address1);
        parcel.writeString(address2);
        parcel.writeString(contactNo);

    }
}