package org.yale.convene.view;

import org.yale.convene.BeenClass.ProjectList;
import org.yale.convene.BeenClass.parentChild.SurveyDetail;

import java.util.List;

/**
 * Created by mahiti on 17/07/18.
 */
public interface ProjectSelectionActivityScene {

    void getProjectList(List<ProjectList> projectList);
    void getLocationBasedActivity(List<SurveyDetail> locationBasedProjectActivity);
}
