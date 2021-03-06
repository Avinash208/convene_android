
package org.fwwb.convene.convenecode.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponsesData {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("ResponsesData")
    @Expose
    private List<ResponsesDatum> responsesData = null;

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

    public List<ResponsesDatum> getResponsesData() {
        return responsesData;
    }

    public void setResponsesData(List<ResponsesDatum> responsesData) {
        this.responsesData = responsesData;
    }

}
