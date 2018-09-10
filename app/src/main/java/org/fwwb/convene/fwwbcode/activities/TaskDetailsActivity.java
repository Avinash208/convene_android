package org.fwwb.convene.fwwbcode.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.AutoSyncActivity;
import org.fwwb.convene.convenecode.BeenClass.AnswersPage;
import org.fwwb.convene.convenecode.BeenClass.childLink;
import org.fwwb.convene.convenecode.ListingActivity;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.DataBaseMapperClass;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.database.Utilities;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.Utils;
import org.fwwb.convene.fwwbcode.modelclasses.MembersBean;
import org.fwwb.convene.fwwbcode.modelclasses.TaskItemBean;
import org.fwwb.convene.fwwbcode.presentor.attendancepresentor.MemberAttendanceAsyncTask;
import org.fwwb.convene.fwwbcode.presentor.attendancepresentor.MemberAttendanceListener;
import org.fwwb.convene.fwwbcode.presentor.attendancepresentor.SaveAttendanceHelper;
import org.fwwb.convene.fwwbcode.presentor.attendancepresentor.SaveAttendanceListener;
import org.fwwb.convene.fwwbcode.presentor.taskpresentor.TaskListHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskDetailsActivity extends AppCompatActivity implements MemberAttendanceListener, SaveAttendanceListener {

    private TextView batchNameTv;
    private TextView trainingNameTv;
    private TextView trainingLocationTv;
    private TextView batchParticipantsTv;
    private TextView trainingDateTv;
    private TextView attendanceSyncCountTv;
    private Button syncBtn;
    private ProgressBar syncProgress;

    private LinearLayout parentPanel;
    private LinearLayout syncLayout;
    private LinearLayout clickedLayout = null;
    private DBHandler dbHandler;
    TaskItemBean taskItemBean;
    private ConveneDatabaseHelper conveneDatabaseHelper;

    private List<MembersBean> membersBeanList = new ArrayList<>();
    private String surveyId="";
    private List<AnswersPage> options;
    private SaveAttendanceHelper saveAttendanceHelper;
    private SyncReceiver syncReceiver;
    private IntentFilter syncFilter;

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
        syncReceiver = new SyncReceiver();
        syncFilter = new IntentFilter("Survey");
        SharedPreferences sharedPreferencesDefault = PreferenceManager.getDefaultSharedPreferences(activity);
        conveneDatabaseHelper = ConveneDatabaseHelper.getInstance(activity, sharedPreferencesDefault.getString(Constants.CONVENE_DB,""), sharedPreferencesDefault.getString("UID",""));
        ExternalDbOpenHelper externalDbOpenHelper = ExternalDbOpenHelper.getInstance(activity, sharedPreferencesDefault.getString(Constants.DBNAME, ""), sharedPreferencesDefault.getString("inv_id", ""));
        int surveyId = externalDbOpenHelper.getAttendanceSurvey(externalDbOpenHelper);
        int questionId = conveneDatabaseHelper.getAttendaceQuestion(surveyId);
        HashMap<String, List<AnswersPage>> answerValues = DataBaseMapperClass.getAnswerFromDBnew(questionId, conveneDatabaseHelper.getReadableDatabase(), null, 1);
        options = answerValues.get(String.valueOf(questionId));
        saveAttendanceHelper = new SaveAttendanceHelper(TaskDetailsActivity.this,surveyId,questionId,this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (syncReceiver!= null)
            registerReceiver(syncReceiver, syncFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (syncReceiver != null)
            unregisterReceiver(syncReceiver);
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
        if (taskItemBean.getTrainingStatus()<=1)
        {
            trainingLocationTv.setText(taskItemBean.getTrainingStatus()+" hour");
        }
        else
        {
            trainingLocationTv.setText(taskItemBean.getTrainingStatus()+" hours");
        }

        trainingNameTv.setText(taskItemBean.getTrainingName());
        setAdapter();

    }

    private void setUnsync() {

        syncBtn.setVisibility(View.VISIBLE);
        syncProgress.setVisibility(View.GONE);
        String uuidForInQuery = getStringInQuery(membersBeanList);
        List<String> list= dbHandler.getUnsyncAttendance(taskItemBean.getTrainingUuid(),uuidForInQuery);
        if (list.isEmpty())
        {
            syncLayout.setVisibility(View.GONE);
        }
        else
        {
            syncLayout.setVisibility(View.VISIBLE);
            attendanceSyncCountTv.setText(list.size()+" unsynced data available.");
        }

    }

    private String getStringInQuery(List<MembersBean> membersBeanList) {
        String tempUuid ="";
        for (MembersBean membersBean:membersBeanList)
        {
            if (tempUuid.isEmpty())
                tempUuid = "'"+membersBean.getMemberUuid()+"'";
            else
                tempUuid = tempUuid+",'"+membersBean.getMemberUuid()+"'";
        }
        return tempUuid;
    }

    private void setAdapter() {
        try {
            if (taskItemBean.getBatchParticipants()==0)
                return;
            ArrayList<childLink> getChildUUids = dbHandler.getChildDetailsFromBeneficiaryLinkage(taskItemBean.getBeneficiaryType(), taskItemBean.getBatchUuid());
            String getQuestionIds = TaskListHelper.getNameSurvey(conveneDatabaseHelper);
            Logger.logD("likage getChildUUids", getChildUUids + "");
            new MemberAttendanceAsyncTask(getChildUUids,getQuestionIds,taskItemBean,dbHandler,this,options).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        }catch (Exception e)
        {
            Logger.logE("TaskDetailsActivity","setAdapter",e);
        }

    }



    private void setUserDataToUi(List<MembersBean> membersBeanList) {
        final Animation enlarge = AnimationUtils.loadAnimation(TaskDetailsActivity.this, R.anim.enlarge);
        final Animation shrink = AnimationUtils.loadAnimation(TaskDetailsActivity.this, R.anim.shrink);
        parentPanel.removeAllViews();

        for (int i = 0; i < membersBeanList.size(); i++) {
            final MembersBean membersBean = membersBeanList.get(i);
            View parentView = getLayoutInflater().inflate(R.layout.attendance_tile, parentPanel, false);
            final ImageView infoImg = parentView.findViewById(R.id.info);
            ImageView present = parentView.findViewById(R.id.attended);
            ImageView absent = parentView.findViewById(R.id.close);
            TextView count = parentView.findViewById(R.id.count);
            TextView memberName = parentView.findViewById(R.id.memberName);
            final LinearLayout attendanceLayout = parentView.findViewById(R.id.attendanceLayout);
            memberName.setText(membersBean.getMemberName());
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

            if (!membersBean.getAttendanceSurveyUuid().isEmpty())
            {
                if (membersBean.getMemberAttendance() == 1)
                {
                    infoImg.setBackground(getResources().getDrawable(R.drawable.rounded_present));
                    infoImg.setImageDrawable(getResources().getDrawable(R.drawable.attendedicon));
                }
                if (membersBean.getMemberAttendance() ==0)
                {
                    infoImg.setBackground(getResources().getDrawable(R.drawable.rounded_absent));
                    infoImg.setImageDrawable(getResources().getDrawable(R.drawable.closeicon));

                }

            }

            present.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attendanceLayout.setVisibility(View.GONE);
                    attendanceLayout.setAnimation(shrink);
                    attendanceLayout.startAnimation(shrink);
                    clickedLayout = null;
                    infoImg.setBackground(getResources().getDrawable(R.drawable.rounded_present));
                    infoImg.setImageDrawable(getResources().getDrawable(R.drawable.attendedicon));
                    saveAttendanceHelper.callSurvey(membersBean.getMemberUuid(),membersBean.getAttendanceSurveyUuid(),taskItemBean.getTrainingUuid(), Integer.parseInt(options.get(0).getAnswerCode()),taskItemBean.getBatchUuid());

                }
            });
            absent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attendanceLayout.setVisibility(View.GONE);
                    attendanceLayout.setAnimation(shrink);
                    attendanceLayout.startAnimation(shrink);
                    clickedLayout = null;
                    infoImg.setBackground(getResources().getDrawable(R.drawable.rounded_absent));
                    infoImg.setImageDrawable(getResources().getDrawable(R.drawable.closeicon));
                    saveAttendanceHelper.callSurvey(membersBean.getMemberUuid(),membersBean.getAttendanceSurveyUuid(),taskItemBean.getTrainingUuid(), Integer.parseInt(options.get(1).getAnswerCode()), taskItemBean.getBatchUuid());

                }
            });


            parentPanel.addView(parentView);
        }

    }

    private void initView() {
        parentPanel = findViewById(R.id.parentPanel);
        syncLayout = findViewById(R.id.syncLayout);
        batchNameTv = findViewById(R.id.toolbarTitle);
        trainingNameTv = findViewById(R.id.trainingNameTv);
        trainingLocationTv = findViewById(R.id.trainingLocationTv);
        batchParticipantsTv = findViewById(R.id.batchParticipantsTv);
        trainingDateTv = findViewById(R.id.trainingDateTv);
        attendanceSyncCountTv = findViewById(R.id.attendanceSyncCountTv);
        syncBtn = findViewById(R.id.syncBtn);
        syncProgress = findViewById(R.id.progressBarSync);
        findViewById(R.id.backPress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callAutoSync();
            }
        });


    }

    private void callAutoSync() {

        syncBtn.setVisibility(View.GONE);
        syncProgress.setVisibility(View.VISIBLE);

        if (Utils.haveNetworkConnection(TaskDetailsActivity.this)) {
            AutoSyncActivity autoSyncObj = new AutoSyncActivity(TaskDetailsActivity.this);
            autoSyncObj.callingAutoSync(1);

        }else
        {
            syncBtn.setVisibility(View.VISIBLE);
            syncProgress.setVisibility(View.GONE);

            Toast.makeText(this, R.string.offline_toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void memberList(List<MembersBean> membersBeanList) {
        this.membersBeanList = membersBeanList;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUserDataToUi(membersBeanList);
                setUnsync();
            }
        });
    }




    @Override
    public void savedAttendance(boolean isSaved, String memberUuid, String surveyUuid) {

    }

    @Override
    public void savedAttendanceSurvey(boolean isSaved, String memberUuid, String surveyUuid) {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            setAdapter();

        }
    });
    }


    /**
     * Receiver Class
     */
    public class SyncReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                runOnUiThread(() -> setUnsync());
            } catch (Exception e) {
                Logger.logE(ListingActivity.class.getSimpleName(), "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }
}
