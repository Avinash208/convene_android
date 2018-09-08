
package org.fwwb.convene.convenecode.BeenClass;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityRuleSet_ {

    @SerializedName("questions_to_display")
    @Expose
    private List<Integer> questionsToDisplay = null;
    @SerializedName("rulesets")
    @Expose
    private List<Ruleset> rulesets = null;

    public List<Integer> getQuestionsToDisplay() {
        return questionsToDisplay;
    }

    public void setQuestionsToDisplay(List<Integer> questionsToDisplay) {
        this.questionsToDisplay = questionsToDisplay;
    }

    public List<Ruleset> getRulesets() {
        return rulesets;
    }

    public void setRulesets(List<Ruleset> rulesets) {
        this.rulesets = rulesets;
    }

}
