package org.fwwb.convene.fwwbcode.presentor.taskpresentor;

import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;

import java.util.List;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public interface TaskListListeners {


    /**
     * this gives current month task
     * @param currentList this month task of the user
     */
    void currentTaskList(List<TaskItemBean> currentList);
    /**
     * this gives upcoming month task
     * @param upcomingList upcoming month task of the user
     */
    void upcomingTaskList(List<TaskItemBean> upcomingList);
    /**
     * this gives recent month task
     * @param recentList recent month task of the user
     */
    void recentTaskList(List<TaskItemBean> recentList);
    void singleTaskItem(TaskItemBean singleTask);
}
