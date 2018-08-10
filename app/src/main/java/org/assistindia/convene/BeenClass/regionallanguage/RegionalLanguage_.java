
package org.assistindia.convene.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegionalLanguage_ {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("language_code")
    @Expose
    private Integer languageCode;
    @SerializedName("language_name")
    @Expose
    private String languageName;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("updated_date")
    @Expose
    private String updatedDated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(Integer languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getUpdatedDated() {
        return updatedDated;
    }

    public void setUpdatedDated(String updatedDated) {
        this.updatedDated = updatedDated;
    }

}
