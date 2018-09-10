package org.fwwb.convene.fwwbcode.presentor.attendancepresentor;

import android.os.AsyncTask;

import org.fwwb.convene.convenecode.BeenClass.AnswersPage;
import org.fwwb.convene.convenecode.BeenClass.childLink;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.fwwbcode.modelclasses.MembersBean;
import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;
import org.fwwb.convene.fwwbcode.presentor.taskpresentor.TaskListHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class MemberAttendanceAsyncTask extends AsyncTask<String,Integer,String> {

    private final ArrayList<childLink> getChildUUids;
    private final String nameQids;
    private final TaskItemBean taskItemBean;
    private Map<String,String> memberAttendance = new HashMap<>();
    private List<MembersBean> membersBeanList = new ArrayList<>();
    private DBHandler dbHandler;
    private MemberAttendanceListener memberAttendanceListener;
    private List<AnswersPage> options;

    public MemberAttendanceAsyncTask(ArrayList<childLink> getChildUUids, String nameQids, TaskItemBean taskItemBean, DBHandler dbHandler, MemberAttendanceListener memberAttendanceListener, List<AnswersPage> options){

        this.getChildUUids = getChildUUids;
        this.nameQids = nameQids;
        this.taskItemBean = taskItemBean;
        this.dbHandler = dbHandler;
        this.memberAttendanceListener = memberAttendanceListener;
        this.options = options;
    }
    @Override
    protected String doInBackground(String... strings) {
        setUserData();
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        memberAttendanceListener.memberList(membersBeanList);
    }

    private void setUserData() {
        memberAttendance = dbHandler.getAllAttendanceOfTraining(taskItemBean.getTrainingUuid(),taskItemBean.getBatchUuid());
        for (int i = 0; i < getChildUUids.size(); i++) {
            MembersBean membersBean =  new MembersBean();
            String name = TaskListHelper.getNameFromResponce(getChildUUids.get(i).getChild_form_id(), nameQids,0,dbHandler);
            membersBean.setMemberName(name);
            membersBean.setMemberUuid(getChildUUids.get(i).getChild_form_id());
            membersBean.setTrainingUuid(taskItemBean.getTrainingUuid());
            String attendanceSurveyUUid = memberAttendance.get(getChildUUids.get(i).getChild_form_id());
            membersBean.setAttendanceSurveyUuid("");
            if (attendanceSurveyUUid != null)
            {
                membersBean.setAttendanceSurveyUuid(attendanceSurveyUUid);
                String option = dbHandler.getResponseChoise(attendanceSurveyUUid);
                if (options.get(0).getAnswerCode().equals(option))
                    membersBean.setMemberAttendance(1);
                if (options.get(1).getAnswerCode().equals(option))
                    membersBean.setMemberAttendance(0);

            }
            membersBeanList.add(membersBean);


        }


    }
}
