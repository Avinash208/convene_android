package org.fwwb.convene.fwwbcode.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.database.Utilities;
import org.fwwb.convene.convenecode.utils.StartSurvey;
import org.fwwb.convene.fwwbcode.FwwbConstants;
import org.fwwb.convene.fwwbcode.fragments.CurrentTaskListFragment;
import org.fwwb.convene.fwwbcode.fragments.RecentTaskListFragment;
import org.fwwb.convene.fwwbcode.fragments.TrainingFragmentPager;
import org.fwwb.convene.fwwbcode.fragments.UpcomingTaskListFragment;
import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;
import org.fwwb.convene.fwwbcode.presentor.taskpresentor.TaskListListeners;
import org.fwwb.convene.fwwbcode.presentor.taskpresentor.TaskListPresenters;

import java.util.ArrayList;
import java.util.List;

import static org.fwwb.convene.convenecode.utils.Constants.SURVEY_ID;

public class TaskSelectionListingActivity extends AppCompatActivity implements TaskListListeners {


    TrainingFragmentPager mTrainingFragmentPager;
    List<Fragment> fragmentList = new ArrayList<>();
    CurrentTaskListFragment currentTaskListFragment;
    UpcomingTaskListFragment upcomingTaskListFragment;
    RecentTaskListFragment recentTaskListFragment;

    public List<TaskItemBean> currentTaskItemBeanList = new ArrayList<>();

    private TaskListPresenters presenters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_selection_listing);
        TextView tittle = findViewById(R.id.toolbarTitle);
        tittle.setText("Task");
        findViewById(R.id.backPress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


        setFragments();
        mTrainingFragmentPager = new TrainingFragmentPager(getSupportFragmentManager(),fragmentList);
        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mTrainingFragmentPager);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));



        FloatingActionButton fab = findViewById(R.id.addTaskFab);
        fab.setOnClickListener(view -> getTraining());
        initPresentor();

    }

    private void initPresentor() {
//     presenters = new TaskListPresenters(TaskSelectionListingActivity.this,this);
    }

    private void getTraining() {
        SharedPreferences prefs = getSharedPreferences(FwwbConstants.MY_PREFS_NAME, MODE_PRIVATE);
        Utilities.setSurveyStatus(prefs, "new");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SURVEY_ID, prefs.getInt("TraningModuleSurvey",188));
        editor.putString("Survey_tittle", "Task");
        editor.apply();
        new StartSurvey(TaskSelectionListingActivity.this, TaskSelectionListingActivity.this, prefs.getInt(SURVEY_ID, 0), prefs.getInt(SURVEY_ID, 0), "", "", "", "", "", null, "", "").execute(); //Chaned by guru removed "village Name"

    }

    private void setFragments() {
        currentTaskListFragment = CurrentTaskListFragment.newInstance();
        upcomingTaskListFragment = UpcomingTaskListFragment.newInstance();
        recentTaskListFragment = RecentTaskListFragment.newInstance();
        fragmentList.add(currentTaskListFragment);
        fragmentList.add(upcomingTaskListFragment);
        fragmentList.add(recentTaskListFragment);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_selection_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






    @Override
    public void currentTaskList(List<TaskItemBean> currentList) {
        currentTaskListFragment.setTaskList(currentList);
    }

    @Override
    public void upcomingTaskList(List<TaskItemBean> upcomingList) {
        upcomingTaskListFragment.setTaskList(upcomingList);
    }

    @Override
    public void recentTaskList(List<TaskItemBean> recentList) {

    }

    @Override
    public void singleTaskItem(TaskItemBean singleTask) {

    }
}
