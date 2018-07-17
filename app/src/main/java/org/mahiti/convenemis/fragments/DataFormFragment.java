package org.mahiti.convenemis.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.mahiti.convenemis.BeenClass.StatusBean;
import org.mahiti.convenemis.BeenClass.SurveysBean;
import org.mahiti.convenemis.BeenClass.beneficiary.Address;
import org.mahiti.convenemis.BeenClass.beneficiary.Datum;
import org.mahiti.convenemis.ListingActivity;
import org.mahiti.convenemis.R;
import org.mahiti.convenemis.ShowSurveyPreview;
import org.mahiti.convenemis.SurveyQuestionActivity;
import org.mahiti.convenemis.api.BeneficiaryApis.PeriodicTypeInterface;
import org.mahiti.convenemis.api.BeneficiaryApis.ResponseUpdateAPI;
import org.mahiti.convenemis.backgroundcallbacks.PendingCompletedSurveyAsyncResultListener;
import org.mahiti.convenemis.backgroundtasks.CompletedSurveyAsyncTask;
import org.mahiti.convenemis.backgroundtasks.PendingSurveyAsyncTask;
import org.mahiti.convenemis.database.ConveneDatabaseHelper;
import org.mahiti.convenemis.database.DBHandler;
import org.mahiti.convenemis.database.ExternalDbOpenHelper;
import org.mahiti.convenemis.database.ResponseCheckController;
import org.mahiti.convenemis.database.SurveyControllerDbHelper;
import org.mahiti.convenemis.location.GPSTracker;
import org.mahiti.convenemis.utils.AddBeneficiaryUtils;
import org.mahiti.convenemis.utils.AnimationUtils;
import org.mahiti.convenemis.utils.Constants;
import org.mahiti.convenemis.utils.Logger;
import org.mahiti.convenemis.utils.PeriodicityUtils;
import org.mahiti.convenemis.utils.ProgressUtils;
import org.mahiti.convenemis.utils.StartSurvey;
import org.mahiti.convenemis.utils.ToastUtils;
import org.mahiti.convenemis.utils.Utils;
import org.mahiti.convenemis.utils.multispinner.SingleSpinnerSearch;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class DataFormFragment extends Fragment implements PeriodicTypeInterface, PendingCompletedSurveyAsyncResultListener {
    private static final String TAG = "DataFormFragment";
    private static final String ISLOCATIONBASED = "isLocationBased";
    private static final String NOTLOCATIONBASED = "isNotLocationBased";
    TextView emptytextview;
    LinearLayout dynamicDataCollectionForm;
    String btype;
    String beneficiaryType;
    SharedPreferences defaultPreferences;
    String beneficiaryTypeId;

    private ExternalDbOpenHelper externalDbOpenHelper;
    private ConveneDatabaseHelper databaseHelper;
    private DBHandler handler;
    private static final String MY_PREFERENCES = "MyPrefs";
    private static final String BENEFICIARIES_TITLE = "Beneficiaries";
    private static final String SAVE_TO_DRAFT_FLAG_KEY = "SaveDraftButtonFlag";
    private static final String CHECK_CONNECTIVITY = "No internet connection";
    private static final String RECENT_PREVIEW_RECORD = "recentPreviewRecord";
    private static final String SURVEY_ID_KEY = "survey_id";
    List<SurveysBean> surveyList;
    int spinnerCount = 0;
    private String locationName;
    private String beneficiaryArray;
    private String boundaryLevel;
    private String locationId;
    private Datum datum;
    List<Datum> beneficiaryNames = new ArrayList<>();
    List<Datum> facilityNames = new ArrayList<>();
    IntentFilter periodicityFilter;
    private LinearLayout dynamicClosedDataCollectionForm;
    private TextView emptyClosedtextview;
    PeriodicReceiver periodicReceiver;
    private String uuid;
    private GPSTracker gpsTracker;
    private ProgressDialog loginDialog;
    private String address;
    private String syncStatus;
    AddBeneficiaryUtils addBeneficiaryUtils;
    Bundle args;
    SurveyControllerDbHelper periodicityCheckControllerDbHelper;
    ResponseCheckController responseCheckController;
    private List<SurveysBean> pendingSurvey = new ArrayList<>();
    private List<SurveysBean> completedSurveyList = new ArrayList<>();
    private int surveyIdDCF = -1;
    private ProgressBar progressBarDf;
    public TextView selectedLangText;
    private LangReceiver langReceiver;
    private IntentFilter langFilter;
    private String isEditableStr = "isEditable";
    private static final String MY_PREFS_NAME = "MyPrefs";
    private SharedPreferences prefs;
    private net.sqlcipher.database.SQLiteDatabase db;
    private String surveyPrimaryKeyId;
    private int surveysId;
    private static final String SURVEYID = "survey_id";
    private static final String PARENT_FORM_ID = "parent_form_primaryid";
    private int parentID;
    private int parent_form_primaryid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.logD("TimeTest", "@onCreate");
        args = getArguments();
        periodicReceiver = new PeriodicReceiver();
        periodicityFilter = new IntentFilter("Survey");
        langReceiver = new LangReceiver();
        langFilter = new IntentFilter("LangService");
        addBeneficiaryUtils = new AddBeneficiaryUtils(getActivity());
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        setToStringFromBundle(args);
    }


    /**
     * method
     *
     * @param args
     */
    private void setToStringFromBundle(Bundle args) {

        beneficiaryType = defaultPreferences.getString("beneficiary_type", "");
        locationName = defaultPreferences.getString("locationName", "");
        locationId = defaultPreferences.getString("location_id", "");
        boundaryLevel = defaultPreferences.getString("boundary_level", "");
        btype = defaultPreferences.getString("typeName", "");
        surveyIdDCF = defaultPreferences.getInt("surveyIdDCF", -1);
        uuid = defaultPreferences.getString(Constants.UUID, "");
        if ((BENEFICIARIES_TITLE).equals(btype)) {
            beneficiaryTypeId = defaultPreferences.getString("beneficiary_type_id", "");
        } else {
            beneficiaryTypeId = defaultPreferences.getString("facility_type_id", "");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.logD("TimeTest", "@onCreateView");
        View view = inflater.inflate(R.layout.data_collection_fragment, container, false);
        emptytextview = view.findViewById(R.id.emptytextview);
        progressBarDf = view.findViewById(R.id.progressBarDf);
        selectedLangText = view.findViewById(R.id.selectedLangText);
        dynamicDataCollectionForm = view.findViewById(R.id.dynamicDataCollectionForm);
        dynamicClosedDataCollectionForm = view.findViewById(R.id.dynamicClosedDataCollectionForm);
        emptyClosedtextview = view.findViewById(R.id.emptyClosedtextview);
        handler = new DBHandler(getActivity());
        db = handler.getdatabaseinstance();
        Intent surveyPrimaryKeyIntent = getActivity().getIntent();
        if (surveyPrimaryKeyIntent != null) {
            surveyPrimaryKeyId = surveyPrimaryKeyIntent.getStringExtra("SurveyId");
            Logger.logV("surveyPrimaryKeyId", "surveyPrimaryKeyId" + surveyPrimaryKeyId);
        }
        if (surveyPrimaryKeyIntent != null) {
            surveysId = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra(SURVEYID));
            parentID = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra("parentID"));
            parent_form_primaryid = Integer.parseInt(surveyPrimaryKeyIntent.getStringExtra(PARENT_FORM_ID));
        }
        new LoadDbAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        setLanguage();
        return view;
    }

    private void setLanguage() {
        String selectedLang = defaultPreferences.getString(Constants.SELECTEDLANGUAGELABEL, "English");
        selectedLangText.setText(getActivity().getString(R.string.selected_language_str) + selectedLang);
    }


    /**
     * method to display the data collection form for Beneficiary and Facility based on btype
     */
    private void methodToGetTheSurveyList() {

        if (!surveyList.isEmpty()) {
            Logger.logD(TAG, "Survey List is empty" + surveyList.toString());
            emptytextview.setVisibility(View.GONE);
        } else {
            emptytextview.setVisibility(View.VISIBLE);
            emptytextview.setText(getString(R.string.data_collection_not_availabe));
            emptyClosedtextview.setVisibility(View.VISIBLE);
            emptyClosedtextview.setText(getString(R.string.no_completd_records_available));
            Logger.logD(TAG, "Survey List is empty");
        }
        showSurveyForm();
    }

    /**
     * method to get the facility based survey based on facility id in survey table
     */
    private void getTheFacilityBasedForm() {
        surveyList = externalDbOpenHelper.getTypeBasedSurvey("Facility",String.valueOf(surveysId), "facility_ids");
    }


    /**
     * method to get the list of survey based on beneficiary and facility
     */
    private void getBeneficiaryFacilityBasedForm() {
        surveyList = externalDbOpenHelper.getBenAndFacTypeBasedSurvey();
    }


    /**
     * method to get the list of survey based on beneficiary ids
     */
    private void getBeneficiaryBasedSurvey() {
        surveyList = externalDbOpenHelper.getTypeBasedSurvey("Beneficiary", beneficiaryTypeId, "beneficiary_ids");
    }


    /**
     * method to view the survey details
     */
    private void showSurveyForm() {
        if (surveyList.isEmpty())
            return;
        new PendingSurveyAsyncTask(getActivity(), surveyList, externalDbOpenHelper, defaultPreferences, periodicityCheckControllerDbHelper, this, handler, surveyPrimaryKeyId, surveysId).execute();
        new CompletedSurveyAsyncTask(getActivity(), surveyList, externalDbOpenHelper, defaultPreferences, surveyPrimaryKeyId, this, handler).execute();

    }


    /**
     * method
     */
    private void setPendingCardView() {
        dynamicDataCollectionForm.removeAllViews();
        for (int surveyPosition = 0; surveyPosition < pendingSurvey.size(); surveyPosition++) {
            String benAndFaci = "";
            callPendingView(pendingSurvey.get(surveyPosition), benAndFaci);

        }
    }

    /**
     * method
     *
     * @param surveysBean
     * @param benAndFaci
     */
    private void callPendingView(SurveysBean surveysBean, String benAndFaci) {
        createPendingRecordView(surveysBean, benAndFaci);

    }

    @Override
    public void onSuccessPeriodicResponse(String periodicResponse, boolean flag) {
        Logger.logD(TAG, "showSurveyForm ");

    }

    /**
     * @param pendingSurvey pending survey parms
     */
    @Override
    public void pendingSurveys(List<SurveysBean> pendingSurvey) {
        Logger.logD(TAG, "pendingSurvey size" + pendingSurvey.size());
        this.pendingSurvey = pendingSurvey;
        setPendingCardView();
        if (!pendingSurvey.isEmpty())
            emptytextview.setVisibility(View.GONE);
        else
            emptytextview.setVisibility(View.VISIBLE);


    }

    @Override
    public void completedSurveys(List<SurveysBean> completedSurvey) {
        Logger.logD(TAG, "completedSurvey size" + completedSurvey.size());
        this.completedSurveyList = completedSurvey;
        showSummeryReport();
        if (!completedSurveyList.isEmpty())
            emptyClosedtextview.setVisibility(View.GONE);
        else
            emptyClosedtextview.setVisibility(View.VISIBLE);

        progressBarDf.setVisibility(View.GONE);
    }


    /**
     * Receiver Class
     */
    public class PeriodicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                showSurveyForm();
            } catch (Exception e) {
                Logger.logE(ListingActivity.class.getSimpleName(), "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }

    /**
     * Receiver Class
     */
    public class LangReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setLanguage();
            } catch (Exception e) {
                Logger.logE(ListingActivity.class.getSimpleName(), "Exception in SyncSurveyActivity  Myreceiver class  ", e);
            }
        }
    }

    /**
     * creating dynamic view for pending data collection form
     *
     * @param surveysBean
     * @param benAndFaci
     */
    private void createPendingRecordView(SurveysBean surveysBean, String benAndFaci) {
        final View childView = getActivity().getLayoutInflater().inflate(R.layout.data_collection_form, dynamicDataCollectionForm, false);
        final TextView surveyNameTextView = childView.findViewById(R.id.surveyName);
        final TextView periodicityName = childView.findViewById(R.id.periodicityName);
        final TextView addOrCompleteButton = childView.findViewById(R.id.addOrComplete);
        final TextView noteText = childView.findViewById(R.id.noteText);
        final SingleSpinnerSearch spinnerSearch = childView.findViewById(R.id.searchSingleSpinner);
        Typeface customfont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        surveyNameTextView.setTypeface(customfont);
        final String SurveyName = surveysBean.getSurveyName();
        if (surveysBean.getSurveyStatus() == 1) {
            noteText.setVisibility(View.VISIBLE);
            noteText.setText("Note: previous " + surveysBean.getPeriodicityFlag().replace("ly", "").toLowerCase() + " response is pending yet.");
        }
        if (surveysBean.getSurveyStatus() == 1 || surveysBean.getSurveyStatus() == 2) {
            addOrCompleteButton.setText(getString(R.string.pending));
        }

        if (surveysBean.isContinue() == 1)
            addOrCompleteButton.setText("Continue");
        else {
            addOrCompleteButton.setText(getString(R.string.pending));
        }
        spinnerSearch.setTag(spinnerCount);
        if (!"".equalsIgnoreCase(benAndFaci)) {
            spinnerSearch.setVisibility(View.VISIBLE);
            setDefaultAdapter(spinnerSearch);
            addOrCompleteButton.setVisibility(View.GONE);
        } else {
            spinnerSearch.setVisibility(View.GONE);
            addOrCompleteButton.setVisibility(View.VISIBLE);

            addOrCompleteButton.setTextColor(getResources().getColor(R.color.meroon));
        }
        surveyNameTextView.setText(SurveyName);
        periodicityName.setText(surveysBean.getPeriodicityFlag());
        dynamicDataCollectionForm.addView(childView);


        addOrCompleteButton.setOnClickListener(view -> {

            TextView btn = (TextView) view;
            AnimationUtils.viewAnimation(view);
            setToStringFromBundle(args);
            methodToCallAdd(btn.getText().toString(), surveyNameTextView.getText().toString(), surveysBean);
        });

    }


    /**
     * @param btn
     * @param surveyNameTextView
     * @param surveysBean
     */
    private void methodToCallAdd(String btn, String surveyNameTextView, SurveysBean surveysBean) {
        try {
            if (btn.equals(getString(R.string.pending))) {
                addBeneficiaryUtils.setToPreferences(getActivity(), surveyNameTextView, 0, 0, " static Village", beneficiaryArray, defaultPreferences,
                        surveysBean);
                SharedPreferences.Editor editor11 = defaultPreferences.edit();
                editor11.putBoolean(ISLOCATIONBASED, false);
                editor11.putBoolean(NOTLOCATIONBASED, false);
                editor11.putBoolean(SAVE_TO_DRAFT_FLAG_KEY, true);
                editor11.putInt(SURVEY_ID_KEY, surveysBean.getId());
                editor11.apply();
                calligGPS();
                if (!gpsTracker.canGetLocation()) {
                    gpsTracker.showSettingsAlert();
                } else {
                    new StartSurvey(getActivity(), getActivity(), surveysBean.getId(), 0, "Static Village", surveyPrimaryKeyId, boundaryLevel, "", "").execute();
                }

            } else if (("Continue").equalsIgnoreCase(btn)) {
                if (surveysBean.getSurveyStatus() == 1)
                    showPrevSurveyDailog(surveysBean, null, -2);
                SharedPreferences.Editor editorSaveDraft = defaultPreferences.edit();
                editorSaveDraft.putBoolean(SAVE_TO_DRAFT_FLAG_KEY, true);
                editorSaveDraft.putBoolean(ISLOCATIONBASED, false);
                editorSaveDraft.putBoolean(NOTLOCATIONBASED, false);
                editorSaveDraft.apply();
                Intent intent = new Intent(getActivity(), SurveyQuestionActivity.class);
                intent.putExtra("SurveyId", String.valueOf(surveysBean.getSurveyPId()));
                intent.putExtra(SURVEY_ID_KEY, String.valueOf(surveysBean.getId()));
                startActivity(intent);
            } else {
                ToastUtils.displayToast("Periodic limit exceed", getActivity());
            }
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on clock of pending", e);
        }
    }


    /**
     * method
     *
     * @param surveysBean
     * @param jsonArray
     * @param selectedBeneficiaryId
     */
    private void showPrevSurveyDailog(SurveysBean surveysBean, JSONArray jsonArray, int selectedBeneficiaryId) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_dialog);
        TextView text = dialog.findViewById(R.id.labelTextview);
        text.setText(R.string.previous_survey);
        Button dialogBtn_okay = dialog.findViewById(R.id.okButton);
        dialogBtn_okay.setOnClickListener(v -> {
            dialog.cancel();
            if (selectedBeneficiaryId == -2)
                return;
            String previousPeriodLastDate = PeriodicityUtils.getPreviousPeriodLastDate(surveysBean.getPeriodicityFlag());
            if (jsonArray != null)
                new StartSurvey(getActivity(), getActivity(), surveysBean.getId(), Integer.parseInt(locationId), locationName, jsonArray.toString(), boundaryLevel, selectedBeneficiaryId, previousPeriodLastDate).execute();
            else
                new StartSurvey(getActivity(), getActivity(), surveysBean.getId(), Integer.parseInt(locationId), locationName, beneficiaryArray, boundaryLevel, "", previousPeriodLastDate).execute();
        });
        dialog.show();
    }

    /**
     * method
     *
     * @param spinnerSearch
     */
    private void setDefaultAdapter(SingleSpinnerSearch spinnerSearch) {
        List<String> stringList = new ArrayList<>();
        stringList.add(Constants.SELECT);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, stringList);
        spinnerSearch.setAdapter(adapterSpinner);
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            setLanguage();
            setToStringFromBundle(args);
            getActivity().registerReceiver(periodicReceiver, periodicityFilter);
            getActivity().registerReceiver(langReceiver, langFilter);
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on register ", e);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(periodicReceiver);
            getActivity().unregisterReceiver(langReceiver);
        } catch (Exception ex) {
            Logger.logE(TAG, " Exception on onPause ", ex);
        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            Logger.logE(TAG, " Exception on unregisterReceiver ", e);
        }
    }

    /**
     * creating dynamic view for summery report
     */
    private void showSummeryReport() {
        dynamicClosedDataCollectionForm.removeAllViews();
        for (int i = 0; i < completedSurveyList.size(); i++) {
            final View childView = getActivity().getLayoutInflater().inflate(R.layout.activity_summery_report, dynamicClosedDataCollectionForm, false);
            final TextView surveyTextView = childView.findViewById(R.id.surveyTextView);
            final TextView periodicityTextview = childView.findViewById(R.id.periodicityTextview);
            final TextView statusTextView = childView.findViewById(R.id.statusTextView);
            final TextView capturedTextView = childView.findViewById(R.id.capturedTextview);
            Typeface customfont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
            surveyTextView.setTypeface(customfont);
            periodicityTextview.setTypeface(customfont);
            periodicityTextview.setText("Periodicity : " + completedSurveyList.get(i).getPeriodicityFlag());
            surveyTextView.setText(completedSurveyList.get(i).getSurveyName());
            capturedTextView.setText("Captured on : " + completedSurveyList.get(i).getSurveyEndDate());
            if (completedSurveyList.get(i).isSurveyDone() == 1) {
                statusTextView.setText("VIEW");
                onClickFunctionalityVIew(statusTextView, completedSurveyList.get(i).getUuid(), completedSurveyList.get(i).getId());
            } else {
                statusTextView.setText(R.string.edit_or_view);
                onClickFunctionality(statusTextView, surveyPrimaryKeyId, completedSurveyList.get(i).getId());
            }
            dynamicClosedDataCollectionForm.addView(childView);
        }

    }

    private void onClickFunctionalityVIew(TextView statusTextView, String surveyPrimaryKeyId, int id) {
        statusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.logD(TAG, "Clicked Completed Periodicity survey" + surveyPrimaryKeyId);
                Intent startShowSurveyPreview = new Intent(getActivity(), ShowSurveyPreview.class);
                startShowSurveyPreview.putExtra("surveyPrimaryKey", surveyPrimaryKeyId);
                startShowSurveyPreview.putExtra("survey_id", id);
                startShowSurveyPreview.putExtra("visibility", false);
                startActivityForResult(startShowSurveyPreview, 200);
            }
        });

    }

    private void onClickFunctionality(TextView statusTextView, String beneficiaryUuid, int surveysId) {
        statusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parentUUIDExist = handler.getActivityUUID(beneficiaryUuid);
                if (!parentUUIDExist.equals("")) {
                    SharedPreferences.Editor recentPreview = defaultPreferences.edit();
                    recentPreview.putString("recentPreviewRecord", "edit");
                    recentPreview.apply();
                    Logger.logD(TAG, "Clicked Completed Periodicity survey" + surveyPrimaryKeyId);
                    Intent startShowSurveyPreview = new Intent(getActivity(), ShowSurveyPreview.class);
                    startShowSurveyPreview.putExtra("surveyPrimaryKey", parentUUIDExist);
                    startShowSurveyPreview.putExtra("survey_id", surveysId);
                    startShowSurveyPreview.putExtra("visibility", false);
                    startActivityForResult(startShowSurveyPreview, 200);
                } else {
                    Toast.makeText(getActivity(), "Sorry Activity Response not found.!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * method to get the completed records from Periodicity  table and set to view
     *
     * @param getCompletedSurveyList param
     * @param surveyTextView
     * @param statusTextView
     * @param capturedTextView
     * @param periodicityTextview
     */
    private void setCompletedPeriodicityRecords(final List<StatusBean> getCompletedSurveyList, TextView surveyTextView, final TextView statusTextView,
                                                TextView capturedTextView, TextView periodicityTextview, final String uuid, SurveysBean surveysBean) {
        DBHandler handler = new DBHandler(getActivity());
        int surveyPrimaryKey = surveysBean.getId();
        int getSurveyPid = handler.checkPrymaryExist(surveyPrimaryKey, uuid, 0);
        Logger.logD("Gettting primary key", "pi" + getSurveyPid);
        if (getSurveyPid != 0) {
            handler.deleteExistingSurveyRecord(String.valueOf(getSurveyPid), 1);
            handler.deleteExistingSurveyRecord(String.valueOf(getSurveyPid), 2);
        }
        if (!getCompletedSurveyList.isEmpty()) {

            for (int k = 0; k < getCompletedSurveyList.size(); k++) {
                setPreviousFlag(statusTextView, surveysBean.getIsViewOrEdit());
                if (getSurveyPid != 0) {
                    final int finalK = k;
                    statusTextView.setOnClickListener(view -> {
                        view.setTag(getCompletedSurveyList.get(finalK).getPrimaryId());
                        Logger.logD("Clicked Tag", "" + getCompletedSurveyList.get(finalK).getPrimaryId());
                        if (("EDIT/VIEW").equalsIgnoreCase(statusTextView.getText().toString())) {
                            methodToCallEdittedSurveyResponse(surveyPrimaryKey, view);
                            SharedPreferences.Editor recentPreview = defaultPreferences.edit();
                            recentPreview.putString(RECENT_PREVIEW_RECORD, String.valueOf(view.getTag()));
                            recentPreview.putBoolean(isEditableStr, true);
                            recentPreview.apply();
                        } else if (("View").equalsIgnoreCase(statusTextView.getText().toString())) {
                            methodToCallEdittedSurveyResponse(surveyPrimaryKey, view);
                            SharedPreferences.Editor recentPreview = defaultPreferences.edit();
                            recentPreview.putBoolean(isEditableStr, false);
                            recentPreview.putString(RECENT_PREVIEW_RECORD, String.valueOf(view.getTag()));
                            recentPreview.apply();
                        } else {
                            if (Utils.haveNetworkConnection(getActivity())) {
                                callServerTOResponse(view, surveyPrimaryKey);
                                SharedPreferences.Editor recentPreview = defaultPreferences.edit();
                                recentPreview.putBoolean(isEditableStr, true);
                                recentPreview.apply();
                            } else {
                                ToastUtils.displayToast(CHECK_CONNECTIVITY, getActivity());
                            }
                        }
                    });
                } else {
                    final int finalK = k;
                    statusTextView.setOnClickListener(view -> {
                        view.setTag(getCompletedSurveyList.get(finalK).getPrimaryId());
                        Logger.logD("Clicked Tag", "" + getCompletedSurveyList.get(finalK).getPrimaryId());
                        if (("EDIT/VIEW").equalsIgnoreCase(statusTextView.getText().toString())) {
                            if (Utils.haveNetworkConnection(getActivity())) {
                                callServerTOResponse(view, surveyPrimaryKey);
                                SharedPreferences.Editor recentPreview = defaultPreferences.edit();
                                recentPreview.putString(RECENT_PREVIEW_RECORD, String.valueOf(view.getTag()));
                                recentPreview.putBoolean(isEditableStr, true);
                                recentPreview.apply();
                            } else {
                                ToastUtils.displayToast(CHECK_CONNECTIVITY, getActivity());
                            }
                        } else if (("View").equalsIgnoreCase(statusTextView.getText().toString())) {
                            methodToCallEdittedSurveyResponse(surveyPrimaryKey, view);
                            SharedPreferences.Editor recentPreview = defaultPreferences.edit();
                            recentPreview.putString(RECENT_PREVIEW_RECORD, String.valueOf(view.getTag()));
                            recentPreview.putBoolean(isEditableStr, false);
                            recentPreview.apply();
                        } else {
                            if (Utils.haveNetworkConnection(getActivity())) {
                                callServerTOResponse(view, surveyPrimaryKey);
                                SharedPreferences.Editor recentPreview = defaultPreferences.edit();
                                recentPreview.apply();
                            } else {
                                ToastUtils.displayToast(CHECK_CONNECTIVITY, getActivity());
                            }

                        }
                    });
                }
                surveyTextView.setText(getCompletedSurveyList.get(k).getName());
                statusTextView.setTextColor(getResources().getColor(R.color.green));
                capturedTextView.setText(String.format("Captured on : %s", getCompletedSurveyList.get(k).getDate()));
                periodicityTextview.setText(String.format("Periodicity : %s", getCompletedSurveyList.get(k).getLanguage()));
            }
        }

    }


    /**
     * method  to call the response api
     *
     * @param surveyPrimaryKey
     * @param view
     */
    private void methodToCallEdittedSurveyResponse(int surveyPrimaryKey, View view) {
        if (Utils.haveNetworkConnection(getActivity())) {
            callServerTOResponse(view, surveyPrimaryKey);
        } else {
            ToastUtils.displayToast(CHECK_CONNECTIVITY, getActivity());
        }
    }

    /**
     * @param statusTextView
     * @param surveyStatus
     */
    private void setPreviousFlag(TextView statusTextView, int surveyStatus) {
        if (surveyStatus == 2) {
            statusTextView.setText("View");

        } else {
            statusTextView.setText(R.string.edit_or_view);
        }
    }


    /**
     * @param getServerKey
     * @param getSurveyId
     */
    private void callServerTOResponse(View getServerKey, int getSurveyId) {
        try {
            setToStringFromBundle(args);
            new ResponseUpdateAPI(getActivity(), getActivity(), getString(R.string.main_Url) + "api/response-detail/", String.valueOf(getServerKey.getTag()), Integer.parseInt(locationId), locationName, beneficiaryArray, boundaryLevel, getSurveyId).execute();
        } catch (Exception e) {
            Logger.logE(TAG, "Exception on calling response list api", e);
        }
    }


    /**
     * method to get the completed the records from survey table
     *
     * @param getPausedCompletedSurveyList
     * @param statusTextView
     * @param periodicityTextview
     * @param surveyTextView
     * @param capturedTextView
     */
    private void setCompletedRecordsInCard(List<StatusBean> getPausedCompletedSurveyList, TextView statusTextView, TextView periodicityTextview, TextView surveyTextView, TextView capturedTextView,
                                           final int surveyNameID, String uuid) {
        if (!getPausedCompletedSurveyList.isEmpty()) {
            final int getSurveyPid = new DBHandler(getActivity()).checkPrymarySaveDraftExist(surveyNameID, uuid);
            for (int k = 0; k < getPausedCompletedSurveyList.size(); k++) {
                statusTextView.setText(R.string.edit_or_view);
                surveyTextView.setText(String.format("%s( Offline )", getPausedCompletedSurveyList.get(k).getName()));
                statusTextView.setTextColor(getResources().getColor(R.color.green));
                capturedTextView.setText(String.format("Captured on : %s", getPausedCompletedSurveyList.get(k).getDate()));
                periodicityTextview.setText(String.format("Periodicity : %s", getPausedCompletedSurveyList.get(k).getLanguage()));
                statusTextView.setOnClickListener(view -> {
                    SharedPreferences.Editor editorSaveDraft = defaultPreferences.edit();
                    editorSaveDraft.putBoolean(SAVE_TO_DRAFT_FLAG_KEY, true);
                    editorSaveDraft.putString(RECENT_PREVIEW_RECORD, String.valueOf(view.getTag()));
                    editorSaveDraft.apply();
                    Intent intent = new Intent(getActivity(), SurveyQuestionActivity.class);
                    intent.putExtra("SurveyId", String.valueOf(getSurveyPid));
                    Logger.logD("survey_id survey_id-->", "-->" + getSurveyPid);
                    intent.putExtra(SURVEY_ID_KEY, String.valueOf(surveyNameID));
                    startActivity(intent);
                });
            }

        }

    }


    /**
     * method to start the suvvey which is beneficiary and facility based survey and insert values into survey table
     *
     * @param k
     * @param j
     * @param items
     * @param spinnerSearch
     * @param surveysBean
     */
    private void startBeneficiaryFacilitySurvey(int k, int j, List<Datum> items, SingleSpinnerSearch spinnerSearch, SurveysBean surveysBean) {
        if (String.valueOf(k).equals(spinnerSearch.getTag().toString())) {
            calligGPS();
            if (!gpsTracker.canGetLocation()) {
                gpsTracker.showSettingsAlert();
            } else {
                loginDialog = ProgressUtils.showProgress(getActivity(), false, "Loading...");
                loginDialog.show();
                methodToCallStartSurvey(spinnerSearch, j, items, surveysBean);
            }
        }
    }

    /**
     * method to initialize the GPS tracker
     */
    public void calligGPS() {
        gpsTracker = new GPSTracker(getActivity());
    }

    /**
     * method to call start survey asynctask and add the details to survey table
     *
     * @param spinnerSearch
     * @param j
     * @param items
     * @param surveysBean
     */
    private void methodToCallStartSurvey(SingleSpinnerSearch spinnerSearch, int j, List<Datum> items, SurveysBean surveysBean) {
        try {
            String spinnerSelectedValue;
            if (!(Constants.SELECT).equalsIgnoreCase(spinnerSearch.getSelectedItem().toString())) {
                addBeneficiaryUtils.setToPreferences(getActivity(), surveysBean.getSurveyName(), Integer.parseInt(locationId), Integer.parseInt(boundaryLevel), locationName, beneficiaryArray, defaultPreferences, surveysBean);
                Integer selectedBeneficiaryId;
                JSONArray jsonArray = new JSONArray(beneficiaryArray);
                jsonArray.getJSONObject(0).put("fac_uuid", items.get(j).getUuid());

                Logger.logV(TAG, "selecteditem beneficiaryArray==>" + jsonArray);
                if (datum == null) {
                    spinnerSelectedValue = spinnerSearch.getSelectedItem().toString();
                    selectedBeneficiaryId = items.get(j).getBeneficiaryTypeId();
                    if ((1) == (defaultPreferences.getInt(Constants.SELECTEDLANGUAGE, 0))) {
                        boolean isLanguageExist = databaseHelper.checkLanguageExistOrNot(surveysBean.getId(), defaultPreferences.getInt(Constants.SELECTEDLANGUAGE, 0));
                        Logger.logD("isLanguageExist", "-->" + isLanguageExist);
                        if (isLanguageExist) {
                            beneficiaryType = items.get(j).getBeneficiaryType();
                            ToastUtils.displayToastUi(spinnerSelectedValue + " is selected", getActivity());
                            if (surveysBean.getSurveyStatus() == 1)
                                showPrevSurveyDailog(surveysBean, jsonArray, selectedBeneficiaryId);
                            else
                                new StartSurvey(getActivity(), getActivity(), surveysBean.getId(), Integer.parseInt(locationId), locationName, jsonArray.toString(), boundaryLevel, selectedBeneficiaryId, "").execute();

                        } else {
                            Utils.showAlertPopUp(getActivity());
                        }
                    } else {
                        boolean isRegionalLangExist = databaseHelper.checkRegionalLanguageExist(surveysBean.getId(), defaultPreferences.getInt(Constants.SELECTEDLANGUAGE, 0));
                        if (isRegionalLangExist) {

                            beneficiaryType = items.get(j).getBeneficiaryType();
                            ToastUtils.displayToastUi(spinnerSelectedValue + " is selected", getActivity());
                            if (surveysBean.getSurveyStatus() == 1)
                                showPrevSurveyDailog(surveysBean, jsonArray, selectedBeneficiaryId);
                            else
                                new StartSurvey(getActivity(), getActivity(), surveysBean.getId(), Integer.parseInt(locationId), locationName, jsonArray.toString(), boundaryLevel, selectedBeneficiaryId, "").execute();
                        } else {
                            Utils.showAlertPopUp(getActivity());
                        }
                    }
                } else {
                    datum = (Datum) spinnerSearch.getSelectedItem();
                    selectedBeneficiaryId = Integer.parseInt(datum.getBeneficiaryType());
                    beneficiaryType = String.valueOf(datum.getBeneficiaryTypeId());
                    new StartSurvey(getActivity(), getActivity(), surveysBean.getId(), Integer.parseInt(locationId), locationName, beneficiaryArray, boundaryLevel, selectedBeneficiaryId, "").execute();

                }
                ProgressUtils.CancelProgress(loginDialog);
            } else {
                ToastUtils.displayToast("Please select the facilty", getActivity());
            }
        } catch (Exception e) {
            Logger.logE("exception", "", e);
        }
    }

    public class LoadDbAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            periodicityCheckControllerDbHelper = new SurveyControllerDbHelper(getActivity());
            responseCheckController = new ResponseCheckController(getActivity());
            externalDbOpenHelper = ExternalDbOpenHelper.getInstance(getActivity(), defaultPreferences.getString(Constants.DBNAME, ""), defaultPreferences.getString("uId", ""));
            databaseHelper = ConveneDatabaseHelper.getInstance(getActivity(), defaultPreferences.getString(Constants.CONVENE_DB, ""), defaultPreferences.getString("UID", ""));
            surveyList = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(address);
                List<Address> addressList = Utils.getAddressList(jsonArray, syncStatus);
                if (!addressList.isEmpty()) {
                    locationId = String.valueOf(addressList.get(0).getBoundaryId());
                }
            } catch (Exception e) {
                Logger.logE(TAG, "", e);
            }

            getTheFacilityBasedForm();
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            methodToGetTheSurveyList();
        }
    }
}
