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
import org.fwwb.convene.convenecode.adapter.ExpandableListAdapterDataCollection;
import org.fwwb.convene.convenecode.database.DBHandler;
import org.fwwb.convene.convenecode.database.ExternalDbOpenHelper;
import org.fwwb.convene.convenecode.presenter.HomePresenter;
import org.fwwb.convene.convenecode.utils.Constants;
import org.fwwb.convene.convenecode.utils.Logger;
import org.fwwb.convene.convenecode.utils.Utils;
import org.fwwb.convene.convenecode.view.HomeViewInterface;
import org.fwwb.convene.fwwbcode.activities.TaskSelectionListingActivity;

import java.util.HashMap;
import java.util.List;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);
        this.context = HomeActivityNew.this;
        homePresenter = new HomePresenter(this);
        initVariables();
        activity=HomeActivityNew.this;
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

    private void UpdateModuleVisiabliteAgainstProject() {
        if (syncSurveyPreferences.getBoolean(Constants.SHOWTRAININGMODULEFLAG,false)){
            trainingButton.setVisibility(View.VISIBLE);
        }else{
            trainingButton.setVisibility(View.GONE);
        }
    }

    private void checkInternet() {
        if (Utils.haveNetworkConnection(context)) {
            Logger.logD("Online","Enable");
        }else{
            Toast.makeText(getBaseContext(), "Application is working in offline", Toast.LENGTH_SHORT).show();
        }
    }

    public int GetDipsFromPixel(float pixels){
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
    /**
     * initializing all the field values
     */
    private void initVariables() {
        imageMenu =  findViewById(R.id.imageMenu);
        imageMenu.setVisibility(View.GONE);
        TextView toolbarTitle =  findViewById(R.id.toolbarTitle);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        toolbarTitle.setTypeface(customFont);
        toolbarTitle.setText(getString(R.string.Home));
        syncSurveyPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivityNew.this);
        pressBack =  findViewById(R.id.backPress);
        pressBack.setVisibility(View.GONE);
        userNameLabel =  findViewById(R.id.userNameLabel);
        String userName = defaultPreferences.getString("user_name", "");
        userNameLabel.setText(String.format(userName));
        userNameLabel.setTypeface(customFont);
        helpTexts = findViewById(R.id.helptext);
        logOut =  findViewById(R.id.logout);
        dataformsLinear =  findViewById(R.id.dataformsLinear);
        expListView =  findViewById(R.id.expandableListview);
        TextView contentUpdateView =  findViewById(R.id.update_content);
        TextView activityButton =  findViewById(R.id.activity);
        trainingButton =  (CardView) findViewById(R.id.card_view);
        contentUpdateView.setOnClickListener(this);
        activityButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;

    }
    @Override
    public void getExpandableListHeading(List<String> listDataHeader, HashMap<String, List<Datum>> listDataChild) {
        Logger.logD("MVP WORKING", listDataHeader.size() + "");
        listAdapter = new ExpandableListAdapterDataCollection(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
             //  startActivity(new Intent(HomeActivityNew.this, ImageProcessingActivity.class));
                 Utils.callDialogConformation(this,this);
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
            default:
                break;
        }
    }

    private void callTaskSelectionActivity() {
        startActivity(new Intent(activity,TaskSelectionListingActivity.class));
    }

    private void callProjectSelectionActivity() {
        startActivity(new Intent(activity,ProjectSelectionActivity.class));
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

}