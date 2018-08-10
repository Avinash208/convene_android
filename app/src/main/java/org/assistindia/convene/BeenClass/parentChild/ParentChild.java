
package org.assistindia.convene.BeenClass.parentChild;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ParentChild {

    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("child_id")
    @Expose
    private List<Integer> childId = new ArrayList<>();
    @SerializedName("order_level")
    @Expose
    private String orderLevel;
    @SerializedName("id")
    @Expose
    private Integer id;

    /**
     * 
     * @return
     *     The active
     */
    public Integer getActive() {
        return active;
    }

    /**
     * 
     * @param active
     *     The active
     */
    public void setActive(Integer active) {
        this.active = active;
    }

    /**
     * 
     * @return
     *     The parentId
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 
     * @param parentId
     *     The parent_id
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     *
     * @return
     *     The modifiedDate
     */
    public String getModifiedDate() {
        return modifiedDate;
    }

    /**
     *
     * @param modifiedDate
     *     The modified_date
     */
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


    /**
     * 
     * @return
     *     The childId
     */
    public List<Integer> getChildId() {
        return childId;
    }

    /**
     * 
     * @param childId
     *     The child_id
     */
    public void setChildId(List<Integer> childId) {
        this.childId = childId;
    }

    /**
     * 
     * @return
     *     The orderLevel
     */
    public String getOrderLevel() {
        return orderLevel;
    }

    /**
     * 
     * @param orderLevel
     *     The order_level
     */
    public void setOrderLevel(String orderLevel) {
        this.orderLevel = orderLevel;
    }

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

}
