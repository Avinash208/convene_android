package org.mahiti.convenemis.BeenClass;

/**
 * Created by mahiti on 7/1/18.
 */

public class QuestionAnswer {
    private String questionText;
    private String answerText;
    private String selectedChildUUID;

    private int child_form_primaryid;
    private int clusterId;
    private int locationLevel;
    private int parentId;
    private  int isActive;
    private  int relationId;


    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public int getLocationLevel() {
        return locationLevel;
    }

    public void setLocationLevel(int locationLevel) {
        this.locationLevel = locationLevel;
    }

    public QuestionAnswer(){

    }
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getSelectedChildUUID() {
        return selectedChildUUID;
    }

    public void setSelectedChildUUID(String selectedChildUUID) {
        this.selectedChildUUID = selectedChildUUID;
    }

    public int getChild_form_primaryid() {
        return child_form_primaryid;
    }

    public void setChild_form_primaryid(int child_form_primaryid) {
        this.child_form_primaryid = child_form_primaryid;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }
}
