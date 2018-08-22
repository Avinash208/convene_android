
package org.yale.convene.BeenClass.houseHoldList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("btype")
    @Expose
    private String btype;
    @SerializedName("partner_id")
    @Expose
    private Integer partnerId;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("jsondata")
    @Expose
    private Jsondata jsondata;
    @SerializedName("id")
    @Expose
    private Integer id;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getBtype() {
        return btype;
    }

    public void setBtype(String btype) {
        this.btype = btype;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Jsondata getJsondata() {
        return jsondata;
    }

    public void setJsondata(Jsondata jsondata) {
        this.jsondata = jsondata;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
