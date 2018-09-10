package org.fwwb.convene.convenecode.api.BeneficiaryApis;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address {

@SerializedName("least_location_name")
@Expose
private String leastLocationName;
@SerializedName("address_id")
@Expose
private String addressId;
@SerializedName("least_location_id")
@Expose
private Integer leastLocationId;
@SerializedName("address1")
@Expose
private String address1;
@SerializedName("address2")
@Expose
private String address2;
@SerializedName("pincode")
@Expose
private String pincode;
@SerializedName("primary")
@Expose
private Integer primary;
@SerializedName("location_level")
@Expose
private Integer locationLevel;

public String getLeastLocationName() {
return leastLocationName;
}

public void setLeastLocationName(String leastLocationName) {
this.leastLocationName = leastLocationName;
}

public String getAddressId() {
return addressId;
}

public void setAddressId(String addressId) {
this.addressId = addressId;
}

public Integer getLeastLocationId() {
return leastLocationId;
}

public void setLeastLocationId(Integer leastLocationId) {
this.leastLocationId = leastLocationId;
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

public Integer getPrimary() {
return primary;
}

public void setPrimary(Integer primary) {
this.primary = primary;
}

public Integer getLocationLevel() {
return locationLevel;
}

public void setLocationLevel(Integer locationLevel) {
this.locationLevel = locationLevel;
}

}