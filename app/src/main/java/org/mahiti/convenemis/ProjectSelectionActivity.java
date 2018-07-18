package org.mahiti.convenemis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.mahiti.convenemis.BeenClass.AnswersPage;
import org.mahiti.convenemis.BeenClass.Project;
import org.mahiti.convenemis.BeenClass.ProjectList;
import org.mahiti.convenemis.BeenClass.parentChild.SurveyDetail;
import org.mahiti.convenemis.adapter.spinnercustomadapter.LocationBasedProjectAdapter;
import org.mahiti.convenemis.presenter.ProjectSelectionPresenter;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.view.ProjectSelectionActivityScene;

import java.util.List;

public class ProjectSelectionActivity extends AppCompatActivity implements ProjectSelectionActivityScene, View.OnClickListener {
    ProjectSelectionPresenter projectSelectionActivityPresenter;
    private Spinner project;
    private RecyclerView recyclerListView;
    private LocationBasedProjectAdapter locationBasedProjectAdapter;
    private TextView emptyLabelMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_selection);
        initVariables();
    }

    private void initVariables() {
        TextView toolbarTextView=(TextView) findViewById(R.id.toolbarTitle);
        ImageView searchIcon= (ImageView) findViewById(R.id.imageMenu);
        Button locationBtn= (Button) findViewById(R.id.locationbtn);
        Button beneficiaryBtn= (Button) findViewById(R.id.beneficiarybtn);
        LinearLayout backbtn= (LinearLayout) findViewById(R.id.backPress);
        project= (Spinner) findViewById(R.id.projectspinner);
        recyclerListView= (RecyclerView) findViewById(R.id.projectrecycler);
        emptyLabelMessage= (TextView) findViewById(R.id.emptytextview);
        searchIcon.setVisibility(View.VISIBLE);
        toolbarTextView.setText(R.string.projectandactivity);
        projectSelectionActivityPresenter= new ProjectSelectionPresenter(this);
        locationBtn.setOnClickListener(this);
        beneficiaryBtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        projectSelectionActivityPresenter.initModule();
    }
    @Override
    public void getProjectList(List<ProjectList> projectList) {

        Logger.logD("Project List",projectList.size()+"");
        ArrayAdapter<ProjectList> spinnerArrayAdapter = new ArrayAdapter(this, R.layout.spinner_multi_row_textview, projectList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
        project.setAdapter(spinnerArrayAdapter);

    }

    @Override
    public void getLocationBasedActivity(List<SurveyDetail> locationBasedProjectActivity) {
        if (!locationBasedProjectActivity.isEmpty()){
            emptyLabelMessage.setVisibility(View.GONE);
            recyclerListView.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerListView.setLayoutManager(linearLayoutManager);
            locationBasedProjectAdapter= new LocationBasedProjectAdapter(this,locationBasedProjectActivity);
            recyclerListView.setAdapter(locationBasedProjectAdapter);
        }else{
            ToastUtils.displayToast("No Activity found in this location !", this);
            emptyLabelMessage.setVisibility(View.VISIBLE);
            recyclerListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.locationbtn:
                projectSelectionActivityPresenter.performLocationFilterFunctionality(project);
                break;
            case R.id.beneficiarybtn:
                projectSelectionActivityPresenter.performBeneficiaryFilterFunctionality(project);
                break;
            case R.id.backPress:
                onBackPressed();
                break;
            default:
                break;
        }
    }


}
