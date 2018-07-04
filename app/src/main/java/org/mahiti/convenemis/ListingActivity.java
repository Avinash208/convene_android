package org.mahiti.convenemis;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import org.mahiti.convenemis.BeenClass.QuestionAnswer;
import org.mahiti.convenemis.BeenClass.StatusBean;
import org.mahiti.convenemis.adapter.spinnercustomadapter.ListingGridViewAdapter;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.Utilities;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.StartSurvey;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mahiti.convenemis.utils.Constants.SURVEY_ID;
import static org.mahiti.convenemis.utils.Constants.constraints;


public class ListingActivity extends BaseActivity implements View.OnClickListener{
    private  String typeValue;
    private  SharedPreferences sharedPreferences;
    private  RecyclerView typeListView;
    ExternalDbOpenHelper dbOpenHelper;

    private LinearLayout backPress;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private String headerName;
    private static final String MY_PREFS_NAME = "MyPrefs";
    public android.database.sqlite.SQLiteDatabase SurveySummaryReportdatabase;
    FloatingActionButton createNewButton;
    Intent intent;
    String beneficiaryTypeId;
    String partnerId;
    TextView emptytextview;
    ImageView imageMenu;
    private static final String TAG = "ListingActivity";
    Myreceiver beneficiryReceiver;
    IntentFilter filter;
    IntentFilter intentFilter;
    Context context;
    CustomAutoCompleteTextView myAutoComplete;
    private Button filterPopUp;
    private static final String UPDATEBENEICIARY_FLAG = "UpdateBeneficiary";
    private static final String UPDATEFACILITY_FLAG = "UpdateFacility";
    private static final String CHECK_EDITBOOLEAN = "isEditBeneficiary";
    private static final String FACILITY_FLAG = "UpdateFacilityUi";
    private static final String UPDATE_BENEFICIARY_FLAG = "UpdateUi";
    private static final String EDITSECONDARY_ADDRESS_FLAG = "editSecondaryAddress";
    private static final String FROM_USER_DETAILS_FLAG = "fromUserDetails";
    private static final String BENEFICIARY_POPUP_FLAG = "BENEFICIARY_POPUP";
    private static final String FACILITY_POPUP_FLAG = "FACILITY_POPUP";
    private int surveyIdDCF = -1;
    private SharedPreferences prefs;
    List<Integer> syncSurveySyncCompletedList;
    List<Integer> syncSurveySyncPendingList;
    SQLiteDatabase syncSurveyDatabase;
    List<StatusBean> syncSurveyList;
    List<StatusBean> syncSurveyListPending;
    List<StatusBean> syncCompletedPendingListPending;
    private ListingGridViewAdapter setstatusAdapter;
    private DBHandler surveySummaryreportdbhandler;
    private String qid;
    private ConveneDatabaseHelper dbConveneHelper;
    private int surveyUUIDKEY=0;
    private String  surveyId="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        context = ListingActivity.this;
        initializeViews();
        imageMenu.setVisibility(View.VISIBLE);
        beneficiryReceiver = new Myreceiver();

        filter = new IntentFilter("BeneficiaryIntentReceiver");
        intentFilter = new IntentFilter("FacilityIntentReceiver");
        createNewButton = findViewById(R.id.createNewButton);
        dbOpenHelper = ExternalDbOpenHelper.getInstance(this, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("uId", ""));
        dbConveneHelper = ConveneDatabaseHelper.getInstance(this, sharedPreferences.getString("CONVENEDB", ""), sharedPreferences.getString("UID", ""));

        Intent i = getIntent();
        typeValue = i.getStringExtra(Constants.TYPE_VALUE);
        headerName = i.getStringExtra(Constants.HEADER_NAME);

        try {
            surveyId = i.getStringExtra(Constants.SURVEY_ID);
            SharedPreferences.Editor editor= prefs.edit();
            editor.putInt(SURVEY_ID, Integer.parseInt(surveyId));
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        methodToStoreSharedPreference(2);
        beneficiaryTypeId = i.getStringExtra(Constants.BENEFICIARY_TYPE_ID);
        partnerId = i.getStringExtra("partner_id");
        toolbarTitle.setText(headerName);
        setDefaultPreference();
        qid=dbOpenHelper.getSummaryQid(prefs.getInt(Constants.SURVEY_ID, 0),dbOpenHelper);
        surveySummaryreportdbhandler = new DBHandler(this);
        myAutoComplete.setText("");
        syncSurveyList = new ArrayList<>();
        syncSurveyListPending = new ArrayList<>();
        syncCompletedPendingListPending = new ArrayList<>();
        syncSurveySyncCompletedList = new ArrayList<>();
        syncSurveySyncPendingList = new ArrayList<>();
        updateSurveyKey(prefs);
        new  summaryReportSync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        backPress.setOnClickListener(this);
        filterPopUp.setOnClickListener(this);
        createNewButton.setOnClickListener(this);
    }

    /**
     * @param prefs prefs
     */
    private void updateSurveyKey(SharedPreferences prefs) {
       surveyUUIDKEY= prefs.getInt("survey_id", 0);
    }
    /*method to set the default filter values to sharedPreferences*/
    private void methodToStoreSharedPreference(int which) {
        SharedPreferences.Editor editor= defaultPreferences.edit();
        editor.putInt("DEFAULT_SELECT",which);
        editor.apply();
    }
    /*method to intialize all the views */
    private void initializeViews() {
        typeListView= findViewById(R.id.typelistview);
        emptytextview= findViewById(R.id.emptytextview);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        backPress= findViewById(R.id.backPress);
        toolbar= findViewById(R.id.toolbar1);
        toolbarTitle= findViewById(R.id.toolbarTitle);
        imageMenu= findViewById(R.id.imageMenu);
        filterPopUp = findViewById(R.id.filter);
        myAutoComplete = findViewById(R.id.autosearch_names);

    }
    @Override
    protected void onResume() {
        super.onResume();
        new  summaryReportSync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        createNewButton.setOnClickListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(beneficiryReceiver);
        }catch (Exception ex){
            Logger.logE("Listing Page"," Exception on onPause ",ex);
        }
    }

    /**
     * Myreceiver
     */
    public class Myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ToastUtils.displayToast("internet enabled",context);
                if(Utils.haveNetworkConnection(context)){
                    ToastUtils.displayToast("internet enabled",context);
                }else{
                    ToastUtils.displayToast("No internet",context);
                }
            } catch (Exception e) {
                Logger.logE(ListingActivity.class.getSimpleName(), "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.backPress){
            onBackPressed();
        }
        if (view.getId()==R.id.createNewButton){
            Utilities.setSurveyStatus(sharedPreferences,"new");
            SharedPreferences.Editor editor= prefs.edit();
            editor.putInt(SURVEY_ID, Integer.parseInt(surveyId));
            editor.apply();
            new StartSurvey(ListingActivity.this,ListingActivity.this,prefs.getInt(SURVEY_ID,0), prefs.getInt(SURVEY_ID,0), "Village Name", "", "","", "").execute();
        }
    }

    /**
     * setDefaultPreference
     */
    private void setDefaultPreference() {
        SharedPreferences.Editor editor2=defaultPreferences.edit();
        editor2.putBoolean("HouseholdUpdateApiStatus",false);
        editor2.putBoolean(BENEFICIARY_POPUP_FLAG,false);
        editor2.putBoolean(FACILITY_POPUP_FLAG,false);
        editor2.putBoolean(UPDATEBENEICIARY_FLAG, false);
        editor2.putBoolean(CHECK_EDITBOOLEAN,false);
        editor2.putBoolean("isEditFacility",false);
        editor2.putBoolean(UPDATEFACILITY_FLAG,false);
        editor2.putBoolean(UPDATE_BENEFICIARY_FLAG,false);
        editor2.putBoolean(FACILITY_FLAG,false);
        editor2.putBoolean(EDITSECONDARY_ADDRESS_FLAG,false);
        editor2.putBoolean(FROM_USER_DETAILS_FLAG,false);
        editor2.apply();
    }
    private class summaryReportSync  extends AsyncTask {
        ProgressDialog progress = new ProgressDialog(ListingActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Loading...");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(true);
            progress.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    summaryReport(surveyUUIDKEY);
                }
            });

            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Logger.logD("-->start time","ended");
            progress.dismiss();
            if (!syncSurveyList.isEmpty()){
                typeListView.setVisibility(View.VISIBLE);
                GridLayoutManager linearLayoutManager = new GridLayoutManager(ListingActivity.this,2);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                typeListView.setLayoutManager(linearLayoutManager);
                setstatusAdapter =new ListingGridViewAdapter(ListingActivity.this,syncSurveyList,qid,
                        dbConveneHelper,surveySummaryreportdbhandler,prefs,sharedPreferences,surveyId,getDynamicLabel(headerName));
                typeListView.setAdapter(setstatusAdapter);
            }else {
                typeListView.setVisibility(View.GONE);
                emptytextview.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getDynamicLabel(String headerName) {
        String headingName="";
        switch (headerName){
            case Constants.GROUP:
                return headerName;
            case Constants.FPO:
                return headerName;
            case Constants.HOUSEHOLDS:
                return headerName;
            default:
                break;
        }
        return headingName;
    }

    /**
     * @param surveyPrimaryKey surveyPrimaryKey
     * @param parentId  parentId
     * @param summaryQIDs summaryQIDs
     * @return QuestionAnswer
     */
    private List<QuestionAnswer> getParentDetails(String surveyPrimaryKey, int parentId,
                                  String summaryQIDs) {
        List<QuestionAnswer> questionAnswerList= new ArrayList<>();
        List<String> displayQuestionList = Arrays.asList(summaryQIDs.split(","));
        String question="";
        if (!displayQuestionList.isEmpty()) {
            for (int i = 0; i < displayQuestionList.size(); i++) {
                String getQuestionType = dbConveneHelper.getQuestionType(displayQuestionList.get(i));
                question = dbConveneHelper.getQuestionFromDb(displayQuestionList.get(i), parentId);
                String answer = "";
                if (getQuestionType.equalsIgnoreCase("R") || getQuestionType.equalsIgnoreCase("S")) {
                    answer = surveySummaryreportdbhandler.getAnswerFromQuestionID(displayQuestionList.get(i), surveySummaryreportdbhandler, String.valueOf(surveyPrimaryKey), getQuestionType);
                    answer = dbConveneHelper.getAnswer(displayQuestionList.get(i), answer, surveyPrimaryKey);
                } else {
                    answer = DBHandler.getAnswerFromQuestionID(displayQuestionList.get(i), surveySummaryreportdbhandler, String.valueOf(surveyPrimaryKey), getQuestionType);
                }

                QuestionAnswer filledBean = new QuestionAnswer();
                filledBean.setQuestionText(question);
                filledBean.setAnswerText(answer);
                filledBean.setParentId(parentId);
                questionAnswerList.add(filledBean);
            }

        }
        return questionAnswerList;
    }

    /**
     *
     * @param survey_id
     * @return
     */
    public int summaryReport(int survey_id) {
        SurveySummaryReportdatabase = dbOpenHelper.getWritableDatabase();
        DBHandler surveySummaryreportdbhandler = new DBHandler(this);
        String pendingQuery = "Select * From Survey where  survey_ids ="+survey_id+" order by start_date desc";
        Logger.logD("pendingQuery","query->" + pendingQuery);
        int pendSurveyStatus = 0;
        String pendSurveyId = "";
        String specimenId = "";
        int countSurvey = 0;
        syncSurveySyncCompletedList.clear();
        syncSurveySyncPendingList.clear();
        syncSurveyList.clear();
        syncSurveyListPending.clear();
        syncCompletedPendingListPending.clear();
        String section1 = "N", section2 = "N", section3 = "N";
        try {
            syncSurveyDatabase = surveySummaryreportdbhandler.getdatabaseinstance_read();
            net.sqlcipher.Cursor cursorPendingSurvey = syncSurveyDatabase.rawQuery(pendingQuery, null);
            if (cursorPendingSurvey != null && cursorPendingSurvey.moveToFirst()) {
                do {
                    pendSurveyStatus = cursorPendingSurvey.getInt(cursorPendingSurvey.getColumnIndex("survey_status"));
                    pendSurveyId = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("uuid"));
                    specimenId = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("cluster_name"));
                  int parent_form_primaryid = cursorPendingSurvey.getInt(cursorPendingSurvey.getColumnIndex("server_primary_key"));
                    String date = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("end_date"));
                    if(date.equals("0")){
                        date = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("start_date"));
                    }
                    specimenId = specimenId +"# Entered on: "+date;
                    String language1 = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("language_id"));
                    section2 = setSummaryReport(pendSurveyStatus, pendSurveyId);
                    List<QuestionAnswer> questionAnswerList=  getParentDetails(pendSurveyId,prefs.getInt(Constants.SURVEY_ID, 0),qid);
                    syncSurveyList.add(new StatusBean(specimenId, String.valueOf(pendSurveyStatus), "", section2, language1, "", pendSurveyId,questionAnswerList,parent_form_primaryid));
                    countSurvey = countSurvey + 1;
                } while (cursorPendingSurvey.moveToNext());
                cursorPendingSurvey.close();
            }

            cursorPendingSurvey.close();
            syncSurveyDatabase.close();
            Logger.logV("Listsize","List size of summery report" + syncSurveyList.size());
        } catch (Exception e) {
            Logger.logE(SurveySummaryReport.class.getSimpleName(), "Exception in SyncSurveyActivity  summaryReport method ", e);
        }
        return pendSurveyStatus;
    }

    private String setSummaryReport(int pendSurveyStatus, String pendSurveyId) {
        return null;
    }

    private void setQuestionTOheading() {
        TextView questionDisplayDynamictextView = findViewById(R.id.question_display);
        String getQuestion;
        List<String> displayQuestionList = Arrays.asList(qid.split(","));
        if (!displayQuestionList.isEmpty()) {
            for (int i = 0; i < displayQuestionList.size(); i++) {
                String question = dbConveneHelper.getQuestionFromDb(displayQuestionList.get(i), prefs.getInt("survey_id", 0));
                if (i==0)
                    questionDisplayDynamictextView.setText(questionDisplayDynamictextView.getText()+question);
                else if ((displayQuestionList.size()-1)==i)
                    questionDisplayDynamictextView.setText(questionDisplayDynamictextView.getText()+" and "+question);
                else
                    questionDisplayDynamictextView.setText(questionDisplayDynamictextView.getText()+", "+question);
            }
        }
    }


}