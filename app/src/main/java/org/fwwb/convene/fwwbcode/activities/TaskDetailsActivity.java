package org.fwwb.convene.fwwbcode.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.BeenClass.QuestionAnswer;
import org.fwwb.convene.convenecode.BeenClass.childLink;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;
import org.fwwb.convene.fwwbcode.presentor.taskpresentor.TaskListHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity {

    private TextView batchNameTv;
    private TextView trainingNameTv;
    private TextView trainingLocationTv;
    private TextView batchParticipantsTv;
    private TextView trainingDateTv;

    private LinearLayout parentPanel;
    private LinearLayout clickedLayout = null;
    private DBHandler dbHandler;
    TaskItemBean taskItemBean;
    private ExternalDbOpenHelper externalDbOpenHelper;
    private ConveneDatabaseHelper conveneDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        initPreferences();
        initView();
        getActivityData();
        
    }

    private void initPreferences() {
        dbHandler = new DBHandler(TaskDetailsActivity.this);
        Activity activity= TaskDetailsActivity.this;
        SharedPreferences sharedPreferencesDefault = PreferenceManager.getDefaultSharedPreferences(activity);
        conveneDatabaseHelper = ConveneDatabaseHelper.getInstance(activity, sharedPreferencesDefault.getString(Constants.CONVENE_DB,""), sharedPreferencesDefault.getString("UID",""));
        externalDbOpenHelper = ExternalDbOpenHelper.getInstance(activity,sharedPreferencesDefault.getString(Constants.DBNAME, ""), sharedPreferencesDefault.getString("inv_id", ""));

    }

    private void getActivityData() {
        if (getIntent() == null)
            return;
        if (getIntent().getExtras() == null)
            return;

        taskItemBean = getIntent().getExtras().getParcelable("taskItem");
        if (taskItemBean== null)
            return;
        batchNameTv.setText(taskItemBean.getBatchName());
        batchParticipantsTv.setText(""+taskItemBean.getBatchParticipants());
        trainingDateTv.setText(taskItemBean.getTrainingDate());
        trainingLocationTv.setText(taskItemBean.getTrainingStatus()+"");
        trainingNameTv.setText(taskItemBean.getTrainingName());
        setAdapter();

    }

    private void setAdapter() {
        try {
            if (taskItemBean.getBatchParticipants()==0)
                return;
            ArrayList<childLink> getChildUUids = dbHandler.getChildDetailsFromBeneficiaryLinkage(taskItemBean.getBeneficiaryType(), taskItemBean.getBatchUuid());
            String getQuestionIds = TaskListHelper.getNameSurvey(conveneDatabaseHelper);
            Logger.logD("likage getChildUUids", getChildUUids + "");
            setUserData(getQuestionIds,getChildUUids);
        }catch (Exception e)
        {
            Logger.logE("TaskDetailsActivity","setAdapter",e);
        }

    }

    private void setUserData(String nameQids, ArrayList<childLink> getChildUUids) {
        final Animation enlarge = AnimationUtils.loadAnimation(TaskDetailsActivity.this, R.anim.enlarge);
        final Animation shrink = AnimationUtils.loadAnimation(TaskDetailsActivity.this, R.anim.shrink);

        parentPanel.removeAllViews();

        for (int i = 0; i < getChildUUids.size(); i++) {

            View parentView = getLayoutInflater().inflate(R.layout.attendance_tile, parentPanel, false);
            final ImageView infoImg = parentView.findViewById(R.id.info);
            ImageView present = parentView.findViewById(R.id.attended);
            ImageView absent = parentView.findViewById(R.id.close);
            TextView count = parentView.findViewById(R.id.count);
            TextView memberName = parentView.findViewById(R.id.memberName);
            final LinearLayout attendanceLayout = parentView.findViewById(R.id.attendanceLayout);
            String name = TaskListHelper.getNameFromResponce(getChildUUids.get(i).getChild_form_id(), nameQids,0,dbHandler);
            memberName.setText(name);
            final int finalI = i;
            count.setText((finalI + 1) + ".");
            infoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickedLayout != null && clickedLayout != attendanceLayout) {
                        clickedLayout.setVisibility(View.GONE);
                        clickedLayout.setAnimation(shrink);
                        clickedLayout.startAnimation(shrink);
                    }
                    clickedLayout = attendanceLayout;
                    if (attendanceLayout.getVisibility() == View.VISIBLE) {
                        attendanceLayout.setVisibility(View.GONE);
                        attendanceLayout.setAnimation(shrink);
                        attendanceLayout.startAnimation(shrink);
                    } else {
                        attendanceLayout.setAnimation(enlarge);
                        attendanceLayout.setVisibility(View.VISIBLE);
                        attendanceLayout.startAnimation(enlarge);

                    }

                }
            });

            present.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TaskDetailsActivity.this, "Present of " + finalI, Toast.LENGTH_SHORT).show();
                    attendanceLayout.setVisibility(View.GONE);
                    attendanceLayout.setAnimation(shrink);
                    attendanceLayout.startAnimation(shrink);
                    clickedLayout = null;
                    infoImg.setBackground(getResources().getDrawable(R.drawable.rounded_present));
                    infoImg.setImageDrawable(getResources().getDrawable(R.drawable.attendedicon));
                }
            });
            absent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TaskDetailsActivity.this, "absent of " + finalI, Toast.LENGTH_SHORT).show();
                    attendanceLayout.setVisibility(View.GONE);
                    attendanceLayout.setAnimation(shrink);
                    attendanceLayout.startAnimation(shrink);
                    clickedLayout = null;
                    infoImg.setBackground(getResources().getDrawable(R.drawable.rounded_absent));
                    infoImg.setImageDrawable(getResources().getDrawable(R.drawable.closeicon));
                }
            });


            parentPanel.addView(parentView);
        }

    }

    private void initView() {
        parentPanel = findViewById(R.id.parentPanel);
        batchNameTv = findViewById(R.id.toolbarTitle);
        trainingNameTv = findViewById(R.id.trainingNameTv);
        trainingLocationTv = findViewById(R.id.trainingLocationTv);
        batchParticipantsTv = findViewById(R.id.batchParticipantsTv);
        trainingDateTv = findViewById(R.id.trainingDateTv);
    }
}
