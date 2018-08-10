
package org.assistindia.convene.BeenClass;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activitylist {

    @SerializedName("levelids")
    @Expose
    private List<Levelid> levelids = null;
    @SerializedName("activity_name")
    @Expose
    private String activityName;
    @SerializedName("order_levels")
    @Expose
    private String orderLevels;
    @SerializedName("activity_id")
    @Expose
    private Integer activityId;
    @SerializedName("labels")
    @Expose
    private String labels;

    public List<Levelid> getLevelids() {
        return levelids;
    }

    public void setLevelids(List<Levelid> levelids) {
        this.levelids = levelids;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getOrderLevels() {
        return orderLevels;
    }

    public void setOrderLevels(String orderLevels) {
        this.orderLevels = orderLevels;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

}
