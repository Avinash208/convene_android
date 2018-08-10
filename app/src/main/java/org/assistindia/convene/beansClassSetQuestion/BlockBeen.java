
package org.assistindia.convene.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BlockBeen {

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
    @SerializedName("Block")
    @Expose
    private List<Block> block = new ArrayList<>();


    public String getMessage() {
        return message;
    }

    public List<Block> getBlock() {
        return block;
    }

}
