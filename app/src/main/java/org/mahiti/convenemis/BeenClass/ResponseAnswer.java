
package org.mahiti.convenemis.BeenClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseAnswer {

    @SerializedName("ResponseANS")
    @Expose
    private List<ResponseAN> responseANS = null;

    public List<ResponseAN> getResponseANS() {
        return responseANS;
    }

    public void setResponseANS(List<ResponseAN> responseANS) {
        this.responseANS = responseANS;
    }

}
