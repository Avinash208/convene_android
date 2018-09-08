package org.fwwb.convene.convenecode.view;

import org.fwwb.convene.convenecode.BeenClass.ProjectList;
import org.fwwb.convene.convenecode.BeenClass.parentChild.SurveyDetail;

import java.util.List;

/**
 * Created by mahiti on 17/07/18.
 */
public interface ProjectSelectionActivityScene {

    void getProjectList(List<ProjectList> projectList);
    void getLocationBasedActivity(List<SurveyDetail> locationBasedProjectActivity);
}
