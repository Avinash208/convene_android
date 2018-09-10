
package org.fwwb.convene.convenecode.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LanguageBlockBeen {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("page_count")
    @Expose
    private Integer pageCount;
    @SerializedName("records_per_page")
    @Expose
    private Integer recordsPerPage;
    @SerializedName("LanguageBlock")
    @Expose
    private List<LanguageBlock> languageBlock = new ArrayList<>();


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getRecordsPerPage() {
        return recordsPerPage;
    }

    public void setRecordsPerPage(Integer recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public List<LanguageBlock> getLanguageBlock() {
        return languageBlock;
    }

    public void setLanguageBlock(List<LanguageBlock> languageBlock) {
        this.languageBlock = languageBlock;
    }

}
