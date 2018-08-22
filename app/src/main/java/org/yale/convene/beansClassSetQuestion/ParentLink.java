
package org.yale.convene.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParentLink {

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("summary_qid")
    @Expose
    private String summary_qid;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("form_type_id")
    @Expose
    private Integer formTypeId;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFormTypeId() {
        return formTypeId;
    }

    public void setFormTypeId(Integer formTypeId) {
        this.formTypeId = formTypeId;
    }

    public String getSummary_qid() {
        return summary_qid;
    }

    public void setSummary_qid(String summary_qid) {
        this.summary_qid = summary_qid;
    }
}
