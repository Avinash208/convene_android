package org.assistindia.convene.view;

import org.assistindia.convene.BeenClass.ProjectList;
import org.assistindia.convene.BeenClass.parentChild.SurveyDetail;

import java.util.List;

/**
 * Created by mahiti on 17/07/18.
 */
public interface ProjectSelectionActivityScene {

    void getProjectList(List<ProjectList> projectList);
    void getLocationBasedActivity(List<SurveyDetail> locationBasedProjectActivity);
}
