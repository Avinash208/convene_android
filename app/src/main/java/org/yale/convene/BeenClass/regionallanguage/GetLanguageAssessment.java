
package org.yale.convene.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetLanguageAssessment {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("LanguageAssessment")
    @Expose

    private List<LanguageAssessment> languageAssessment = new ArrayList<>();

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


    public List<LanguageAssessment> getLanguageAssessment() {
        return languageAssessment;
    }

    public void setLanguageQuestion(List<LanguageAssessment> languageAssessment) {
        this.languageAssessment = languageAssessment;
    }
}
