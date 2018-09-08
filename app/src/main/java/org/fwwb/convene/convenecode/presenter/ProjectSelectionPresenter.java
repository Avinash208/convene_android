package org.fwwb.convene.convenecode.presenter;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Spinner;

import org.fwwb.convene.convenecode.BeenClass.ProjectList;
import org.fwwb.convene.convenecode.BeenClass.parentChild.SurveyDetail;
import org.fwwb.convene.convenecode.ProjectSelectionActivity;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.view.ProjectSelectionActivityScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahiti on 11/04/18.
 */

public class ProjectSelectionPresenter implements ProjectSelectionActivityPresenter{
    ProjectSelectionActivityScene projectSelectionActivityScene;
    ProjectSelectionActivity activity;
    ExternalDbOpenHelper dbhelper;
    public ProjectSelectionPresenter(ProjectSelectionActivity projectSelectionActivity) {
        this.projectSelectionActivityScene=(ProjectSelectionActivity) projectSelectionActivity;
        this.activity=projectSelectionActivity;
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        dbhelper = ExternalDbOpenHelper.getInstance(activity, defaultPreferences.getString(Constants.DBNAME, ""), defaultPreferences.getString("inv_id", ""));


    }

    public void initModule() {
        new aSyncProjectList(dbhelper).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void performLocationFilterFunctionality(Spinner project) {
        ProjectList getProjectName= (ProjectList) project.getSelectedItem();
        if (!getProjectName.getProjectName().isEmpty() && !getProjectName.getProjectName().equalsIgnoreCase("Select")){
            MyThread myThread=   new MyThread(getProjectName);
            myThread.run();


        }else {
            ToastUtils.displayToast("Please select project",activity);
        }
    }
    public  void performBeneficiaryFilterFunctionality(Spinner project){
        ProjectList getProjectName= (ProjectList) project.getSelectedItem();
        if (!getProjectName.getProjectName().isEmpty() && !getProjectName.getProjectName().equalsIgnoreCase("Select")){
            new myBeneficiaryListingThread(getProjectName).run();
        }else {
            ToastUtils.displayToast("Please select project",activity);
        }
    }


    @Override
    public void getProjectList(List<ProjectList> projectList) {

    }

    private class aSyncProjectList extends AsyncTask{
        ExternalDbOpenHelper dbhelper;
        List<ProjectList> getList= new ArrayList<>();
        private aSyncProjectList(ExternalDbOpenHelper dbhelper) {
            this.dbhelper=dbhelper;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            getList= dbhelper.getAllProjectList(dbhelper);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (!getList.isEmpty())
                projectSelectionActivityScene.getProjectList(getList);
        }
    }

    private class MyThread implements Runnable {
        ProjectList getProjectName;
        private MyThread(ProjectList getProjectName) {
            this.getProjectName=getProjectName;
        }

        @Override
        public void run() {
            List<SurveyDetail> getLocationBasedActivity= dbhelper.getLocationActivityBasedProject(getProjectName.getProjectId(),dbhelper,200);
            projectSelectionActivityScene.getLocationBasedActivity(getLocationBasedActivity);
        }
    }

    private class myBeneficiaryListingThread implements Runnable {
        ProjectList getProjectName;
        public myBeneficiaryListingThread(ProjectList getProjectName) {
            this.getProjectName=getProjectName;
        }

        @Override
        public void run() {
            List<SurveyDetail> getLocationBasedActivity= dbhelper.getLocationActivityBasedProject(getProjectName.getProjectId(),dbhelper,201);
            projectSelectionActivityScene.getLocationBasedActivity(getLocationBasedActivity);
        }
    }
}
