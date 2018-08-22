package org.yale.convene.BeenClass.facilities;

/**
 * Created by mahiti on 29/8/17.
 */

public class Facility {
    private String name;
    private int facilityTypeId;
    private int facilitySubtypeId;
    private int thematicAreaId;
    private String services;
    private String address1;
    private String address2;
    private int boundaryId;
    private int pincode;
    private int id;
    private String uuid;
    private String facilityType;
    private String syncStatus;
    private String createdDateFac;


    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFacilityTypeId() {
        return facilityTypeId;
    }

    public void setFacilityTypeId(int facilityTypeId) {
        this.facilityTypeId = facilityTypeId;
    }

    public int getFacilitySubtypeId() {
        return facilitySubtypeId;
    }

    public void setFacilitySubtypeId(int facilitySubtypeId) {
        this.facilitySubtypeId = facilitySubtypeId;
    }

    public int getThematicAreaId() {
        return thematicAreaId;
    }

    public void setThematicAreaId(int thematicAreaId) {
        this.thematicAreaId = thematicAreaId;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
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

    public int getBoundaryId() {
        return boundaryId;
    }

    public void setBoundaryId(int boundaryId) {
        this.boundaryId = boundaryId;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public String getCreatedDateFac() {
        return createdDateFac;
    }

    public void setCreatedDateFac(String createdDateFac) {
        this.createdDateFac = createdDateFac;
    }
}
