package org.fwwb.convene.convenecode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.fwwb.convene.R;
import org.fwwb.convene.convenecode.BeenClass.beneficiary.Datum;
import org.fwwb.convene.convenecode.BeenClass.parentChild.SurveyDetail;
import org.fwwb.convene.convenecode.adapter.ExpandableListAdapterDataCollection;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.database.Utilities;
import org.fwwb.convene.convenecode.presenter.HomePresenter;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.StartSurvey;
import org.fwwb.convene.convenecode.utils.Utils;
import org.fwwb.convene.convenecode.view.HomeViewInterface;
import org.fwwb.convene.fwwbcode.activities.TaskSelectionListingActivity;

import java.util.HashMap;
import java.util.List;

import static org.fwwb.convene.convenecode.utils.Constants.SURVEY_ID;

/**
 * COde changes by Avinash Raj
 */
public class HomeActivityNew extends BaseActivity implements View.OnClickListener, HomeViewInterface {

    private long backPressed;
    private static final String TAG = "HomeActivityNew";
    Intent intent;
    ExpandableListAdapterDataCollection listAdapter;
    ExpandableListView expListView;
    ExternalDbOpenHelper dbhelper;
    ImageView imageMenu;
    HashMap<String, List<Datum>> listDataChild;
    TextView helpTexts;
    SharedPreferences syncSurveyPreferences;
    TextView logOut;
    TextView userNameLabel;
    private TextView dueCountLable;
    Context context;
    private LinearLayout pressBack;
    private LinearLayout dataformsLinear;
    HomePresenter homePresenter;
    Activity activity;
    private View currentView;
    private CardView trainingButton;
    private static final String MyPREFERENCES = "MyPrefs";
    private LinearLayout completedSurveyBtn;
    private LinearLayout summerySurveyBtn;
    private LinearLayout pendingSurveyBtn;
    private LinearLayout addNewSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);
        this.context = HomeActivityNew.this;
        homePresenter = new HomePresenter(this);
        initVariables();
        activity = HomeActivityNew.this;
        Logger.logD("Current page is homescreen", "curent page is homescreen");
        DBHandler dbHandler = new DBHandler(this);
        dbhelper = ExternalDbOpenHelper.getInstance(HomeActivityNew.this, defaultPreferences.getString(Constants.DBNAME, ""), defaultPreferences.getString("inv_id", ""));
        listDataChild = new HashMap<>();
        homePresenter.doExpandableListHeadingFunctionality(dbhelper);
        pressBack.setOnClickListener(this);
        helpTexts.setOnClickListener(this);
        logOut.setOnClickListener(this);
        logOut.setOnClickListener(this);
        dataformsLinear.setOnClickListener(this);
        DisplayMetrics metrics;
        int width;
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        expListView.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(10));
        expListView.setGroupIndicator(null);
        expListView.setChildIndicator(null);
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousItem) {
                    expListView.collapseGroup(previousItem);
                }
                previousItem = groupPosition;
            }
        });
        checkInternet();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateModuleVisiabliteAgainstProject();

    }
    private void UpdateViewModulesAgainstProject(HashMap<String, List<Datum>> listDataChild) {
        addNewSurvey = (LinearLayout) findViewById(R.id.add_new_survey);
        LinearLayout dynamicButtonContainer = findViewById(R.id.dynamic_button_container);
        List<Datum> getBeneficiaryList = listDataChild.get("Listing survey");
        for (int i = 0; i < getBeneficiaryList.size(); i++) {
            View inflatParentView = getLayoutInflater().inflate(R.layout.home_button_dynamic, dynamicButtonContainer, false);
            TextView addSurveyText = inflatParentView.findViewById(R.id.survey_name_text);
            Datum getBeanValues = getBeneficiaryList.get(i);
            addSurveyText.setText(new StringBuilder().append(getBeanValues.getName()).append(" List").toString());
            addSurveyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<SurveyDetail> surveyDetail = SurveyListLevels.getSurveyList(context, defaultPreferences.getString(Constants.DBNAME, ""), defaultPreferences.getString("UID", ""), "");
                    SurveyDetail surveyDetailBean;
                    for (int j = 0; j < surveyDetail.size(); j++) {
                        if (getBeanValues.getName().equalsIgnoreCase(surveyDetail.get(j).getSurveyName())) {
                            surveyDetailBean = surveyDetail.get(j);
                            setSharedPreferences(surveyDetailBean, getBeanValues,"1");
                        }
                    }
                }
            });
            addNewSurvey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<SurveyDetail> surveyDetail = SurveyListLevels.getSurveyList(context, defaultPreferences.getString(Constants.DBNAME, ""), defaultPreferences.getString("UID", ""), "");
                    SurveyDetail surveyDetailBean;
                    for (int j = 0; j < surveyDetail.size(); j++) {
                        if (getBeanValues.getName().equalsIgnoreCase(surveyDetail.get(j).getSurveyName())) {
                            surveyDetailBean = surveyDetail.get(j);
                            setSharedPreferences(surveyDetailBean, getBeanValues,"2");
                        }
                    }
                }
            });


            dynamicButtonContainer.addView(inflatParentView);
        }

    }

    private void UpdateModuleVisiabliteAgainstProject() {
        if (syncSurveyPreferences.getBoolean(Constants.SHOWTRAININGMODULEFLAG, false)) {
            trainingButton.setVisibility(View.VISIBLE);
        } else {
            trainingButton.setVisibility(View.GONE);
        }
    }

    private void checkInternet() {
        if (Utils.haveNetworkConnection(context)) {
            Logger.logD("Online", "Enable");
        } else {
            Toast.makeText(getBaseContext(), "Application is working in offline", Toast.LENGTH_SHORT).show();
        }
    }

    public int GetDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    /**
     * initializing all the field values
     */
    private void initVariables() {
        imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setVisibility(View.GONE);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        toolbarTitle.setTypeface(customFont);
        toolbarTitle.setText(getString(R.string.Home));
        syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivityNew.this);
        pressBack = findViewById(R.id.backPress);
        pressBack.setVisibility(View.GONE);
        userNameLabel = findViewById(R.id.userNameLabel);
        String userName = defaultPreferences.getString("user_name", "");
        userNameLabel.setText(String.format(userName));
        userNameLabel.setTypeface(customFont);
        helpTexts = findViewById(R.id.helptext);
        logOut = findViewById(R.id.logout);
        dataformsLinear = findViewById(R.id.dataformsLinear);
        expListView = findViewById(R.id.expandableListview);
        TextView contentUpdateView = findViewById(R.id.update_content);
        TextView activityButton = findViewById(R.id.activity);
        trainingButton = (CardView) findViewById(R.id.card_view);
        completedSurveyBtn = (LinearLayout) findViewById(R.id.completed_survey);
        pendingSurveyBtn = (LinearLayout) findViewById(R.id.pending_survey);
        summerySurveyBtn = (LinearLayout) findViewById(R.id.summery_container);

        contentUpdateView.setOnClickListener(this);
        activityButton.setOnClickListener(this);
        completedSurveyBtn.setOnClickListener(this);
        summerySurveyBtn.setOnClickListener(this);
        pendingSurveyBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;

    }

    @Override
    public void getExpandableListHeading(List<String> listDataHeader, HashMap<String, List<Datum>> listDataChild) {
        if (syncSurveyPreferences.getBoolean(Constants.YALE_PROJECT, false)) {
            UpdateViewModulesAgainstProject(listDataChild);

        } else if (syncSurveyPreferences.getBoolean(Constants.FWWB_PROJECT, false)) {
            Logger.logD("MVP WORKING", listDataHeader.size() + "");
            listAdapter = new ExpandableListAdapterDataCollection(this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                //  startActivity(new Intent(HomeActivityNew.this, ImageProcessingActivity.class));
                Utils.callDialogConformation(this, this);
                break;
            case R.id.update_content:
                Utils.contentUpdateConformation(this);
                break;
            case R.id.activity:
                callProjectSelectionActivity();
                break;
            case R.id.dataformsLinear:
                callTaskSelectionActivity();
                break;
            case R.id.completed_survey:
                startActivity(new Intent(activity, CompletedSurveyActivity.class));
                break;
            case R.id.pending_survey:
                startActivity(new Intent(activity, PendingSurveyActivity.class));
                break;
            case R.id.summery_container:
                Intent summaryIntent = new Intent(activity, SummaryPageActivity.class);
                summaryIntent.putExtra(Constants.SUMMARYSTATUS, "1");
                startActivity(summaryIntent);
                break;
            default:
                break;
        }
    }

    private void callTaskSelectionActivity() {
        startActivity(new Intent(activity, TaskSelectionListingActivity.class));
    }

    private void callProjectSelectionActivity() {
        startActivity(new Intent(activity, ProjectSelectionActivity.class));
    }

    /**
     * method back press from device functionality which will exit from app
     */
    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else
            Toast.makeText(getBaseContext(), getString(R.string.pressToExit), Toast.LENGTH_SHORT).show();
        backPressed = System.currentTimeMillis();
    }

    /**
     * @param surveyDetailBean
     * @param datum
     * @param getActivityFlag 1-> Call Listing page 2-> call New HouseHold create page
     */
    private void setSharedPreferences(SurveyDetail surveyDetailBean, Datum datum, String getActivityFlag) {

        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Logger.logD("-->start time", "checking time line");
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.SURVEY_NAMe, surveyDetailBean.getSurveyName());
        editor.putInt(Constants.FEATURE, surveyDetailBean.getPFeature());
        editor.putInt(Constants.LIMIT, surveyDetailBean.getPLimit());
        editor.putInt(Constants.PERIODICITY, Integer.parseInt(surveyDetailBean.getPiriodicity()));
        editor.putString(Constants.LABEL, surveyDetailBean.getLabels());
        editor.putString(Constants.VERSION, surveyDetailBean.getVn());
        editor.putInt(Constants.CONFIG, (surveyDetailBean.getBConfig()));
        editor.putInt(Constants.RD, surveyDetailBean.getReasonDisagree());
        editor.putString(Constants.DESCRIPTION, surveyDetailBean.getActivityDescription());
        editor.putString(Constants.constraints, surveyDetailBean.getConstraints());

        String[] orderLevels = surveyDetailBean.getOrderLevels().split(",");
        editor.putString(Constants.O_LEAVEL, orderLevels[orderLevels.length - 1]);
        editor.putString(Constants.CODE, surveyDetailBean.getPcode());
        editor.putString(Constants.PROJECTFLOW, "0");
        if (datum.getActive() == 0) {
            editor.putInt(SURVEY_ID, datum.getBeneficiaryTypeId());
            editor.putInt(Constants.SURVEY_ID_HOME, datum.getBeneficiaryTypeId());
            editor.putInt(Constants.ADDBUTTON, 1);
        } else {
            editor.putInt(SURVEY_ID, surveyDetailBean.getSurveyId());
            editor.putInt(Constants.SURVEY_ID_HOME, surveyDetailBean.getSurveyId());
            editor.putInt(Constants.ADDBUTTON, 0);
        }
        editor.putString(Constants.BENEFICIARY_TYPE, surveyDetailBean.getBeneficiaryType());
        editor.putString(Constants.BENEFICIARY_IDS, surveyDetailBean.getBeneficiaryIds());
        editor.putString(Constants.FACILITY_IDS, surveyDetailBean.getFacilityIds());
        editor.putString("Survey_tittle", surveyDetailBean.getSurveyName());
        editor.putString(Constants.SURVEY_NAME_HOME, surveyDetailBean.getSurveyName());
        editor.putInt(Constants.Q_CONFIGS, surveyDetailBean.getQConfig());
        editor.apply();
        Logger.logD("-->start time", "checking time line");
        if (sharedpreferences.getInt(SURVEY_ID, 0) != 0 && getActivityFlag.equalsIgnoreCase("1") ) {
            Intent survrySummaryReport = new Intent(context, ListingActivity.class);
            survrySummaryReport.putExtra(SURVEY_ID, String.valueOf(sharedpreferences.getInt(SURVEY_ID, 0)));
            survrySummaryReport.putExtra(Constants.HEADER_NAME, surveyDetailBean.getBeneficiaryType());
            startActivity(survrySummaryReport);
        }else if (sharedpreferences.getInt(SURVEY_ID, 0) != 0 && getActivityFlag.equalsIgnoreCase("2") ) {
            SharedPreferences prefsMY = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
            Utilities.setSurveyStatus(prefsMY, "new");
            new StartSurvey(activity, activity, sharedpreferences.getInt(SURVEY_ID, 0), sharedpreferences.getInt(SURVEY_ID, 0), "", "", "", "", "", null, "", "").execute(); //Chaned by guru removed "village Name"*/
        }else {
            Intent intent1 = new Intent(this, LocationBasedActivity.class);
            intent1.putExtra(Constants.PERIODICITY, surveyDetailBean.getPiriodicityFlag());
            intent1.putExtra(Constants.P_LIMIT, 1);
            intent1.putExtra("periodicity_count", 1);
            intent1.putExtra(SURVEY_ID, surveyDetailBean.getSurveyId());
            intent1.putExtra("survey_name", surveyDetailBean.getSurveyName());
            intent1.putExtra("benId", "");
            startActivity(intent1);
        }
    }

}