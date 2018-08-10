
package org.assistindia.convene.BeenClass.parentChild;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ParentChildBean {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("page_count")
    @Expose
    private Integer pageCount;
    @SerializedName("records_per_page")
    @Expose
    private Integer recordsPerPage;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("parent_child")
    @Expose
    private List<ParentChild> parentChild = new ArrayList<>();

    /**
     *
     * @return
     *     The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     *     The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     *     The totalCount
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     *
     * @param totalCount
     *     The total_count
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     *
     * @return
     *     The pageCount
     */
    public Integer getPageCount() {
        return pageCount;
    }

    /**
     *
     * @param pageCount
     *     The page_count
     */
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    /**
     *
     * @return
     *     The recordsPerPage
     */
    public Integer getRecordsPerPage() {
        return recordsPerPage;
    }

    /**
     *
     * @param recordsPerPage
     *     The records_per_page
     */
    public void setRecordsPerPage(Integer recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    /**
     *
     * @return
     *     The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     *     The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     *     The parentChild
     */
    public List<ParentChild> getParentChild() {
        return parentChild;
    }

    /**
     *
     * @param parentChild
     *     The parent_child
     */
    public void setParentChild(List<ParentChild> parentChild) {
        this.parentChild = parentChild;
    }

}
