package org.mahiti.convenemis.BeenClass;


public class AssesmentBean
{
    int qid;
    String assessment;
    int assessmentId;
    int mandatory;
    String qtype;
    String groupValidation;

  public AssesmentBean(){

  }

    public int getMandatory() {
        return mandatory;
    }

    public void setMandatory(int mandatory) {
        this.mandatory = mandatory;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getQtype() {
        return qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getGroupValidation() {
        return groupValidation;
    }

    public void setGroupValidation(String groupValidation) {
        this.groupValidation = groupValidation;
    }
}
