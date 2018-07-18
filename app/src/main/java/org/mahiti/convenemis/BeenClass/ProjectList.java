
package org.mahiti.convenemis.BeenClass;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProjectList {

    @SerializedName("project_id")
    @Expose
    private Integer projectId;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("project_name")
    @Expose
    private String projectName;
    @SerializedName("activitylist")
    @Expose
    private List<Activitylist> activitylist = null;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<Activitylist> getActivitylist() {
        return activitylist;
    }

    public void setActivitylist(List<Activitylist> activitylist) {
        this.activitylist = activitylist;
    }
    public String toString(){
        return projectName;
    }

}
