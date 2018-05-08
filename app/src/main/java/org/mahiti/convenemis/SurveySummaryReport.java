package org.mahiti.convenemis;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.sqlcipher.database.SQLiteDatabase;


import org.mahiti.convenemis.BeenClass.StatusBean;
import org.mahiti.convenemis.BeenClass.parentChild.LevelBeen;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.DataBaseMapperClass;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.utils.CheckNetwork;
import org.mahiti.convenemis.utils.ClusterInfo;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SurveySummaryReport extends BaseActivity implements View.OnClickListener  {
    private static final String MY_PREFS_NAME = "MyPrefs";
    private static final String ThemeCurrents = "AppliedTheme";
    private static final String TAG = "SurveySummaryReport";
    private DBHandler surveySummaryreportdbhandler;
    private ListView syncSurveyListView;
    List<Integer> syncSurveySyncCompletedList;
    List<Integer> syncSurveySyncPendingList;
    SQLiteDatabase syncSurveyDatabase;
    List<StatusBean> syncSurveyList;
    List<StatusBean> syncSurveyListPending;
    List<StatusBean> syncCompletedPendingListPending;
    DBHandler syncSurveyHandler;
    private TextView surveyCompleted;
    SharedPreferences syncSurveyPreferences;
    ProgressDialog syncSurveyProgDialog;
    private TextView surveyPending;
    SharedPreferences prefs;
    Myreceiver reMyreceive;
    IntentFilter filter;
    public android.database.sqlite.SQLiteDatabase SurveySummaryReportdatabase;
    SharedPreferences.Editor syncSurveyEditor;
    String language = ClusterInfo.English;
    AlertDialog alert1;
    private CheckNetwork chckNework;
    private TextView sync_value;
    private LinearLayout layoutCreate;
    private LinearLayout pendinglayout;
    private LinearLayout synclayout;
    private LinearLayout createlayoutmain;
    private LinearLayout pendinglayoutmain;
    private TextView createdLabel;
    private TextView pendingLabel;
    private TextView synchronizedLabel;
    private View viewCreate;
    private View viewPending;
    private View viewSync;

    ExternalDbOpenHelper externalDbOpenHelper;
    private SetSurveyStatus setstatusAdapter;
    private ConveneDatabaseHelper dbOpenHelper;
    private TextView toolbarTitle;
    String qid;
    private CheckBox checkBox;
    private LinearLayout filterContainer;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_summary_report_activitynew);
        Logger.logD("-->end time 1","checking time line");
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        chckNework = new CheckNetwork(this);
        reMyreceive = new Myreceiver();
        filter = new IntentFilter("Survey");
        Bundle extras = getIntent().getExtras();
        syncSurveyProgDialog = ProgressUtils.showProgress(SurveySummaryReport.this, false, "");
        preferences = PreferenceManager.getDefaultSharedPreferences(SurveySummaryReport.this);
        String surveyTittle;
        if (extras != null) {
            surveyTittle = prefs.getString("Survey_tittle", null);
            this.setTitle(surveyTittle);
        }
        UpdateToolBar();
        syncSurveyPreferences= PreferenceManager.getDefaultSharedPreferences(SurveySummaryReport.this);
        syncSurveyEditor = syncSurveyPreferences.edit();
        dbOpenHelper = ConveneDatabaseHelper.getInstance(this, syncSurveyPreferences.getString("CONVENEDB", ""), syncSurveyPreferences.getString("UID", ""));
       // SurveySummaryReportdatabase = dbOpenHelper.getWritableDatabase();
       // surveySummaryreportdbhandler = new DBHandler(this);
        syncSurveyHandler = new DBHandler(SurveySummaryReport.this);
        syncSurveyDatabase = syncSurveyHandler.getdatabaseinstance();
        syncSurveyList = new ArrayList<>();
        syncSurveyListPending = new ArrayList<>();
        syncCompletedPendingListPending = new ArrayList<>();
        syncSurveySyncCompletedList = new ArrayList<>();
        syncSurveySyncPendingList = new ArrayList<>();
        externalDbOpenHelper= ExternalDbOpenHelper.getInstance(this, preferences.getString("DBNAME",""),syncSurveyPreferences.getString("UID",""));
        qid=externalDbOpenHelper.getSummaryQid(prefs.getInt(Constants.SURVEY_ID, 0),externalDbOpenHelper);
        final Button fab = (Button) findViewById(R.id.createsurvey);
        layoutCreate= (LinearLayout) findViewById(R.id.createlayout);
        createlayoutmain= (LinearLayout) findViewById(R.id.createlayoutmain);
        pendinglayoutmain = (LinearLayout) findViewById(R.id.pendinglayoutmain);
        pendinglayout= (LinearLayout) findViewById(R.id.pendinglayout);
        surveyCompleted= (TextView)findViewById(R.id.completed_value);
         synclayout= (LinearLayout) findViewById(R.id.synclayout);
        surveyPending= (TextView)findViewById(R.id.pending_value);
        createdLabel= (TextView)findViewById(R.id.createdLabel);
        pendingLabel= (TextView)findViewById(R.id.pendingLabel);
        synchronizedLabel= (TextView)findViewById(R.id.synchronizedLabel);
        sync_value= (TextView)findViewById(R.id.sync_value);
        syncSurveyListView = (ListView) findViewById(R.id.listreport) ;
        syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        syncSurveyProgDialog = new ProgressDialog(this);
        viewCreate= findViewById(R.id.viewCreate);
        viewPending= findViewById(R.id.viewpending);
        viewSync= findViewById(R.id.viewSync);
        syncSurveyProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        syncSurveyProgDialog.setMessage("Processing Pending Survey" + "...");
       /* searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        searchBar.setHint("Search by name");
        searchBar.setTextHintColor(R.color.white);
        searchBar.setMaxSuggestionCount(5);
        searchBar.setOnSearchActionListener(this);*/
        filterContainer = (LinearLayout) findViewById(R.id.filtercontainer);
        LinearLayout singleSpinnerContainer = (LinearLayout) findViewById(R.id.singlespinnercontainer);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
       // spinnerSearch=new SingleSpinnerSearchFilter(this);
  //      singleSpinnerContainer.addView(spinnerSearch);
     //   callOnTextChangeLisiner(searchBar);
       // createViewBOx();
        layoutCreate.setOnClickListener(this);
        synclayout.setOnClickListener(this);
        pendinglayout.setOnClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              int questionCount= DataBaseMapperClass.getQuestionCountfromDB(prefs.getInt("survey_id", 0),SurveySummaryReportdatabase);
               Log.d("SurveyIdForGetQCount",prefs.getInt("survey_id", 0)+"");
                if (questionCount==0) {
                    Intent calllevelactivity = new Intent(SurveySummaryReport.this, LevelsActivityNew.class);
                    startActivity(calllevelactivity);
                }else{
                    Toast.makeText(SurveySummaryReport.this,"Question are Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new  summaryReportSync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Logger.logD("-->end time 2","checking time line");
       setVisiablity();
        createFilterValues();


    }

    private void setVisiablity() {
        if (prefs.getInt("survey_id", 0)==17)
            filterContainer.setVisibility(View.VISIBLE);
        else
            filterContainer.setVisibility(View.GONE);
    }

    private void createFilterValues() {
        List<Integer> firstSpinnerIds = externalDbOpenHelper.getLevelIds("level5", "level5", "");
       final List<LevelBeen> list = externalDbOpenHelper.getLevelsValues("level5", firstSpinnerIds.toString());

       /*spinnerSearch.setFilterItems(list, 0, new SpinnerListenerFilter() {

           @Override
           public void onItemsSelected(List<LevelBeen> items) {
               try {
                   for(int j=0;j<items.size();j++){
                       if (items.get(j).isSelected()) {
                           Logger.logD("Selected","Item is"+list.get(j).getId());
                           externalDbOpenHelper= ExternalDbOpenHelper.getInstance(SurveySummaryReport.this, preferences.getString("DBNAME",""),syncSurveyPreferences.getString("UID",""));
                           int getVillageID=externalDbOpenHelper.fetchDatabaseToFilterList(list.get(j).getId());
                           Logger.logD("Selected","villageID"+getVillageID);
                           DBHandler  surveySummaryreportdbhandler = new DBHandler(SurveySummaryReport.this);
                           List<StatusBean> syncSurveyList = surveySummaryreportdbhandler.getFilterSearchedRecords(getVillageID,surveySummaryreportdbhandler,
                                   checkBox.isChecked(),externalDbOpenHelper);
                           Logger.logD("filled","List against search "+syncSurveyList.size());
                           if (!syncSurveyList.isEmpty()){
                               SetSurveyStatus SetstatusAdapter =new SetSurveyStatus(SurveySummaryReport.this,syncSurveyList);
                               syncSurveyListView.setAdapter(SetstatusAdapter);
                           }else {
                               List<StatusBean> syncSurveyListEmpty =new ArrayList<>();
                               SetSurveyStatus SetstatusAdapter =new SetSurveyStatus(SurveySummaryReport.this,syncSurveyListEmpty);
                               syncSurveyListView.setAdapter(SetstatusAdapter);
                           }

                       }
                   }
               }catch (Exception e){
                   Logger.logE(TAG,"onItemsSelected in ",e);
               }

           }
       });*/

    }


    private void UpdateToolBar() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
            Logger.logD("Tittle","survey tittle selected"+prefs.getString("Survey_tittle", null));
            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(prefs.getString("Survey_tittle", null));
            setSupportActionBar(toolbar);
        }catch (Exception e){
            Logger.logE(TAG,"UpdateToolBar in ",e);
        }


    }
    /*private void callDatabase(CharSequence userText) {
        SurveySummaryReportdatabase = dbOpenHelper.getWritableDatabase();
        DBHandler  surveySummaryreportdbhandler = new DBHandler(this);
        try {
            if (!userText.equals("") && userText.length()>0 ) {
                List<StatusBean> syncSurveyList = surveySummaryreportdbhandler.callDatabaseToFillSurveyList(userText, syncSurveyHandler, qid);
                Logger.logD("filled","List against search "+syncSurveyList.size());
                if (!syncSurveyList.isEmpty()){
                    SetSurveyStatus SetstatusAdapter =new SetSurveyStatus(SurveySummaryReport.this,syncSurveyList);
                    syncSurveyListView.setAdapter(SetstatusAdapter);
                }
            }else{
                methodToSetSyncDataApapter();
            }
        }catch (Exception e){
            Logger.logE(TAG,"callDatabase in ",e);
        }

    }*/


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(reMyreceive, filter);
       new  summaryReportSync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.setTitle(prefs.getString("surveyName", null));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            finish();
    }

    /**
     *
     * @param survey_id
     * @return
     */
    public int summaryReport(int survey_id) {
        SurveySummaryReportdatabase = dbOpenHelper.getWritableDatabase();
        DBHandler  surveySummaryreportdbhandler = new DBHandler(this);
        String pendingQuery = "Select * From Survey where  survey_ids ="+survey_id + " order by id DESC";
        int pendSurveyStatus = 0;
        int pendSurveyId = 0;
        String specimenId = "";
        int countSurvey = 0;
        syncSurveySyncCompletedList.clear();
        syncSurveySyncPendingList.clear();
        syncSurveyList.clear();
        syncSurveyListPending.clear();
        syncCompletedPendingListPending.clear();
        String typoCodes = "";
        String section1 = "N", section2 = "N", section3 = "N";
        try {
            syncSurveyDatabase = surveySummaryreportdbhandler.getdatabaseinstance_read();
            net.sqlcipher.Cursor cursorPendingSurvey = syncSurveyDatabase.rawQuery(pendingQuery, null);
            if (cursorPendingSurvey != null && cursorPendingSurvey.moveToFirst()) {
                do {
                    pendSurveyStatus = cursorPendingSurvey.getInt(cursorPendingSurvey.getColumnIndex("survey_status"));
                    pendSurveyId = cursorPendingSurvey.getInt(cursorPendingSurvey.getColumnIndex("id"));
                    specimenId = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("cluster_name"));
                    String date = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("end_date"));
                    if(date.equals("0")){
                        date = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("start_date"));
                    }
                    specimenId = specimenId +"# Entered on: "+date;
                    String language1 = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("language_id"));
                    section2 = setSummaryReport(pendSurveyStatus, pendSurveyId);
                    /*if ( pendSurveyStatus==1){
                            syncSurveyListPending.add(new StatusBean(specimenId, String.valueOf(pendSurveyStatus), "", section2, language1, "", pendSurveyId, ""));
                        }
                    syncSurveyList.add(new StatusBean(specimenId, String.valueOf(pendSurveyStatus), "", section2, language1, "", pendSurveyId, ""));*/
                    countSurvey = countSurvey + 1;
                } while (cursorPendingSurvey.moveToNext());
                cursorPendingSurvey.close();
            }
            setPendingText();
            cursorPendingSurvey.close();
            syncSurveyDatabase.close();
            Logger.logV("Listsize","List size of summery report" + syncSurveyList.size());
        } catch (Exception e) {
            Logger.logE(SurveySummaryReport.class.getSimpleName(), "Exception in SyncSurveyActivity  summaryReport method ", e);
        }
        return pendSurveyStatus;
    }

    private void setPendingText() {
        surveyCompleted.setText(String.valueOf(syncSurveySyncCompletedList.size()+syncSurveySyncPendingList.size()));
        surveyPending.setText(String.valueOf(syncSurveySyncPendingList.size()));
        sync_value.setText(String.valueOf(syncSurveySyncCompletedList.size()));
    }
    public String setSummaryReport(int pendSurveyStatus, int pendSurveyId) {
        if (pendSurveyStatus == 1 || pendSurveyStatus == 3 || pendSurveyStatus==0) {
            Logger.logD("pendSurveyId","pendSurveyId" + pendSurveyId);
            syncSurveySyncPendingList.add(pendSurveyId);
        } else if (pendSurveyStatus == 2) {
            syncSurveySyncCompletedList.add(pendSurveyId);
        }
        if (pendSurveyStatus != 0)
            return "Y";
        return "N";
    }
    @Override
    public void onClick(View view) {
        if (R.id.backPress==view.getId()){
            finish();
        }

    }

    private void methodToSetSyncDataApapter() {
        if (syncSurveyList.size()>0){
            Log.d("syncSurveyList",syncSurveyList.size()+"");
            SetSurveyStatus SetstatusAdapter =new SetSurveyStatus(SurveySummaryReport.this,syncSurveyList);
            syncSurveyListView.setAdapter(SetstatusAdapter);
        }
    }

    private void methodToSetPendingAdapter() {
        if (syncSurveyListPending.size()>0){
            Log.d("syncSurveyListPending",syncSurveyListPending.size()+"");
            setstatusAdapter =new SetSurveyStatus(SurveySummaryReport.this,syncSurveyListPending);
            syncSurveyListView.setAdapter(setstatusAdapter);

        }else{
            try {
                setstatusAdapter =new SetSurveyStatus(SurveySummaryReport.this,syncSurveyListPending);
                syncSurveyListView.setAdapter(setstatusAdapter);
            }catch (Exception e){
                Logger.logE(TAG,"Exception in ",e);
            }
           
        }

    }

    private class SetSurveyStatus  extends BaseAdapter {

        Context context;
        List<StatusBean> statusbean;

        public SetSurveyStatus(SurveySummaryReport surveySummaryReport, List<StatusBean> statusBeanList) {
            this.context = surveySummaryReport;
            statusbean = statusBeanList;

        }
        @Override
        public int getViewTypeCount() {

            return getCount();
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }
        @Override
        public int getCount() {
            return statusbean.size();
        }

        @Override
        public StatusBean getItem(int position) {
            return statusbean.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            Log.v("getView ", "getView" + i);
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            final Viewholder vh;
            View layoutView = convertView;
            if (layoutView == null) {
                vh = new Viewholder();
                layoutView = inflater.inflate(R.layout.surveysummary_detail_row, viewGroup, false);
           //     vh.surveyName = (TextView) layoutView.findViewById(R.id.survey_id);
              //  vh.schemasTextView=(TextView)layoutView.findViewById(R.id.scheme_btm);
              //  vh.tvPart1 = (TextView) layoutView.findViewById(R.id.date);
                vh.anniversariesListDymanicLabel= (LinearLayout) layoutView.findViewById(R.id.linearLayout);
               // vh.schemeparentlayout= (LinearLayout) layoutView.findViewById(R.id.schemeparentlayout);
                layoutView.setTag(vh);
            } else {
                layoutView = convertView;
                vh = (Viewholder) layoutView.getTag();
            }

            if(!"".equals(qid) && qid.contains(","))
            {
                String summary="";
                String[] qids=qid.split(",");
                for(int k=0;k<qids.length;k++)
                {
                    summary=summary + DBHandler.getAnswerFromPreviousQuestion(qids[k], surveySummaryreportdbhandler, String.valueOf(statusbean.get(i).getSurveyId()));
                }
                String[] getClusterName=statusbean.get(i).getCaseId().split("#");
                vh.surveyName.setText(getClusterName[0]);
                vh.tvPart1.setText(getClusterName[1]);
                 Logger.logD("Getting many times","->"+i);
                vh.anniversariesListDymanicLabel.removeAllViews();
                /* getParentDetails(vh.anniversariesListDymanicLabel,statusbean.get(i).getSurveyId(),
                        prefs.getInt(Constants.SURVEY_ID, 0),qid);*/

            }
            else if(!"".equals(qid))
            {
                String summaryData= DBHandler.getAnswerFromPreviousQuestion(qid, surveySummaryreportdbhandler, String.valueOf(statusbean.get(i).getSurveyId()));
                vh.surveyName.setText(summaryData);
            }
            else {
                if (statusbean.get(i).getCaseId().equals("0"))
                    vh.surveyName.setText(statusbean.get(i).getCaseId());
                else
                    vh.surveyName.setText(statusbean.get(i).getCaseId());
            }
            if (prefs.getInt(Constants.SURVEY_ID, 0)==17)
                vh.schemeparentlayout.setVisibility(View.VISIBLE);
            else
                vh.schemeparentlayout.setVisibility(View.GONE);

            return layoutView;
        }
        /*private void getParentDetails(LinearLayout linearLayout, int  surveyPrimaryKey, int parentId,
                                      String summaryQIDs) {
            List<String> displayQuestionList = Arrays.asList(summaryQIDs.split(","));
            if (!displayQuestionList.isEmpty()) {
                for (int i = 0; i < displayQuestionList.size(); i++) {
                    View schemeChildView = getLayoutInflater().inflate(R.layout.scheme_inflate_row_summary, linearLayout, false);//child.xml
                    String getQuestionType = dbOpenHelper.getQuestionType(displayQuestionList.get(i));
                    String question = dbOpenHelper.getQuestionFromDb(displayQuestionList.get(i), parentId);
                    String answer = "";
                    if (getQuestionType.equalsIgnoreCase("R") || getQuestionType.equalsIgnoreCase("S")) {
                        answer = DBHandler.getAnswerFromQuestionID(displayQuestionList.get(i), syncSurveyHandler, String.valueOf(surveyPrimaryKey), getQuestionType);
                        answer = dbOpenHelper.getAnswer(displayQuestionList.get(i), answer, String.valueOf(surveyPrimaryKey));
                    } else {
                        answer = DBHandler.getAnswerFromQuestionID(displayQuestionList.get(i), syncSurveyHandler, String.valueOf(surveyPrimaryKey), getQuestionType);
                    }

                    QuestionAnswer filledBean = new QuestionAnswer();
                    filledBean.setQuestionText(question);
                    filledBean.setAnswerText(answer);
                    filledBean.setParentId(parentId);
                    setParentView(filledBean, schemeChildView);
                    linearLayout.addView(schemeChildView);


                }
            }







        }*/
        private class Viewholder {
            TextView surveyName;
            TextView schemasTextView;
            TextView tvPart1;
            LinearLayout anniversariesListDymanicLabel ;
            LinearLayout schemeparentlayout ;
        }
    }

   /* private void setParentView(QuestionAnswer questionAnswersList, View schemeChildView) {
        TextView questionButton = (TextView) schemeChildView.findViewById(R.id.nameTextLebel);
        questionButton.setText(questionAnswersList.getQuestionText());
        TextView answerText = (TextView) schemeChildView.findViewById(R.id.nameText);
        answerText.setText(questionAnswersList.getAnswerText());
    }
*/

   /* public void pendingSectionsStart(String spId, int sId, String typoCode, String anstext) {

        if (spId != null && !"".equals(spId)) {
            String[] typoArray = typoCode.split(",");
            SharedPreferences.Editor editor = syncSurveyPreferences.edit();
            editor.putString("resumeSurvey", "Yes");
            editor.putString("pending_specimen_id", spId);
            editor.putString("backupCaseId", spId);
            editor.putString("reason_off_survey", "");
            editor.putBoolean(PreferenceConstants.surveySkipFlag, false);
            editor.putString(PreferenceConstants.typologyId, typoArray[typoArray.length - 1]);
            editor.apply();
            syncSurveyProgDialog.dismiss();
            if (sId != 0) {
                Intent intent = new Intent(SurveySummaryReport.this, SurveyQuestionActivity.class);
                intent.putExtra(PreferenceConstants.surveyId, String.valueOf(sId));
                intent.putExtra("anstext", anstext);
                startActivity(intent);
            } else {
                Logger.logD("","");
            }
        }
    }*/




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(SurveySummaryReport.this, LoginParentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                SharedPreferences.Editor editor = syncSurveyPreferences.edit();
                editor.putString("uId", "");
                editor.apply();
                return true;
            case R.id.changeTheme:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        popUpMenu();
                    }
                });
                return true;

            case R.id.changeLanguage:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                return true;
            case R.id.sync_data:
                syncFunction();
                return true;
            case R.id.locationselection:
                LocationSelectionView();
                return true;
            case R.id.mahiti:
                companyInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /*private void chooseLanguage() {
        final SharedPreferences.Editor syncSurveyEditor = preferences.edit();
        final String[] languages = new String[]{ this.getString(R.string.Englishh)};
        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String language = languages[which];
                Logger.logV("Selected Language","Selected language in SurveySelectionActivity" + language);
                if(getString(R.string.Englishh).equals(language)) {
                    syncSurveyEditor.putInt("selectedLangauge", 1);
                    Logger.logV("Selected Language","Selected language in SurveySelectionActivity" + languages[which]);
                    BaseActivity.setLocality(1,SurveySummaryReport.this);
                    alert1.cancel();
                } else {
                    syncSurveyEditor.putInt("selectedLangauge", 2);
                    Logger.logV("Selected Language","Selected language in SurveySelectionActivity" + languages[which]);
                    BaseActivity.setLocality(2,SurveySummaryReport.this);
                    alert1.cancel();
                }
                syncSurveyEditor.apply();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(SurveySummaryReport.this);
        builder.setTitle(getString(R.string.select));
        builder.setSingleChoiceItems(languages, preferences.getInt("selectedLangauge", 0), dialogInterface);
        alert1 = builder.create();
        alert1.show();
    }*/
    private void popUpMenu() {
        final String[] themeColors = {"Blue","Green","Yellow"};


        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                language = themeColors[which];
                syncSurveyPreferences.edit();

                switch (themeColors[which]){
                    case "Blue":
                        syncSurveyEditor.putString(ThemeCurrents,"Blue");
                        syncSurveyEditor.putInt("SelectedValue", 1);
                        syncSurveyEditor.apply();

                        TaskStackBuilder.create(SurveySummaryReport.this)
                                .addNextIntent(new Intent(SurveySummaryReport.this, LoginParentActivity.class))
                                .addNextIntent(getIntent())
                                .startActivities();
                        alert1.cancel();
                        break;



                    case "Green":
                        syncSurveyEditor.putString(ThemeCurrents,"Green");
                        syncSurveyEditor.putInt("SelectedValue", 3);
                        syncSurveyEditor.apply();

                        TaskStackBuilder.create(SurveySummaryReport.this)
                                .addNextIntent(new Intent(SurveySummaryReport.this, LoginParentActivity.class))
                                .addNextIntent(getIntent())
                                .startActivities();
                        alert1.cancel();
                        break;

                    case "Yellow":
                        syncSurveyEditor.putString(ThemeCurrents,"Yellow");
                        syncSurveyEditor.putInt("SelectedValue", 2);
                        syncSurveyEditor.apply();
                        TaskStackBuilder.create(SurveySummaryReport.this)
                                .addNextIntent(new Intent(SurveySummaryReport.this, LoginParentActivity.class))
                                .addNextIntent(getIntent())
                                .startActivities();
                        alert1.cancel();
                        break;
                }

            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(SurveySummaryReport.this);
        builder.setTitle("Select  Theme");
        builder.setSingleChoiceItems(themeColors, syncSurveyPreferences.getInt("SelectedValue", 0), dialogInterface);
        alert1 = builder.create();
        alert1.show();
    }

    public void syncFunction() {
        if (chckNework.checkNetwork()) {
            if (!syncSurveyPreferences.getBoolean("sendingSurvey", false)) {
                ToastUtils.displayToast(getString(R.string.syncingPendingData), SurveySummaryReport.this);
                AutoSyncActivity autoSyncObj = new AutoSyncActivity(SurveySummaryReport.this);
                autoSyncObj.callingAutoSync(1);
                syncSurveyEditor = syncSurveyPreferences.edit();
                syncSurveyEditor.putBoolean("sendingSurvey", true);
                syncSurveyEditor.apply();
            } else{
                Toast.makeText(this,getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,getString(R.string.checkNet), Toast.LENGTH_SHORT).show();
        }
    }

    private void LocationSelectionView() {
        syncSurveyEditor = syncSurveyPreferences.edit();

        final String[] items = {"Gps","Agps"};
        DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                language = items[which];
                syncSurveyPreferences.edit();
                if(getString(R.string.agps).equals(language))
                {
                    syncSurveyEditor.putString("LocationSelectionMode", "1");
                    syncSurveyEditor.putInt("SelectedValue", 1);
                    ToastUtils.displayToast(getString(R.string.agpsSelected),SurveySummaryReport.this);
                    alert1.cancel();
                }
                else
                {
                    turnGPSOn();
                    syncSurveyEditor.putString("LocationSelectionMode", "2");
                    syncSurveyEditor.putInt("SelectedValue", 0);
                    ToastUtils.displayToast(getString(R.string.gpsSelected), SurveySummaryReport.this);
                    alert1.cancel();
                }
                syncSurveyEditor.apply();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(SurveySummaryReport.this);
        builder.setTitle(syncSurveyPreferences.getString(ClusterInfo.Location_Selection, ""));
        builder.setSingleChoiceItems(items, syncSurveyPreferences.getInt("SelectedValue", 0), dialogInterface);
        alert1 = builder.create();
        alert1.show();
    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
    public void companyInfo() {
        try {
            Uri uri = Uri.parse(getString(R.string.mahitiURL));
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        } catch (Exception e) {
            Logger.logE(SurveySummaryReport.class.getSimpleName(), "Exception in SyncSurveyActivity  onCreate  method  ", e);
        }
    }
    private class summaryReportSync  extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    summaryReport(prefs.getInt("survey_id", 0));
                }
            });

            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
             if (syncSurveyList.size()>0){
                 syncSurveyListView.setVisibility(View.VISIBLE);
                 setstatusAdapter =new SetSurveyStatus(SurveySummaryReport.this,syncSurveyList);
                 syncSurveyListView.setAdapter(setstatusAdapter);
             }
        }
    }
    public class Myreceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Logger.logD("Myreceiver", "Myreceiver-->");

                summaryReport(prefs.getInt("survey_id", 0));
            } catch (Exception e) {
                Logger.logE(SurveySummaryReport.class.getSimpleName(), "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }


    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, "DataBase_Backup");
        if (!file.exists()) {
            file.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());

        return file.getAbsolutePath() + "/database_1.9_" + currentDateAndTime + ".db";
    }

   /* private static void error(Activity activity) {
        String logData = SendErrorToServer.SendErrorLog();
        SendErrorToServer sendErrorObj = new SendErrorToServer(activity);
        sendErrorObj.SendErrorLog(logData);
    }*/



}
