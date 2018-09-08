package org.fwwb.convene.convenecode.BeenClass.boundarylevel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LocationTypeList {

    @SerializedName("location-type")
    @Expose
    private List<LocationType> locationType = new ArrayList<>();

    public List<LocationType> getLocationType() {
        return locationType;
    }

    public void setLocationType(List<LocationType> locationType) {
        this.locationType = locationType;
    }

}