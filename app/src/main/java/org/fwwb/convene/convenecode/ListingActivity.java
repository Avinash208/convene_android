package org.fwwb.convene.convenecode;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.BeenClass.LanguageBean;
import org.fwwb.convene.convenecode.BeenClass.Question;
import org.fwwb.convene.convenecode.BeenClass.QuestionAnswer;
import org.fwwb.convene.convenecode.BeenClass.StatusBean;
import org.fwwb.convene.convenecode.adapter.spinnercustomadapter.ListingGridViewAdapter;
import org.fwwb.convene.convenecode.api.FilterCallBackInterface;
import org.fwwb.convene.convenecode.database.ConveneDatabaseHelper;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.database.Utilities;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.StartSurvey;
import org.fwwb.convene.convenecode.utils.ToastUtils;
import org.fwwb.convene.convenecode.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fwwb.convene.convenecode.utils.Constants.SURVEY_ID;


public class ListingActivity extends BaseActivity implements View.OnClickListener, FilterCallBackInterface {
    private SharedPreferences sharedPreferences;
    private RecyclerView typeListView;
    ExternalDbOpenHelper dbOpenHelper;

    private LinearLayout backPress;
    private TextView toolbarTitle;
    private String headerName;
    private static final String MY_PREFS_NAME = "MyPrefsMyPrefs";
    public android.database.sqlite.SQLiteDatabase SurveySummaryReportdatabase;
    FloatingActionButton createNewButton;
    Intent intent;
    String beneficiaryTypeId;
    String partnerId;
    TextView emptytextview;
    ImageView imageMenu;
    Myreceiver beneficiryReceiver;
    IntentFilter filter;
    IntentFilter intentFilter;
    Context context;
    private static final String UPDATEBENEICIARY_FLAG = "UpdateBeneficiary";
    private static final String UPDATEFACILITY_FLAG = "UpdateFacility";
    private static final String CHECK_EDITBOOLEAN = "isEditBeneficiary";
    private static final String FACILITY_FLAG = "UpdateFacilityUi";
    private static final String UPDATE_BENEFICIARY_FLAG = "UpdateUi";
    private static final String EDITSECONDARY_ADDRESS_FLAG = "editSecondaryAddress";
    private static final String FROM_USER_DETAILS_FLAG = "fromUserDetails";
    private static final String BENEFICIARY_POPUP_FLAG = "BENEFICIARY_POPUP";
    private static final String FACILITY_POPUP_FLAG = "FACILITY_POPUP";
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
    private int surveyUUIDKEY = 0;
    private String surveyId = "0";
    private ImageView filterButton;
    private TextView filterLocationName;
    private FloatingActionButton dynamicFloatingMenu;
    AlertDialog dialog;
    List<LanguageBean> languageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        context = ListingActivity.this;
        initializeViews();

        beneficiryReceiver = new Myreceiver();

        filter = new IntentFilter("BeneficiaryIntentReceiver");
        intentFilter = new IntentFilter("FacilityIntentReceiver");
        createNewButton = findViewById(R.id.createNewButton);
        dbOpenHelper = ExternalDbOpenHelper.getInstance(this, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("uId", ""));
        dbConveneHelper = ConveneDatabaseHelper.getInstance(this, sharedPreferences.getString("CONVENEDB", ""), sharedPreferences.getString("UID", ""));

        Intent i = getIntent();
        headerName = i.getStringExtra(Constants.HEADER_NAME);
        try {
            surveyId = i.getStringExtra(Constants.SURVEY_ID);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SURVEY_ID, Integer.parseInt(surveyId));
            editor.apply();
        } catch (Exception e) {
            Logger.logE(Constants.TAG, "ListingActivity", e);
        }
        callLanguageMethodRunBackground();
        beneficiaryTypeId = i.getStringExtra(Constants.BENEFICIARY_TYPE_ID);
        partnerId = i.getStringExtra("partner_id");
        toolbarTitle.setText(headerName);
        setDefaultPreference();
        qid = dbOpenHelper.getSummaryQid(prefs.getInt(Constants.SURVEY_ID, 0), dbOpenHelper);
        surveySummaryreportdbhandler = new DBHandler(this);
        syncSurveyList = new ArrayList<>();
        syncSurveyListPending = new ArrayList<>();
        syncCompletedPendingListPending = new ArrayList<>();
        syncSurveySyncCompletedList = new ArrayList<>();
        syncSurveySyncPendingList = new ArrayList<>();
        updateSurveyKey(prefs);
        backPress.setOnClickListener(this);
        createNewButton.setOnClickListener(this);
        setVisiablity();
    }
    private void setVisiablity() {
//        if(prefs.getInt(Constants.ADDBUTTON,0)==0){
//            createNewButton.setVisibility(View.GONE);
//        }else{
//
//        }
        createNewButton.setVisibility(View.VISIBLE);
    }
    private void callLanguageMethodRunBackground() {
         new languageThread().run();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    /**
     * @param prefs prefs
     */
    private void updateSurveyKey(SharedPreferences prefs) {
        surveyUUIDKEY = prefs.getInt("survey_id", 0);
    }
    /*method to set the default filter values to sharedPreferences*/
    private void methodToStoreSharedPreference(int which) {
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putInt("DEFAULT_SELECT", which);
        editor.putInt(Constants.SELECTEDLANGUAGE, languageList.get(which).getLanguagecode());
        editor.putString(Constants.SELECTEDLANGUAGELABEL, languageList.get(which).getLanguageName());
        editor.apply();
        ToastUtils.displayToast("Selected "+languageList.get(which).getLanguageName(),this);
    }
    /*method to intialize all the views */
    private void initializeViews() {
        typeListView = findViewById(R.id.typelistview);
        emptytextview = findViewById(R.id.emptytextview);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        backPress = findViewById(R.id.backPress);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        imageMenu = findViewById(R.id.imageMenu);
        filterButton = findViewById(R.id.filter);
        filterLocationName = findViewById(R.id.locationTV);
        filterButton.setOnClickListener(this);
        imageMenu.setOnClickListener(this);
        languageList= new ArrayList<>();
    }
    @Override
    protected void onResume() {
        super.onResume();

        updateFilterLabelsNonMandatory();
        new summaryReportSync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        createNewButton.setOnClickListener(this);
        updateInfoDescription();

    }

    private void updateFilterLabelsNonMandatory() {
        String getFilterRecords = sharedPreferences.getString(Constants.FILTER + "@" + surveyId, "");
        if (!getFilterRecords.isEmpty()) {
            Logger.logD("filterCallBack", getFilterRecords);
            String[] getSelectedLocation = getFilterRecords.split(",");
            if (getSelectedLocation.length > 0) {
                filterLocationName.setText(new StringBuilder().append(getSelectedLocation[getSelectedLocation.length - 1]).toString());
            }
        }
    }

    private void updateInfoDescription() {
        if(!prefs.getString(Constants.DESCRIPTION,"").isEmpty()){
            imageMenu.setVisibility(View.VISIBLE);
        }else{
            imageMenu.setVisibility(View.GONE);
        }
    }

    private void updateFilterLabels() {
        try {
            String getFilterRecords = sharedPreferences.getString(Constants.FILTER + "@" + surveyId, "");
            if (!getFilterRecords.isEmpty()) {
                Logger.logD("filterCallBack", getFilterRecords);
                String[] getSelectedLocation = getFilterRecords.split(",");
                if (getSelectedLocation.length > 0) {
                    filterLocationName.setText(new StringBuilder().append(getSelectedLocation[getSelectedLocation.length - 1]).toString());
                     new updateListAccordingFilter(getSelectedLocation[getSelectedLocation.length - 1]).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }/*else{
                SupportClass supportClass = new SupportClass();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SURVEY_ID, prefs.getInt(SURVEY_ID, 0));
                supportClass.filterSupport(bundle, ListingActivity.this, dbOpenHelper);
            }*/
        } catch (Exception e) {
            Logger.logE("filterCallBack", "on onPause ", e);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(beneficiryReceiver);
        } catch (Exception ex) {
            Logger.logE("Listing Page", " Exception on onPause ", ex);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void filterCallBack() {
        updateFilterLabels();

    }

    /**
     * Myreceiver
     */
    public class Myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ToastUtils.displayToast("internet enabled", context);
                if (Utils.haveNetworkConnection(context)) {
                    ToastUtils.displayToast("internet enabled", context);
                } else {
                    ToastUtils.displayToast("No internet", context);
                }
            } catch (Exception e) {
                Logger.logE(ListingActivity.class.getSimpleName(), "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter:
                SupportClass supportClass = new SupportClass();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SURVEY_ID, prefs.getInt(SURVEY_ID, 0));
                supportClass.filterSupport(bundle, ListingActivity.this, dbOpenHelper);
                break;
            case R.id.backPress:
                onBackPressed();
                break;
            case R.id.createNewButton:
               if (!languageList.isEmpty()){
                 if (languageList.size()<=1){
                     SharedPreferences prefsMY = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
                     Utilities.setSurveyStatus(prefsMY, "new");
                     SharedPreferences prefsTemp = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
                     SharedPreferences.Editor editor = prefsTemp.edit();
                     editor.putInt(SURVEY_ID, Integer.parseInt(surveyId));
                     editor.putString("Survey_tittle", headerName);
                     editor.apply();
                     new StartSurvey(ListingActivity.this, ListingActivity.this, prefs.getInt(SURVEY_ID, 0), prefs.getInt(SURVEY_ID, 0), "", "", "", "", "", null, "", "").execute(); //Chaned by guru removed "village Name"
                     return;
                 }
                   List<String> getLanguageToString= new ArrayList<>();
                   for (int i=0;i<languageList.size();i++){
                       getLanguageToString.add(languageList.get(i).getLanguageName());
                   }
                   showFilterList(getLanguageToString);
               }else{
                   ToastUtils.displayToast("Sorry no Language found to continue!",this);
               }
                break;
            case R.id.imageMenu:
                if (!prefs.getString(Constants.DESCRIPTION,"").isEmpty())
                    createInfoDescriptionDialog(prefs.getString(Constants.DESCRIPTION,""));
                else
                    ToastUtils.displayToast("No detail information ",this);
                break;
            default:
                break;
        }
    }

    private void createInfoDescriptionDialog(String description) {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Activity information").setMessage(description).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();

    }

    /**
     * method to set the filterLogin option in alert dialog for beneficiary and facility
     * @param filterBy - based on list set the alert dialog to show the filterLogin option
     */
    private void showFilterList(final  List<String>  filterBy) {
        if (!filterBy.isEmpty()) {
            DialogInterface.OnClickListener dialogInterface = (dialog, which) -> {
                setAdapters(which);
                dialog.cancel();
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(ListingActivity.this);
            builder.setTitle(getString(R.string.SelectLanguage));
            builder.setSingleChoiceItems(filterBy.toArray(new String[filterBy.size()]),  defaultPreferences.getInt("DEFAULT_SELECT",0), dialogInterface);
            dialog = builder.create();
            dialog.show();


        }else{
            ToastUtils.displayToast("filterLogin is not available",context);
        }
    }
    private void setAdapters(int which) {
        setSortingOrder(which);

        Utilities.setSurveyStatus(prefs, "new");
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(SURVEY_ID, Integer.parseInt(surveyId));
                editor.apply();
                new StartSurvey(ListingActivity.this, ListingActivity.this, prefs.getInt(SURVEY_ID, 0), prefs.getInt(SURVEY_ID, 0), "Village Name", "", "", "", "", null, "", "").execute();

    }

    /**
     * method to set the adapter based on sort selection
     * @param which

     */
    private void setSortingOrder(int which) {
        methodToStoreSharedPreference(which);
    }

    /**
     * setDefaultPreference
     */
    private void setDefaultPreference() {
        SharedPreferences.Editor editor2 = defaultPreferences.edit();
        editor2.putBoolean("HouseholdUpdateApiStatus", false);
        editor2.putBoolean(BENEFICIARY_POPUP_FLAG, false);
        editor2.putBoolean(FACILITY_POPUP_FLAG, false);
        editor2.putBoolean(UPDATEBENEICIARY_FLAG, false);
        editor2.putBoolean(CHECK_EDITBOOLEAN, false);
        editor2.putBoolean("isEditFacility", false);
        editor2.putBoolean(UPDATEFACILITY_FLAG, false);
        editor2.putBoolean(UPDATE_BENEFICIARY_FLAG, false);
        editor2.putBoolean(FACILITY_FLAG, false);
        editor2.putBoolean(EDITSECONDARY_ADDRESS_FLAG, false);
        editor2.putBoolean(FROM_USER_DETAILS_FLAG, false);
        editor2.apply();
    }

    private class summaryReportSync extends AsyncTask {
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
            Logger.logD("-->start time", "ended");
            progress.dismiss();
            setAdapterList();
            if (!syncSurveyList.isEmpty()) {
                typeListView.setVisibility(View.VISIBLE);
                emptytextview.setVisibility(View.GONE);
                GridLayoutManager linearLayoutManager = new GridLayoutManager(ListingActivity.this, 2);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                typeListView.setLayoutManager(linearLayoutManager);
                filterLocationName.setText("");
                setstatusAdapter = new ListingGridViewAdapter(ListingActivity.this, syncSurveyList, qid,
                        dbConveneHelper, surveySummaryreportdbhandler, prefs, sharedPreferences, surveyId, getDynamicLabel(headerName));
                typeListView.setAdapter(setstatusAdapter);
            } else {
                typeListView.setVisibility(View.GONE);
                emptytextview.setText("No " + headerName.toLowerCase()+ " available");
                emptytextview.setVisibility(View.VISIBLE);
            }
        }
    }
    public String getDynamicLabel(String headerName) {
        String headingName = "";
        switch (headerName) {
            case Constants.GROUP:
                createNewButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.convene_toolbar_color)));
                return headerName;
            case Constants.FPO:
                createNewButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.convene_toolbar_color)));
                return headerName;
            case Constants.HOUSEHOLDS:
                createNewButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.convene_toolbar_color)));
                return headerName;
            case Constants.FARMERS:
                createNewButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.convene_toolbar_color)));
                return headerName;
            default:
                headingName= headerName;
                break;
        }
        return headingName;
    }

    private void setAdapterList() {

    }


    /**
     * @param surveyPrimaryKey surveyPrimaryKey
     * @param parentId         parentId
     * @param summaryQIDs      summaryQIDs
     * @return QuestionAnswer
     */
    private List<QuestionAnswer> getParentDetails(String surveyPrimaryKey, int parentId,
                                                  String summaryQIDs) {
        List<QuestionAnswer> questionAnswerList = new ArrayList<>();
        List<String> displayQuestionList = Arrays.asList(summaryQIDs.split(","));
        String question = "";
        if (!displayQuestionList.isEmpty()) {
            for (int i = 0; i < displayQuestionList.size(); i++) {
                Question getQuestionType = dbConveneHelper.getQuestionBean(displayQuestionList.get(i));
                question = dbConveneHelper.getQuestionFromDb(displayQuestionList.get(i), parentId);
                String answer = "";
                if ( ("R").equalsIgnoreCase(getQuestionType.getQuestion()) || ("S").equalsIgnoreCase(getQuestionType.getQuestion())) {
                    answer = surveySummaryreportdbhandler.getAnswerFromQuestionID(displayQuestionList.get(i), surveySummaryreportdbhandler, String.valueOf(surveyPrimaryKey), getQuestionType.getQuestion(), getQuestionType.getQuestionCode());
                    answer = dbConveneHelper.getAnswer(displayQuestionList.get(i), answer, surveyPrimaryKey);
                }else if(("AI").equalsIgnoreCase(getQuestionType.getQuestion())){
                    answer = DBHandler.getAnswerFromQuestionID(displayQuestionList.get(i), surveySummaryreportdbhandler, String.valueOf(surveyPrimaryKey), getQuestionType.getQuestion(),getQuestionType.getQuestionCode());
                }else {
                    answer = DBHandler.getAnswerFromQuestionID(displayQuestionList.get(i), surveySummaryreportdbhandler, String.valueOf(surveyPrimaryKey), getQuestionType.getQuestion(), getQuestionType.getQuestionCode());
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
     * @param survey_id
     * @return
     */
    public int summaryReport(int survey_id) {
        SurveySummaryReportdatabase = dbOpenHelper.getWritableDatabase();
        DBHandler surveySummaryreportdbhandler = new DBHandler(this);
        String pendingQuery = "Select * From Survey where  survey_ids =" + survey_id + " order by start_date desc";
        Logger.logD("pendingQuery", "query->" + pendingQuery);
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
                    if (date.equals("0")) {
                        date = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("start_date"));
                    }
                    specimenId = specimenId + "# Entered on: " + date;
                    String language1 = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("language_id"));
                    section2 = setSummaryReport(pendSurveyStatus, pendSurveyId);
                    List<QuestionAnswer> questionAnswerList = getParentDetails(pendSurveyId, prefs.getInt(Constants.SURVEY_ID, 0), qid);
                    syncSurveyList.add(new StatusBean(specimenId, String.valueOf(pendSurveyStatus), "", section2, language1, "", pendSurveyId, questionAnswerList, parent_form_primaryid));
                    countSurvey = countSurvey + 1;
                } while (cursorPendingSurvey.moveToNext());
                cursorPendingSurvey.close();
            }

            cursorPendingSurvey.close();
            syncSurveyDatabase.close();
            Logger.logV("Listsize", "List size of summery report" + syncSurveyList.size());
        } catch (Exception e) {
            Logger.logE(ListingActivity.class.getSimpleName(), "Exception in SyncSurveyActivity  summaryReport method ", e);
        }
        return pendSurveyStatus;
    }

    private String setSummaryReport(int pendSurveyStatus, String pendSurveyId) {
        return null;
    }


    private class updateListAccordingFilter extends AsyncTask {


        String locationName;
        List<StatusBean> filterList = new ArrayList<>();

        public updateListAccordingFilter(String leastLocationName) {
            locationName = leastLocationName;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            filterList = surveySummaryreportdbhandler.updateList(locationName, surveyId);
            for (int i = 0; i < filterList.size(); i++) {
                List<QuestionAnswer> questionAnswerList = getParentDetails(filterList.get(i).getUuid(), Integer.parseInt(surveyId), qid);
                StatusBean surveysBean = filterList.get(i);
                surveysBean.setQuestionAnswerList(questionAnswerList);
                filterList.set(i,surveysBean);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (!filterList.isEmpty()) {
                typeListView.setVisibility(View.VISIBLE);
                GridLayoutManager linearLayoutManager = new GridLayoutManager(ListingActivity.this, 2);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                typeListView.setLayoutManager(linearLayoutManager);

                setstatusAdapter = new ListingGridViewAdapter(ListingActivity.this, filterList, qid,
                        dbConveneHelper, surveySummaryreportdbhandler, prefs, sharedPreferences, surveyId, getDynamicLabel(headerName));
                typeListView.setAdapter(setstatusAdapter);
            } else {
                typeListView.setVisibility(View.GONE);
                emptytextview.setText("No " + headerName.toLowerCase()+ " available");
                emptytextview.setVisibility(View.VISIBLE);
            }
        }
    }


    private class languageThread implements   Runnable{
        @Override
        public void run() {
            languageList=dbConveneHelper.getAllRegionalLanguage();
        }
    }
}