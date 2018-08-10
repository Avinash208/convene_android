
package org.assistindia.convene.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageOption {

    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @SerializedName("option_pid")
    @Expose
    private Integer optionPid;
    @SerializedName("extra_column2")
    @Expose
    private Integer extraColumn2;
    @SerializedName("option_text")
    @Expose
    private String optionText;
    @SerializedName("validation")
    @Expose
    private String validation;
    @SerializedName("extra_column1")
    @Expose
    private String extraColumn1;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("question_pid")
    @Expose
    private Integer questionPid;
    @SerializedName("language_id")
    @Expose
    private Integer languageId;

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getOptionPid() {
        return optionPid;
    }

    public void setOptionPid(Integer optionPid) {
        this.optionPid = optionPid;
    }

    public Integer getExtraColumn2() {
        return extraColumn2;
    }

    public void setExtraColumn2(Integer extraColumn2) {
        this.extraColumn2 = extraColumn2;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getExtraColumn1() {
        return extraColumn1;
    }

    public void setExtraColumn1(String extraColumn1) {
        this.extraColumn1 = extraColumn1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionPid() {
        return questionPid;
    }

    public void setQuestionPid(Integer questionPid) {
        this.questionPid = questionPid;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

}
