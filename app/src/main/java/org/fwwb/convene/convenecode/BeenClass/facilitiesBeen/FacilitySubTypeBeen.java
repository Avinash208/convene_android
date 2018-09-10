package org.fwwb.convene.convenecode.BeenClass.facilitiesBeen;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mahiti on 18/5/17.
 */

public class FacilitySubTypeBeen
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    public FacilitySubTypeBeen(){

    }

    public FacilitySubTypeBeen(int id,String name){
        this.id=id;
        this.name=name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String toString()
    {
        return name;
    }

}
