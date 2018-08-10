
package org.assistindia.convene.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BeneficiaryLinkage {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("linkages")
    @Expose
    private List<Linkage> linkages = null;
    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Linkage> getLinkages() {
        return linkages;
    }

    public void setLinkages(List<Linkage> linkages) {
        this.linkages = linkages;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
