package org.fwwb.convene.fwwbcode.presentor.attendancepresentor;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public interface SaveAttendanceListener {
    void savedAttendance(boolean isSaved,String memberUuid,String surveyUuid);
    void savedAttendanceSurvey(boolean isSaved,String memberUuid,String surveyUuid);
}
