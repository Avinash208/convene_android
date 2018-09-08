
package org.fwwb.convene.convenecode.beansClassSetQuestion;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdatedTables {

    @SerializedName("LanguageAssessment")
    @Expose
    private Boolean languageAssessment;
    @SerializedName("SkipRules")
    @Expose
    private Boolean skipRules;
    @SerializedName("LanguageQuestion")
    @Expose
    private Boolean languageQuestion;
    @SerializedName("SkipMandatory")
    @Expose
    private Boolean skipMandatory;
    @SerializedName("Question")
    @Expose
    private Boolean question;
    @SerializedName("LanguageLabels")
    @Expose
    private Boolean languageLabels;
    @SerializedName("LanguageBlock")
    @Expose
    private Boolean languageBlock;
    @SerializedName("LanguageOptions")
    @Expose
    private Boolean languageOptions;
    @SerializedName("Assessment")
    @Expose
    private Boolean assessment;
    @SerializedName("Options")
    @Expose
    private Boolean options;
    @SerializedName("Block")
    @Expose
    private Boolean block;

    public Boolean getLanguageAssessment() {
        return languageAssessment;
    }

    public void setLanguageAssessment(Boolean languageAssessment) {
        this.languageAssessment = languageAssessment;
    }

    public Boolean getSkipRules() {
        return skipRules;
    }

    public void setSkipRules(Boolean skipRules) {
        this.skipRules = skipRules;
    }

    public Boolean getLanguageQuestion() {
        return languageQuestion;
    }

    public void setLanguageQuestion(Boolean languageQuestion) {
        this.languageQuestion = languageQuestion;
    }

    public Boolean getSkipMandatory() {
        return skipMandatory;
    }

    public void setSkipMandatory(Boolean skipMandatory) {
        this.skipMandatory = skipMandatory;
    }

    public Boolean getQuestion() {
        return question;
    }

    public void setQuestion(Boolean question) {
        this.question = question;
    }

    public Boolean getLanguageLabels() {
        return languageLabels;
    }

    public void setLanguageLabels(Boolean languageLabels) {
        this.languageLabels = languageLabels;
    }

    public Boolean getLanguageBlock() {
        return languageBlock;
    }

    public void setLanguageBlock(Boolean languageBlock) {
        this.languageBlock = languageBlock;
    }

    public Boolean getLanguageOptions() {
        return languageOptions;
    }

    public void setLanguageOptions(Boolean languageOptions) {
        this.languageOptions = languageOptions;
    }

    public Boolean getAssessment() {
        return assessment;
    }

    public void setAssessment(Boolean assessment) {
        this.assessment = assessment;
    }

    public Boolean getOptions() {
        return options;
    }

    public void setOptions(Boolean options) {
        this.options = options;
    }

    public Boolean getBlock() {
        return block;
    }

    public void setBlock(Boolean block) {
        this.block = block;
    }

}
