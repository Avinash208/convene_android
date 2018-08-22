package org.yale.convene;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.yale.convene.BeenClass.ProjectList;
import org.yale.convene.BeenClass.parentChild.SurveyDetail;
import org.yale.convene.adapter.spinnercustomadapter.LocationBasedProjectAdapter;
import org.yale.convene.presenter.ProjectSelectionPresenter;
import org.yale.convene.utils.Logger;
import org.yale.convene.utils.ToastUtils;
import org.yale.convene.view.ProjectSelectionActivityScene;

import java.util.List;

public class ProjectSelectionActivity extends AppCompatActivity implements ProjectSelectionActivityScene, View.OnClickListener {
    ProjectSelectionPresenter projectSelectionActivityPresenter;
    private Spinner project;
    private RecyclerView recyclerListView;
    private LocationBasedProjectAdapter locationBasedProjectAdapter;
    private TextView emptyLabelMessage;
    private int storeProjectSelectedIndex=0;
    private Button beneficiaryBtn;
    private Button locationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_selection);
        initVariables();
        spinnerLisiner(project);
    }

    private void spinnerLisiner(Spinner project) {
        project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Logger.logD("Selected positon->",i+"");
                storeProjectSelectedIndex=i;
                beneficiaryBtn.performClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initVariables() {
        TextView toolbarTextView = (TextView) findViewById(R.id.toolbarTitle);
        ImageView searchIcon = (ImageView) findViewById(R.id.imageMenu);
        locationBtn = (Button) findViewById(R.id.locationbtn);
        beneficiaryBtn = (Button) findViewById(R.id.beneficiarybtn);
        LinearLayout backbtn = (LinearLayout) findViewById(R.id.backPress);
        project = (Spinner) findViewById(R.id.projectspinner);
        recyclerListView = (RecyclerView) findViewById(R.id.projectrecycler);
        emptyLabelMessage = (TextView) findViewById(R.id.emptytextview);
        searchIcon.setVisibility(View.VISIBLE);
        toolbarTextView.setText(R.string.projectandactivity);
        projectSelectionActivityPresenter = new ProjectSelectionPresenter(this);
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
        Logger.logD("Project List", projectList.size() + "");
        ArrayAdapter<ProjectList> spinnerArrayAdapter = new ArrayAdapter(this, R.layout.spinner_multi_row_textview, projectList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_multi_row_textview);// The drop down view
        project.setAdapter(spinnerArrayAdapter);
        if (storeProjectSelectedIndex!=0){
            project.setSelection(storeProjectSelectedIndex);

        }
        beneficiaryBtn.performClick();



    }

    @Override
    public void getLocationBasedActivity(List<SurveyDetail> locationBasedProjectActivity) {
        if (!locationBasedProjectActivity.isEmpty()) {
            emptyLabelMessage.setVisibility(View.GONE);
            recyclerListView.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerListView.setLayoutManager(linearLayoutManager);
            locationBasedProjectAdapter = new LocationBasedProjectAdapter(this, locationBasedProjectActivity);
            recyclerListView.setAdapter(locationBasedProjectAdapter);
        } else {
            ToastUtils.displayToast("No Activity found !", this);
            emptyLabelMessage.setVisibility(View.VISIBLE);
            recyclerListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.locationbtn:
                Drawable dw = getApplicationContext().getResources().getDrawable(R.drawable.round_homepagebtn_location);
                Drawable dw1 = getApplicationContext().getResources().getDrawable(R.drawable.round_tab);
                locationBtn.setBackground(dw);
                beneficiaryBtn.setBackground(dw1);
                projectSelectionActivityPresenter.performLocationFilterFunctionality(project);
                break;
            case R.id.beneficiarybtn:
                Drawable dwb = getApplicationContext().getResources().getDrawable(R.drawable.round_homepagebtn_location);
                Drawable dwb1 = getApplicationContext().getResources().getDrawable(R.drawable.round_tab);
                beneficiaryBtn.setBackground(dwb);
                locationBtn.setBackground(dwb1);
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
