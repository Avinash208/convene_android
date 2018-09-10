package org.fwwb.convene.fwwbcode.modelclasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class MembersBean implements Parcelable {

    private String memberUuid;//Member uuid
    private String memberName;//Member name
    private String trainingUuid;//tarining UUId
    private String attendanceSurveyUuid;//aattendance survey(DCF) uuid
    private int memberAttendance;//member attended or not
    private boolean isChangeable;//

    public MembersBean(String memberUuid, String memberName, String trainingUuid, String attendanceSurveyUuid, int memberAttendance, boolean isChangeable) {
        this.memberUuid = memberUuid;
        this.memberName = memberName;
        this.trainingUuid = trainingUuid;
        this.attendanceSurveyUuid = attendanceSurveyUuid;
        this.memberAttendance = memberAttendance;
        this.isChangeable = isChangeable;
    }

    private MembersBean(Parcel in) {
        memberUuid = in.readString();
        memberName = in.readString();
        trainingUuid = in.readString();
        attendanceSurveyUuid = in.readString();
        memberAttendance = in.readInt();
        isChangeable = in.readByte() != 0;
    }

    public static final Creator<MembersBean> CREATOR = new Creator<MembersBean>() {
        @Override
        public MembersBean createFromParcel(Parcel in) {
            return new MembersBean(in);
        }

        @Override
        public MembersBean[] newArray(int size) {
            return new MembersBean[size];
        }
    };

    public MembersBean() {

    }

    public String getMemberUuid() {
        return memberUuid;
    }

    public void setMemberUuid(String memberUuid) {
        this.memberUuid = memberUuid;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getTrainingUuid() {
        return trainingUuid;
    }

    public void setTrainingUuid(String trainingUuid) {
        this.trainingUuid = trainingUuid;
    }

    public String getAttendanceSurveyUuid() {
        return attendanceSurveyUuid;
    }

    public void setAttendanceSurveyUuid(String attendanceSurveyUuid) {
        this.attendanceSurveyUuid = attendanceSurveyUuid;
    }

    public int getMemberAttendance() {
        return memberAttendance;
    }

    public void setMemberAttendance(int memberAttendance) {
        this.memberAttendance = memberAttendance;
    }

    public boolean isChangeable() {
        return isChangeable;
    }

    public void setChangeable(boolean changeable) {
        isChangeable = changeable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(memberUuid);
        dest.writeString(memberName);
        dest.writeString(trainingUuid);
        dest.writeString(attendanceSurveyUuid);
        dest.writeInt(memberAttendance);
        dest.writeByte((byte) (isChangeable ? 1 : 0));
    }
}
