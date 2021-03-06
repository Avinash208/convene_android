
package org.fwwb.convene.convenecode.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdatedTablesBeen {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("updatedTables")
    @Expose
    private UpdatedTables updatedTables;

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

    public UpdatedTables getUpdatedTables() {
        return updatedTables;
    }

    public void setUpdatedTables(UpdatedTables updatedTables) {
        this.updatedTables = updatedTables;
    }

}
