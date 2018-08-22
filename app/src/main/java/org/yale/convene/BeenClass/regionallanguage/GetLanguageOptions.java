
package org.yale.convene.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetLanguageOptions {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("LanguageOptions")
    @Expose
    private List<LanguageOption> languageOptions = new ArrayList<>();

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

    public List<LanguageOption> getLanguageOptions() {
        return languageOptions;
    }

    public void setLanguageOptions(List<LanguageOption> languageOptions) {
        this.languageOptions = languageOptions;
    }

}
