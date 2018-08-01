package org.mahiti.convenemis.BeenClass.parentChild;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.mahiti.convenemis.beansClassSetQuestion.ParentLink;

import java.util.ArrayList;
import java.util.List;

public class SurveyDetail implements Parcelable{

    @SerializedName("summary_qid")
    @Expose
    private String summaryQid;
    @SerializedName("b_config")
    @Expose
    private Integer bConfig;
    @SerializedName("clusterVersion")
    @Expose
    private Integer clusterVersion;
    @SerializedName("reasonDisagree")
    @Expose
    private Integer reasonDisagree;
    @SerializedName("pLimit")
    @Expose
    private Integer pLimit;
    @SerializedName("order_levels")
    @Expose
    private String orderLevels;
    @SerializedName("piriodicity")
    @Expose
    private String piriodicity;
    @SerializedName("survey_name")
    @Expose
    private String surveyName;
    @SerializedName("beneficiary_ids")
    @Expose
    private String beneficiaryIds;
    @SerializedName("pFeature")
    @Expose
    private Integer pFeature;
    @SerializedName("vn")
    @Expose
    private String vn;
    @SerializedName("facility_type")
    @Expose
    private String facilityType;
    @SerializedName("labels")
    @Expose
    private String labels;
    @SerializedName("pcode")
    @Expose
    private String pcode;
    @SerializedName("rule_engine")
    @Expose
    private List<RuleEngine> ruleEngine = new ArrayList<RuleEngine>();
    @SerializedName("beneficiary_type")
    @Expose
    private String beneficiaryType;
    @SerializedName("expiry_age")
    @Expose
    private Integer expiryAge;
    @SerializedName("facility_ids")
    @Expose
    private String facilityIds;


    @SerializedName("piriodicity_flag")
    @Expose
    private String piriodicityFlag;

    @SerializedName("category_name")
    @Expose
    private String categoryName;

    @SerializedName("category_id")
    @Expose
    private String categoryId;

    @SerializedName("constraints")
    @Expose
    private String constraints;

    @SerializedName("survey_id")
    @Expose
    private Integer surveyId;

    @SerializedName("survey_type")
    @Expose
    private Integer surveyType;


    @SerializedName("q_config")
    @Expose
    private Integer qConfig;
    @SerializedName("linkages")
    @Expose
    private List<LinkagesList> linkagesDetails = null;

    @SerializedName("parent_link")
    @Expose
    private List<ParentLink> parentLink = null;

    private  Integer ProjectID;
    private  String ProjectName;
    private  String level1;
    private  String level2;
    private  String level3;
    private  String level4;
    private  String level5;
    private  String level6;
    private  String level7;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public SurveyDetail() {
    }

    public SurveyDetail(Parcel in) {
        summaryQid = in.readString();
        if (in.readByte() == 0) {
            bConfig = null;
        } else {
            bConfig = in.readInt();
        }
        if (in.readByte() == 0) {
            clusterVersion = null;
        } else {
            clusterVersion = in.readInt();
        }
        if (in.readByte() == 0) {
            reasonDisagree = null;
        } else {
            reasonDisagree = in.readInt();
        }
        if (in.readByte() == 0) {
            pLimit = null;
        } else {
            pLimit = in.readInt();
        }
        orderLevels = in.readString();
        piriodicity = in.readString();
        surveyName = in.readString();
        beneficiaryIds = in.readString();
        if (in.readByte() == 0) {
            pFeature = null;
        } else {
            pFeature = in.readInt();
        }
        vn = in.readString();
        facilityType = in.readString();
        labels = in.readString();
        pcode = in.readString();
        beneficiaryType = in.readString();
        if (in.readByte() == 0) {
            expiryAge = null;
        } else {
            expiryAge = in.readInt();
        }
        facilityIds = in.readString();
        piriodicityFlag = in.readString();
        if (in.readByte() == 0) {
            surveyId = null;
        } else {
            surveyId = in.readInt();
        }
        if (in.readByte() == 0) {
            qConfig = null;
        } else {
            qConfig = in.readInt();
        }
    }

    public static final Creator<SurveyDetail> CREATOR = new Creator<SurveyDetail>() {
        @Override
        public SurveyDetail createFromParcel(Parcel in) {
            return new SurveyDetail(in);
        }

        @Override
        public SurveyDetail[] newArray(int size) {
            return new SurveyDetail[size];
        }
    };

    public SurveyDetail(String surveyName, int pFuture, int pLimit, int periodicity, String labels, String vn, String beneficiaryType, String beneficiaryIds, int bConfig, int reasonDisagree, String orderLevels, int surveyId, int qConfig, String summaryQid, List<RuleEngine> ruleEngineValue, String facilityType, String facilityIds, String periodicityFlag, String constraints) {
        this.surveyName=surveyName;
        this.pFeature=pFuture;
        this.pLimit=pLimit;
        this.piriodicity= String.valueOf(periodicity);
        this.labels=labels;
        this.vn=vn;
        this.beneficiaryType=beneficiaryType;
        this.beneficiaryIds=beneficiaryIds;
        this.bConfig=bConfig;
        this.reasonDisagree=reasonDisagree;
        this.orderLevels=orderLevels;
        this.surveyId=surveyId;
        this.qConfig=qConfig;
        this.summaryQid=summaryQid;
        this.ruleEngine=ruleEngineValue;
        this.facilityType=facilityType;
        this.facilityIds=facilityIds;
        this.piriodicityFlag=periodicityFlag;
        this.constraints=constraints;
    }

    public String getSummaryQid() {
        return summaryQid;
    }

    public void setSummaryQid(String summaryQid) {
        this.summaryQid = summaryQid;
    }

    public Integer getBConfig() {
        return bConfig;
    }

    public void setBConfig(Integer bConfig) {
        this.bConfig = bConfig;
    }

    public Integer getClusterVersion() {
        return clusterVersion;
    }

    public void setClusterVersion(Integer clusterVersion) {
        this.clusterVersion = clusterVersion;
    }

    public Integer getReasonDisagree() {
        return reasonDisagree;
    }

    public void setReasonDisagree(Integer reasonDisagree) {
        this.reasonDisagree = reasonDisagree;
    }

    public Integer getPLimit() {
        return pLimit;
    }

    public void setPLimit(Integer pLimit) {
        this.pLimit = pLimit;
    }

    public String getOrderLevels() {
        return orderLevels;
    }

    public void setOrderLevels(String orderLevels) {
        this.orderLevels = orderLevels;
    }

    public String getPiriodicity() {
        return piriodicity;
    }

    public void setPiriodicity(String piriodicity) {
        this.piriodicity = piriodicity;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getBeneficiaryIds() {
        return beneficiaryIds;
    }

    public void setBeneficiaryIds(String beneficiaryIds) {
        this.beneficiaryIds = beneficiaryIds;
    }

    public Integer getPFeature() {
        return pFeature;
    }

    public void setPFeature(Integer pFeature) {
        this.pFeature = pFeature;
    }

    public String getVn() {
        return vn;
    }

    public void setVn(String vn) {
        this.vn = vn;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public List<RuleEngine> getRuleEngine() {
        return ruleEngine;
    }

    public void setRuleEngine(List<RuleEngine> ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public String getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(String beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    public Integer getExpiryAge() {
        return expiryAge;
    }

    public void setExpiryAge(Integer expiryAge) {
        this.expiryAge = expiryAge;
    }

    public String getFacilityIds() {
        return facilityIds;
    }

    public void setFacilityIds(String facilityIds) {
        this.facilityIds = facilityIds;
    }

    public String getPiriodicityFlag() {
        return piriodicityFlag;
    }

    public void setPiriodicityFlag(String piriodicityFlag) {
        this.piriodicityFlag = piriodicityFlag;
    }

    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public Integer getQConfig() {
        return qConfig;
    }

    public void setQConfig(Integer qConfig) {
        this.qConfig = qConfig;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(summaryQid);
        if (bConfig == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(bConfig);
        }
        if (clusterVersion == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(clusterVersion);
        }
        if (reasonDisagree == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(reasonDisagree);
        }
        if (pLimit == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(pLimit);
        }
        parcel.writeString(orderLevels);
        parcel.writeString(piriodicity);
        parcel.writeString(surveyName);
        parcel.writeString(beneficiaryIds);
        if (pFeature == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(pFeature);
        }
        parcel.writeString(vn);
        parcel.writeString(facilityType);
        parcel.writeString(labels);
        parcel.writeString(pcode);
        parcel.writeString(beneficiaryType);
        if (expiryAge == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(expiryAge);
        }
        parcel.writeString(facilityIds);
        parcel.writeString(piriodicityFlag);
        if (surveyId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(surveyId);
        }
        if (qConfig == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(qConfig);
        }
    }

    public Integer getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(Integer surveyType) {
        this.surveyType = surveyType;
    }

    public List<LinkagesList> getLinkagesDetails() {

        return linkagesDetails;
    }

    public void setLinkagesDetails(List<LinkagesList> linkagesDetails) {
        this.linkagesDetails = linkagesDetails;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }


    public Integer getProjectID() {
        return ProjectID;
    }

    public void setProjectID(Integer projectID) {
        ProjectID = projectID;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getLevel1() {
        return level1;
    }

    public void setLevel1(String level1) {
        this.level1 = level1;
    }

    public String getLevel2() {
        return level2;
    }

    public void setLevel2(String level2) {
        this.level2 = level2;
    }

    public String getLevel3() {
        return level3;
    }

    public void setLevel3(String level3) {
        this.level3 = level3;
    }

    public String getLevel4() {
        return level4;
    }

    public void setLevel4(String level4) {
        this.level4 = level4;
    }

    public String getLevel5() {
        return level5;
    }

    public void setLevel5(String level5) {
        this.level5 = level5;
    }

    public String getLevel6() {
        return level6;
    }

    public void setLevel6(String level6) {
        this.level6 = level6;
    }

    public String getLevel7() {
        return level7;
    }

    public void setLevel7(String level7) {
        this.level7 = level7;
    }

    public List<ParentLink> getParentLink() {
        return parentLink;
    }

    public void setParentLink(List<ParentLink> parentLink) {
        this.parentLink = parentLink;
    }
}