package org.fwwb.convene.fwwbcode.presentor.attendancepresentor;

import org.fwwb.convene.fwwbcode.modelclasses.MembersBean;

import java.util.List;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public interface MemberAttendanceListener {

    void memberList(List<MembersBean> membersBeanList);
}
