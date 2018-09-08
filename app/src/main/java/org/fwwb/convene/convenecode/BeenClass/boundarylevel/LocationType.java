package org.fwwb.convene.convenecode.BeenClass.boundarylevel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LocationType {

    @SerializedName("boundaries")
    @Expose
    private List<Boundary> boundaries = new ArrayList<>();
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("name")
    @Expose
    private String name;

    public List<Boundary> getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(List<Boundary> boundaries) {
        this.boundaries = boundaries;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return name;
    }
}