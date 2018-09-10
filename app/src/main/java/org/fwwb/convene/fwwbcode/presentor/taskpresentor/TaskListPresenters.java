package org.fwwb.convene.fwwbcode.presentor.taskpresentor;

import android.app.Activity;

import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright Mahiti Infotech Pvt Ltd (here after referred to as Mahiti) 2017.
 * All rights reserved. This library cannot be repackaged, included in any other application, reverse engineered, altered or extended without written permission from Mahiti.
 */
public class TaskListPresenters implements TaskListListeners{


    private final Activity activity;
    private final TaskListListeners listListeners;


    /**
     * @param activity
     * @param listListeners
     */
    public TaskListPresenters(Activity activity, TaskListListeners listListeners)
    {

        this.activity = activity;
        this.listListeners = listListeners;
        new TaskListHelper(this.activity,this);
    }


    @Override
    public void currentTaskList(List<TaskItemBean> currentList) {
        listListeners.currentTaskList(currentList);
    }

    @Override
    public void upcomingTaskList(List<TaskItemBean> upcomingList) {
        listListeners.upcomingTaskList(upcomingList);

    }

    @Override
    public void recentTaskList(List<TaskItemBean> recentList) {
        listListeners.recentTaskList(recentList);
    }
}
