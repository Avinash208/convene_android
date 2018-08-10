package org.assistindia.convene.BeenClass;


public class AddressBean {
    private String address1;
    private String address2;
    private String pincode;
    private int boundaryId;
    private String boundaryName;


    public String getBoundaryName() {
        return boundaryName;
    }

    public void setBoundaryName(String boundaryName) {
        this.boundaryName = boundaryName;
    }

    public void setBoundaryId(int boundaryId) {
        this.boundaryId = boundaryId;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

}
