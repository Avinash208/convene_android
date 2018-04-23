package org.mahiti.convenemis.BeenClass.partners;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

@SerializedName("project_name")
@Expose
private String projectName;
@SerializedName("name")
@Expose
private String name;
@SerializedName("program_id")
@Expose
private Integer programId;
@SerializedName("active")
@Expose
private Integer active;
@SerializedName("project_id")
@Expose
private Integer projectId;
@SerializedName("partner_id")
@Expose
private String partnerId;
@SerializedName("id")
@Expose
private Integer id;

public String getProjectName() {
return projectName;
}

public void setProjectName(String projectName) {
this.projectName = projectName;
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

public Integer getProgramId() {
return programId;
}

public void setProgramId(Integer programId) {
this.programId = programId;
}

public Integer getActive() {
return active;
}

public void setActive(Integer active) {
this.active = active;
}

public Integer getProjectId() {
return projectId;
}

public void setProjectId(Integer projectId) {
this.projectId = projectId;
}

public String getPartnerId() {
return partnerId;
}

public void setPartnerId(String partnerId) {
this.partnerId = partnerId;
}

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

}