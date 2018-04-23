
package org.mahiti.convenemis.BeenClass.beneficiary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetBeneficiaryDetails {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private List<Datum> data = new ArrayList<>();

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}
