
package org.yale.convene.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegionalLanguage {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("regional_language")
    @Expose
    private List<RegionalLanguage_> regionalLanguage = null;

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

    public List<RegionalLanguage_> getRegionalLanguage() {
        return regionalLanguage;
    }

    public void setRegionalLanguage(List<RegionalLanguage_> regionalLanguage) {
        this.regionalLanguage = regionalLanguage;
    }

}
