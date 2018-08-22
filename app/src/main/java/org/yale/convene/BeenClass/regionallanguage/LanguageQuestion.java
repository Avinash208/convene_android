
package org.yale.convene.BeenClass.regionallanguage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageQuestion {

    @SerializedName("help_text")
    @Expose
    private String helpText;
    @SerializedName("question_text")
    @Expose
    private String questionText;
    @SerializedName("instruction")
    @Expose
    private String instruction;
    @SerializedName("extra_column2")
    @Expose
    private Integer extraColumn2;
    @SerializedName("extra_column1")
    @Expose
    private String extraColumn1;
    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("question_pid")
    @Expose
    private Integer questionPid;
    @SerializedName("language_id")
    @Expose
    private Integer languageId;

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Integer getExtraColumn2() {
        return extraColumn2;
    }

    public void setExtraColumn2(Integer extraColumn2) {
        this.extraColumn2 = extraColumn2;
    }

    public String getExtraColumn1() {
        return extraColumn1;
    }

    public void setExtraColumn1(String extraColumn1) {
        this.extraColumn1 = extraColumn1;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
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
