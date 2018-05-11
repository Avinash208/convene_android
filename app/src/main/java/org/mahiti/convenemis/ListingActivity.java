package org.mahiti.convenemis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import org.mahiti.convenemis.BeenClass.QuestionAnswer;
import org.mahiti.convenemis.BeenClass.StatusBean;
import org.mahiti.convenemis.BeenClass.SurveysBean;
import org.mahiti.convenemis.BeenClass.beneficiary.Datum;
import org.mahiti.convenemis.BeenClass.facilities.FacilityListInterface;
import org.mahiti.convenemis.adapter.BeneficiaryTypeAdapter;
import org.mahiti.convenemis.adapter.FacilityTypeAdapter;
import org.mahiti.convenemis.api.BeneficiaryApis.BeneficaryTypeInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.FacilitySubTypeInterface;
import org.mahiti.convenemis.api.FacilitiesListAsyncTask;
import org.mahiti.convenemis.api.MeetingAPIs.BeneficiaryAsyncTask;
import org.mahiti.convenemis.backgroundcallbacks.BenificiaryListingCallback;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.Utilities;
import org.mahiti.convenemis.network.ClusterToTypo;
import org.mahiti.convenemis.network.UpdateFilterInterface;
import org.mahiti.convenemis.utils.AddBeneficiaryUtils;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.StartSurvey;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class ListingActivity extends BaseActivity implements View.OnClickListener,
        UpdateFilterInterface, BenificiaryListingCallback {
    String typeValue;
    SharedPreferences sharedPreferences;
    ListView typeListView;
    ExternalDbOpenHelper dbOpenHelper;

    LinearLayout backPress;
    Toolbar toolbar;
    TextView toolbarTitle;
    String headerName;
    private static final String MY_PREFS_NAME = "MyPrefs";
    public android.database.sqlite.SQLiteDatabase SurveySummaryReportdatabase;
    List<Datum> beneficiaryFinalList = new ArrayList<>();
    List<org.mahiti.convenemis.BeenClass.facilities.Datum> facilityFinalList = new ArrayList<>();
    BeneficiaryTypeAdapter typeAdapter;
    FacilityTypeAdapter typeAdapter1;
    FloatingActionButton createNewButton;
    Intent intent;
    String beneficiaryTypeId;
    String partnerId;
    private CharSequence autoSearchText="";
    TextView emptytextview;
    ImageView imageMenu;
    private static final String BENEFICIARIES_TITLE = "Beneficiaries";
    private static final String FACILITIES_TITLE = "Facilities";
    private static final String TAG = "ListingActivity";
    Myreceiver beneficiryReceiver;
    IntentFilter filter;
    IntentFilter intentFilter;
    Context context;
    AlertDialog alert1;
    CustomAutoCompleteTextView myAutoComplete;
    private ImageView resetAutoSearch;
    private Button filterList;
    private Button filterPopUp;
    private static final String UPDATEBENEICIARY_FLAG = "UpdateBeneficiary";
    private static final String UPDATEFACILITY_FLAG = "UpdateFacility";
    private static final String CHECK_EDITBOOLEAN = "isEditBeneficiary";
    private static final String FACILITY_FLAG = "UpdateFacilityUi";
    private static final String UPDATE_BENEFICIARY_FLAG = "UpdateUi";
    private static final String EDITSECONDARY_ADDRESS_FLAG = "editSecondaryAddress";
    private static final String FROM_USER_DETAILS_FLAG = "fromUserDetails";
    private static final String CURRENT_PAGE_FLAG = "SELECTED_PAGE";
    private static final String BENEFICIARYTITLE_FLAG = "BENEFICIARY";
    private static final String BENEFICIARY_POPUP_FLAG = "BENEFICIARY_POPUP";
    private static final String FACILITYTITLE_FLAG = "FACILITY";
    private static final String FACILITY_POPUP_FLAG = "FACILITY_POPUP";
    private String modifiedDate="";
    AddBeneficiaryUtils addBeneficiaryUtils;
    private int surveyIdDCF = -1;
    private SharedPreferences prefs;
    List<Integer> syncSurveySyncCompletedList;
    List<Integer> syncSurveySyncPendingList;
    SQLiteDatabase syncSurveyDatabase;
    List<StatusBean> syncSurveyList;
    List<StatusBean> syncSurveyListPending;
    List<StatusBean> syncCompletedPendingListPending;
    private SetSurveyStatus setstatusAdapter;
    private DBHandler surveySummaryreportdbhandler;
    private String qid;
    private ConveneDatabaseHelper dbConveneHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        context = ListingActivity.this;
        initializeViews();
        hideFilter();
        imageMenu.setVisibility(View.GONE);
        beneficiryReceiver = new Myreceiver();
        addBeneficiaryUtils = new AddBeneficiaryUtils(this);
        filter = new IntentFilter("BeneficiaryIntentReceiver");
        intentFilter = new IntentFilter("FacilityIntentReceiver");
        createNewButton = findViewById(R.id.createNewButton);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        toolbarTitle.setTypeface(customFont);
        dbOpenHelper = ExternalDbOpenHelper.getInstance(this, sharedPreferences.getString(Constants.DBNAME, ""), sharedPreferences.getString("uId", ""));
        dbConveneHelper = ConveneDatabaseHelper.getInstance(this, sharedPreferences.getString("CONVENEDB", ""), sharedPreferences.getString("UID", ""));

        Intent i = getIntent();
        typeValue = i.getStringExtra(Constants.TYPE_VALUE);
        headerName = i.getStringExtra(Constants.HEADER_NAME);
        methodToStoreSharedPreference(2);
        beneficiaryTypeId = i.getStringExtra(Constants.BENEFICIARY_TYPE_ID);
        partnerId = i.getStringExtra("partner_id");
        Logger.logV(TAG, "Beneficiary type id value and partner id ====>" + typeValue + "type id value " + beneficiaryTypeId + " partner id" + partnerId);
        Logger.logV(TAG, "Beneficiary value from Adapter ====>" + typeValue);
        Logger.logV(TAG, "Beneficiary header value from Adapter ====>" + headerName);
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
        myAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Logger.logV(TAG, "beforeTextChanged====>");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (myAutoComplete.isPerformingCompletion()) {
                    return;
                }
                autoSearchText = s;
                getAutoSearchAdapter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Logger.logV(TAG, "afterTextChanged");

            }
        });
        resetAutoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAutoComplete.setText("");
            }
        });

        new  summaryReportSync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        setQuestionTOheading();
        backPress.setOnClickListener(this);
        filterPopUp.setOnClickListener(this);
        createNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new StartSurvey(ListingActivity.this,ListingActivity.this,prefs.getInt(Constants.SURVEY_ID, 0), prefs.getInt(Constants.SURVEY_ID, 0), "Village Name", "", "","", "").execute();
            }
        });



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

    private void hideFilter() {
       /* if (getIntent().getExtras().getInt("surveyIdDCF",-1) != -1 ) {
            filterPopUp.setVisibility(View.GONE);
            filterList.setVisibility(View.GONE);
            surveyIdDCF = getIntent().getExtras().getInt("surveyIdDCF",-1);
        }*/
    }


    /*method to set the default filter values to sharedPreferences*/
    private void methodToStoreSharedPreference(int which) {
        SharedPreferences.Editor editor= defaultPreferences.edit();
        editor.putInt("DEFAULT_SELECT",which);
        editor.apply();
    }

    /**
     * method to get the beneficiary details  and facility details in list based on text changes
     * @param userInput - based on character selection get the list of data from database
     */
    private void getAutoSearchAdapter(CharSequence userInput){
        /*try{
                if(BENEFICIARIES_TITLE.equalsIgnoreCase(headerName)){
                    modifiedDate=dbOpenHelper.getBeneficiaryLastModifiedDate(beneficiaryTypeId,"");
                    List<Datum> getBeneficiaryName= dbOpenHelper.getBeneficiaryNameForFilter(userInput.toString(),beneficiaryTypeId,modifiedDate);
                    Logger.logV(TAG,"getBeneficiaryName"+getBeneficiaryName.size());
                    beneficiaryFinalList.clear();
                    if(getBeneficiaryName.isEmpty()){
                        emptytextview.setVisibility(View.VISIBLE);
                        emptytextview.setText("No Matches Found");
                    }else{
                        emptytextview.setVisibility(View.GONE);
                        setTextViewAdapter(getBeneficiaryName);
                    }
                }else{
                    modifiedDate=dbOpenHelper.getFacilityLastModifiedDate(beneficiaryTypeId,"");
                    List<org.mahiti.convenemis.BeenClass.facilities.Datum> getBeneficiaryName= dbOpenHelper.getFacilityNameForFilter(userInput.toString(),beneficiaryTypeId,modifiedDate);
                    Logger.logV(TAG,"getBeneficiaryName"+getBeneficiaryName.size());
                    facilityFinalList.clear();
                    if(getBeneficiaryName.isEmpty()){
                        emptytextview.setVisibility(View.VISIBLE);
                        emptytextview.setText("No Matches Found");
                    }else{
                        emptytextview.setVisibility(View.GONE);
                        setTextViewAdapterFilter(getBeneficiaryName);
                    }
                }
        }catch (Exception e){
            Logger.logE(TAG,"Exception in the AutoSearch view",e);
        }*/
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
        filterList = findViewById(R.id.sort);
        filterPopUp = findViewById(R.id.filter);
        myAutoComplete = findViewById(R.id.autosearch_names);
        resetAutoSearch = findViewById(R.id.resetautosearch);

    }
    @Override
    protected void onResume() {
        super.onResume();

        if(!"".equals(autoSearchText)){
            getAutoSearchAdapter(autoSearchText);
        }
        registerReceiver(beneficiryReceiver, filter);
        registerReceiver(beneficiryReceiver, intentFilter);
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



    @Override
    public void UpdateFilterAdapter(List<Datum> getSortedList) {
        beneficiaryFinalList.clear();
        if(!getSortedList.isEmpty()) {
            emptytextview.setVisibility(View.GONE);
            typeListView.setVisibility(View.VISIBLE);
            Logger.logV(TAG,"on filter-->"+getSortedList.toString());
            if((sharedPreferences.getString(CURRENT_PAGE_FLAG,"").equalsIgnoreCase(BENEFICIARYTITLE_FLAG)&&(sharedPreferences.getBoolean(UPDATE_BENEFICIARY_FLAG,false)||sharedPreferences.getBoolean(UPDATEBENEICIARY_FLAG, false)))){
                beneficiaryFinalList.clear();
            }
            beneficiaryFinalList.addAll(getSortedList);
            typeAdapter = new BeneficiaryTypeAdapter(ListingActivity.this, beneficiaryFinalList,headerName, surveyIdDCF);
            typeListView.setAdapter(typeAdapter);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(BENEFICIARY_POPUP_FLAG,true);
            editor.apply();
        }else{
            typeListView.setVisibility(View.GONE);
            emptytextview.setText(getString(R.string.check_empty_beneficiary));
            emptytextview.setVisibility(View.VISIBLE);
        }
    }


    /**
     * interface success response,based on sorting selected facility based survey status(Completed/Pending) get the records and update the Listing and UI
     * @param getSortedList list of bean to set the adapter
     */
    @Override
    public void UpdateFilterAdapterFacility(List<org.mahiti.convenemis.BeenClass.facilities.Datum> getSortedList) {
        facilityFinalList.clear();
        if(!getSortedList.isEmpty()) {
            typeListView.setVisibility(View.VISIBLE);
            emptytextview.setVisibility(View.GONE);
            Logger.logV(TAG,"on filter-->"+getSortedList.toString());
            if((sharedPreferences.getBoolean(FACILITY_FLAG,false)||sharedPreferences.getBoolean(UPDATEFACILITY_FLAG, false))&& sharedPreferences.getString(CURRENT_PAGE_FLAG,"").equalsIgnoreCase(FACILITYTITLE_FLAG)){
                facilityFinalList.clear();
            }
            facilityFinalList.addAll(getSortedList);
            typeAdapter1 = new FacilityTypeAdapter(ListingActivity.this, facilityFinalList,headerName, surveyIdDCF);
            typeListView.setAdapter(typeAdapter1);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean(FACILITY_POPUP_FLAG,true);
            editor.apply();
        }else{
            typeListView.setVisibility(View.GONE);
            emptytextview.setText(getString(R.string.check_empty_facility));
            emptytextview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void surveysDetails(List<SurveysBean> pendingSurvey) {


    }


    public class Myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if(Utils.haveNetworkConnection(context)){

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
    }

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
                    summaryReport(prefs.getInt("survey_id", 0));
                }
            });

            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Logger.logD("-->start time","ended");
            if (!syncSurveyList.isEmpty()){
                progress.dismiss();
                typeListView.setVisibility(View.VISIBLE);
                setstatusAdapter =new SetSurveyStatus(ListingActivity.this,syncSurveyList);
                typeListView.setAdapter(setstatusAdapter);
            }else {
                typeListView.setVisibility(View.GONE);
                emptytextview.setVisibility(View.VISIBLE);
            }
        }
    }

    private List<QuestionAnswer> getParentDetails(String surveyPrimaryKey, int parentId,
                                  String summaryQIDs) {
        List<QuestionAnswer> questionAnswerList= new ArrayList<>();
        List<String> displayQuestionList = Arrays.asList(summaryQIDs.split(","));
        String question="";
        if (!displayQuestionList.isEmpty()) {
            for (int i = 0; i < displayQuestionList.size(); i++) {
               // View schemeChildView = getLayoutInflater().inflate(R.layout.scheme_inflate_row_summary, linearLayout, false);//child.xml
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
      //  String pendingQuery = "Select * From Survey where  survey_ids ="+survey_id+" order by sync_date desc";
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
        String typoCodes = "";
        String section1 = "N", section2 = "N", section3 = "N";
        try {
            syncSurveyDatabase = surveySummaryreportdbhandler.getdatabaseinstance_read();
            net.sqlcipher.Cursor cursorPendingSurvey = syncSurveyDatabase.rawQuery(pendingQuery, null);
            if (cursorPendingSurvey != null && cursorPendingSurvey.moveToFirst()) {
                do {
                    pendSurveyStatus = cursorPendingSurvey.getInt(cursorPendingSurvey.getColumnIndex("survey_status"));
                    pendSurveyId = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("uuid"));
                    specimenId = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("cluster_name"));
                    String date = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("end_date"));
                    if(date.equals("0")){
                        date = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("start_date"));
                    }
                    specimenId = specimenId +"# Entered on: "+date;
                    String language1 = cursorPendingSurvey.getString(cursorPendingSurvey.getColumnIndex("language_id"));
                    section2 = setSummaryReport(pendSurveyStatus, pendSurveyId);
                    List<QuestionAnswer> questionAnswerList=  getParentDetails(pendSurveyId,prefs.getInt(Constants.SURVEY_ID, 0),qid);
                    syncSurveyList.add(new StatusBean(specimenId, String.valueOf(pendSurveyStatus), "", section2, language1, "", pendSurveyId,questionAnswerList));
                    countSurvey = countSurvey + 1;
                } while (cursorPendingSurvey.moveToNext());
                cursorPendingSurvey.close();
            }
           // setPendingText();
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

    private class SetSurveyStatus  extends BaseAdapter {

        Context context;
        List<StatusBean> statusbean;
        LinearLayout filledlinearLayout;

        public SetSurveyStatus(ListingActivity surveySummaryReport, List<StatusBean> statusBeanList) {
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
            final ListingActivity.SetSurveyStatus.Viewholder vh;
            View layoutView = convertView;
            if (layoutView == null) {
                vh = new ListingActivity.SetSurveyStatus.Viewholder();
                layoutView = inflater.inflate(R.layout.surveysummary_detail_row, viewGroup, false);
                vh.anniversariesListDymanicLabel= (LinearLayout) layoutView.findViewById(R.id.linearLayout);
                layoutView.setTag(vh);
            } else {
                layoutView = convertView;
                vh = (ListingActivity.SetSurveyStatus.Viewholder) layoutView.getTag();
            }

            if(!"".equals(qid) && qid.contains(","))
            {
                String summary="";
                String[] qids=qid.split(",");
                for(int k=0;k<qids.length;k++)
                {
                    summary=summary + DBHandler.getAnswerFromPreviousQuestion(qids[k],surveySummaryreportdbhandler , String.valueOf(statusbean.get(i).getSurveyId()));
                }
                     String[] getClusterName=statusbean.get(i).getCaseId().split("#");
                Logger.logD("Getting many times","->"+i);
                vh.anniversariesListDymanicLabel.removeAllViews();
                setParentView(vh.anniversariesListDymanicLabel,statusbean.get(i).getQuestionAnswerList(),vh.anniversariesListDymanicLabel);

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

            vh.anniversariesListDymanicLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Toast.makeText(context,"Click"+statusbean.get(i).getSurveyId(),Toast.LENGTH_SHORT).show();
                }
            });
            return layoutView;
        }

        private class Viewholder {
            TextView surveyName;
            TextView schemasTextView;
            TextView tvPart1;
            LinearLayout anniversariesListDymanicLabel ;
            LinearLayout schemeparentlayout ;
        }
    }
     private void setParentView(View schemeChildView,List<QuestionAnswer> questionAnswersList,LinearLayout ll) {
         try {
             for (int i=0;i<questionAnswersList.size();i++){
                 TextView answerText = new TextView(this);
                 answerText.setTextSize(18);
                 answerText.setText(questionAnswersList.get(i).getAnswerText());
                 ll.addView(answerText);

             }
         } catch (Exception e) {
             Logger.logE(TAG,"exception here",e);
         }
     }
}