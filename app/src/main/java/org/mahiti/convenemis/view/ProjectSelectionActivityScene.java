package org.mahiti.convenemis.view;

import org.mahiti.convenemis.BeenClass.Project;
import org.mahiti.convenemis.BeenClass.ProjectList;
import org.mahiti.convenemis.BeenClass.parentChild.SurveyDetail;

import java.util.List;

/**
 * Created by mahiti on 17/07/18.
 */
public interface ProjectSelectionActivityScene {

    void getProjectList(List<ProjectList> projectList);
    void getLocationBasedActivity(List<SurveyDetail> locationBasedProjectActivity);
}
