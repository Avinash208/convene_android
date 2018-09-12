package org.fwwb.convene.fwwbcode.modelclasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class TaskItemBean implements Parcelable {

    private String batchName;
    private String trainingName;
    private String TrainingLocation;
    private String trainingDate;
    private String batchUuid;
    private String taskUuid;
    private String trainingUuid;
    private int trainingStatus;
    private int batchParticipants;
    private int beneficiaryType;
    private int trainingHour;
    private int surveyStatus;

    public TaskItemBean()
    {}
    public TaskItemBean(String batchName, String trainingName, String trainingLocation, String trainingDate, String batchUuid, int trainingStatus, int batchParticipants) {
        this.batchName = batchName;
        this.trainingName = trainingName;
        TrainingLocation = trainingLocation;
        this.trainingDate = trainingDate;
        this.batchUuid = batchUuid;
        this.trainingStatus = trainingStatus;
        this.batchParticipants = batchParticipants;
    }

    protected TaskItemBean(Parcel in) {
        batchName = in.readString();
        trainingName = in.readString();
        TrainingLocation = in.readString();
        trainingDate = in.readString();
        batchUuid = in.readString();
        taskUuid = in.readString();
        trainingUuid = in.readString();
        trainingStatus = in.readInt();
        batchParticipants = in.readInt();
        beneficiaryType = in.readInt();
        trainingHour = in.readInt();
    }

    public static final Creator<TaskItemBean> CREATOR = new Creator<TaskItemBean>() {
        @Override
        public TaskItemBean createFromParcel(Parcel in) {
            return new TaskItemBean(in);
        }

        @Override
        public TaskItemBean[] newArray(int size) {
            return new TaskItemBean[size];
        }
    };

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getTrainingLocation() {
        return TrainingLocation;
    }

    public void setTrainingLocation(String trainingLocation) {
        TrainingLocation = trainingLocation;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public String getBatchUuid() {
        return batchUuid;
    }

    public void setBatchUuid(String batchUuid) {
        this.batchUuid = batchUuid;
    }

    public int getTrainingStatus() {
        return trainingStatus;
    }

    public void setTrainingStatus(int trainingStatus) {
        this.trainingStatus = trainingStatus;
    }

    @Override
    public String toString() {
        return trainingUuid;
    }

    public int getBatchParticipants() {
        return batchParticipants;
    }

    public void setBatchParticipants(int batchParticipants) {
        this.batchParticipants = batchParticipants;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public String getTrainingUuid() {
        return trainingUuid;
    }

    public void setTrainingUuid(String trainingUuid) {
        this.trainingUuid = trainingUuid;
    }


    public int getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(int beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(batchName);
        dest.writeString(trainingName);
        dest.writeString(TrainingLocation);
        dest.writeString(trainingDate);
        dest.writeString(batchUuid);
        dest.writeString(taskUuid);
        dest.writeString(trainingUuid);
        dest.writeInt(trainingStatus);
        dest.writeInt(batchParticipants);
        dest.writeInt(beneficiaryType);
        dest.writeInt(trainingHour);
    }

    public int getTrainingHour() {
        return trainingHour;
    }

    public void setTrainingHour(int trainingHour) {
        this.trainingHour = trainingHour;
    }

    public int getSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(int surveyStatus) {
        this.surveyStatus = surveyStatus;
    }
}
