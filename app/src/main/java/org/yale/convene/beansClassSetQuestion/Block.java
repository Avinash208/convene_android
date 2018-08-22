
package org.yale.convene.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Block {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("block_code")
    @Expose
    private String blockCode;
    @SerializedName("block_name")
    @Expose
    private String blockName;
    @SerializedName("language_id")
    @Expose
    private Integer languageId;
    @SerializedName("survey_id")
    @Expose
    private Integer surveyId;
    @SerializedName("block_order")
    @Expose
    private Integer blockOrder;
    @SerializedName("active")
    @Expose
    private Integer active;
    @SerializedName("updated_time")
    @Expose
    private String updatedTime;
    @SerializedName("extra_column1")
    @Expose
    private String extraColumn1;
    @SerializedName("extra_column2")
    @Expose
    private Integer extraColumn2;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public Integer getBlockOrder() {
        return blockOrder;
    }

    public void setBlockOrder(Integer blockOrder) {
        this.blockOrder = blockOrder;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getExtraColumn1() {
        return extraColumn1;
    }

    public void setExtraColumn1(String extraColumn1) {
        this.extraColumn1 = extraColumn1;
    }

    public Integer getExtraColumn2() {
        return extraColumn2;
    }

    public void setExtraColumn2(Integer extraColumn2) {
        this.extraColumn2 = extraColumn2;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }
}
